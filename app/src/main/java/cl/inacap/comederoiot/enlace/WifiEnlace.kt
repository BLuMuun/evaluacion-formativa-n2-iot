package cl.inacap.comederoiot.enlace

import cl.inacap.comederoiot.seguridad.Cripto
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.crypto.SecretKey

/** Servidor TCP para Nodo y cliente TCP para Panel. Solo transporta líneas cifradas. */
class WifiEnlace(private val clave: SecretKey) : Enlace {
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _estado = MutableStateFlow(EstadoEnlace.DESCONECTADO)
    private val _entrantes = MutableSharedFlow<Mensaje>(extraBufferCapacity = 32)
    private val cerrojoEnvio = Mutex()

    @Volatile private var servidor: ServerSocket? = null
    @Volatile private var socket: Socket? = null
    @Volatile private var modoNodo = false
    @Volatile private var cerrado = false

    override val estado = _estado.asStateFlow()
    override val entrantes = _entrantes.asSharedFlow()

    override fun iniciarNodo() {
        if (_estado.value != EstadoEnlace.DESCONECTADO || cerrado) return
        modoNodo = true
        _estado.value = EstadoEnlace.ESPERANDO

        alcance.launch {
            try {
                val server = ServerSocket(PUERTO)
                servidor = server
                while (isActive && !cerrado) {
                    _estado.value = EstadoEnlace.ESPERANDO
                    val cliente = server.accept()
                    if (cerrado) {
                        cliente.close()
                        break
                    }
                    atender(cliente, paraNodo = true)
                }
            } catch (_: CancellationException) {
                throw CancellationException()
            } catch (_: IOException) {
                if (!cerrado) _estado.value = EstadoEnlace.ERROR
            } finally {
                runCatching { servidor?.close() }
                servidor = null
                if (cerrado) _estado.value = EstadoEnlace.DESCONECTADO
            }
        }
    }

    override fun conectarPanel(ipNodo: String) {
        if (_estado.value != EstadoEnlace.DESCONECTADO || cerrado) return
        modoNodo = false
        _estado.value = EstadoEnlace.CONECTANDO

        alcance.launch {
            val cliente = Socket()
            try {
                cliente.tcpNoDelay = true
                cliente.keepAlive = true
                cliente.connect(InetSocketAddress(ipNodo, PUERTO), TIEMPO_ESPERA_MS)
                if (cerrado) {
                    cliente.close()
                    return@launch
                }
                atender(cliente, paraNodo = false)
            } catch (_: CancellationException) {
                throw CancellationException()
            } catch (_: Exception) {
                if (!cerrado) _estado.value = EstadoEnlace.ERROR
                runCatching { cliente.close() }
            }
        }
    }

    private suspend fun atender(cliente: Socket, paraNodo: Boolean) {
        socket = cliente
        cliente.tcpNoDelay = true
        cliente.keepAlive = true
        _estado.value = EstadoEnlace.CONECTADO
        try {
            val entrada = cliente.getInputStream()
            while (alcance.isActive && !cerrado && !cliente.isClosed) {
                val linea = leerLineaLimitada(entrada) ?: break
                val texto = Cripto.descifrar(linea, clave) ?: continue
                val mensaje = Mensaje.desdeLinea(texto) ?: continue
                val direccionValida = if (paraNodo) {
                    mensaje.permitidoParaNodo()
                } else {
                    mensaje.permitidoParaPanel()
                }
                if (direccionValida) _entrantes.emit(mensaje)
            }
        } catch (_: CancellationException) {
            throw CancellationException()
        } catch (_: IOException) {
            // EOF o error de red: el estado cambia y el Nodo vuelve a esperar al Panel.
        } finally {
            runCatching { cliente.close() }
            if (socket === cliente) socket = null
            if (!cerrado) {
                _estado.value = if (modoNodo) EstadoEnlace.ESPERANDO else EstadoEnlace.DESCONECTADO
            }
        }
    }

    override suspend fun enviar(mensaje: Mensaje): Boolean = cerrojoEnvio.withLock {
        withContext(Dispatchers.IO) {
            val cliente = socket ?: return@withContext false
            try {
                val linea = Cripto.cifrar(mensaje.aLinea(), clave)
                if (linea.length > MAX_LINEA) return@withContext false
                cliente.getOutputStream().write((linea + "\n").toByteArray(Charsets.UTF_8))
                cliente.getOutputStream().flush()
                true
            } catch (_: Exception) {
                runCatching { cliente.close() }
                if (socket === cliente) socket = null
                if (!cerrado) {
                    _estado.value = if (modoNodo) EstadoEnlace.ESPERANDO else EstadoEnlace.DESCONECTADO
                }
                false
            }
        }
    }

    /** Corta la sesión TCP; el servidor Nodo queda escuchando para una reconexión del Panel. */
    override fun cerrarConexionActual() {
        runCatching { socket?.close() }
        socket = null
        if (!cerrado) {
            _estado.value = if (modoNodo) EstadoEnlace.ESPERANDO else EstadoEnlace.DESCONECTADO
        }
    }

    override fun cerrar() {
        cerrado = true
        runCatching { socket?.close() }
        runCatching { servidor?.close() }
        socket = null
        servidor = null
        alcance.cancel()
        _estado.value = EstadoEnlace.DESCONECTADO
    }

    companion object {
        const val PUERTO = 5050
        private const val TIEMPO_ESPERA_MS = 5_000
        private const val MAX_LINEA = 256

        /** Recibe únicamente una IP local IPv4; devuelve null si el teléfono no está en Wi-Fi. */
        fun ipLocal(): String? = try {
            NetworkInterface.getNetworkInterfaces()?.toList()
                ?.filter {
                    it.isUp && !it.isLoopback &&
                        (it.name.startsWith("wlan") || it.name.startsWith("ap") ||
                            it.name.startsWith("swlan"))
                }
                ?.flatMap { it.inetAddresses.toList() }
                ?.filterIsInstance<Inet4Address>()
                ?.firstOrNull { it.isSiteLocalAddress && !it.isLoopbackAddress }
                ?.hostAddress
        } catch (_: Exception) {
            null
        }

        /** Lee hasta el salto de línea y limita memoria incluso si llega una trama enorme. */
        private fun leerLineaLimitada(entrada: java.io.InputStream): String? {
            val bytes = ByteArrayOutputStream()
            var excedida = false
            while (true) {
                val siguiente = entrada.read()
                if (siguiente == -1) return null
                if (siguiente == '\n'.code) {
                    if (excedida) return ""
                    val trama = bytes.toByteArray()
                    val largo = if (trama.lastOrNull() == '\r'.code.toByte()) trama.size - 1 else trama.size
                    return String(trama, 0, largo, Charsets.UTF_8)
                }
                if (!excedida) {
                    if (bytes.size() >= MAX_LINEA) {
                        excedida = true
                    } else {
                        bytes.write(siguiente)
                    }
                }
            }
        }
    }
}
