package cl.inacap.comederoiot

import android.content.Context
import android.os.SystemClock
import cl.inacap.comederoiot.enlace.Enlace
import cl.inacap.comederoiot.enlace.EstadoEnlace
import cl.inacap.comederoiot.enlace.Mensaje
import cl.inacap.comederoiot.enlace.MonitorWifi
import cl.inacap.comederoiot.enlace.WifiEnlace
import cl.inacap.comederoiot.seguridad.Cripto
import java.net.InetAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class RolApp { NODO, PANEL }

data class EstadoSesion(
    val rol: RolApp? = null,
    val enlace: EstadoEnlace = EstadoEnlace.DESCONECTADO,
    val ipLocal: String? = null,
    val rasgunos: Int = 0,
    val comedero: String = "CERRADO",
    val ultimaEntrega: String = "—",
    val notificacionesHabilitadas: Boolean = true,
    val aviso: String? = null,
    val error: String? = null
)

/** Coordina pantallas, contador, enlace y entrega sin ejecutar esperas en el hilo de interfaz. */
object Sesion {
    private const val VENTANA_MS = 5_000L
    private const val ENTREGA_MS = 1_500L
    private const val LATIDO_MS = 2_000L
    private const val TIEMPO_SIN_LATIDO_MS = 6_000L
    private val alcance = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _estado = MutableStateFlow(EstadoSesion())
    private var contexto: Context? = null
    private var enlace: Enlace? = null
    private var rol: RolApp? = null
    private var ventana = VentanaRasgunos(VENTANA_MS)
    private var trabajoVentana: Job? = null
    private var trabajoEntrega: Job? = null
    private var trabajoLatido: Job? = null
    private var trabajoEstado: Job? = null
    private var trabajoMensajes: Job? = null
    private var generacionVentana = 0
    private var generacionSesion = 0
    private var ultimoMensajeValidoMs = 0L
    private var monitorWifi: MonitorWifi? = null

    val estado = _estado.asStateFlow()

    fun actualizarPermisoNotificaciones(contexto: Context) {
        _estado.value = _estado.value.copy(
            notificacionesHabilitadas = Notificaciones.permisoHabilitado(contexto)
        )
    }

    fun iniciarNodo(contexto: Context, codigo: String): String? {
        if (!Cripto.codigoValido(codigo)) return "El código debe tener exactamente 6 dígitos."
        val ip = WifiEnlace.ipLocal()
            ?: return "Conecta este teléfono a la red Wi-Fi o al hotspot antes de iniciar el Nodo."

        val generacion = preparar(contexto, RolApp.NODO, ip)
        alcance.launch {
            try {
                val clave = withContext(Dispatchers.Default) { Cripto.claveDesdeCodigo(codigo) }
                if (generacion != generacionSesion) return@launch
                val nuevoEnlace = WifiEnlace(clave)
                enlace = nuevoEnlace
                monitorWifi = MonitorWifi(contexto) { notificarPerdidaDeRed() }.also { it.iniciar() }
                observarEnlace(nuevoEnlace, RolApp.NODO)
                _estado.value = _estado.value.copy(enlace = EstadoEnlace.ESPERANDO)
                nuevoEnlace.iniciarNodo()
            } catch (_: Exception) {
                if (generacion == generacionSesion) {
                    _estado.value = _estado.value.copy(
                        enlace = EstadoEnlace.ERROR,
                        error = "No se pudo preparar el cifrado del enlace."
                    )
                }
            }
        }
        return null
    }

    fun conectarPanel(contexto: Context, ip: String, codigo: String): String? {
        if (!Cripto.codigoValido(codigo)) return "El código debe tener exactamente 6 dígitos."
        if (!esIpv4(ip.trim())) return "Escribe una dirección IPv4 válida del Nodo."

        val generacion = preparar(contexto, RolApp.PANEL, null)
        alcance.launch {
            try {
                val clave = withContext(Dispatchers.Default) { Cripto.claveDesdeCodigo(codigo) }
                if (generacion != generacionSesion) return@launch
                val nuevoEnlace = WifiEnlace(clave)
                enlace = nuevoEnlace
                monitorWifi = MonitorWifi(contexto) { notificarPerdidaDeRed() }.also { it.iniciar() }
                observarEnlace(nuevoEnlace, RolApp.PANEL)
                _estado.value = _estado.value.copy(enlace = EstadoEnlace.CONECTANDO)
                nuevoEnlace.conectarPanel(ip.trim())
            } catch (_: Exception) {
                if (generacion == generacionSesion) {
                    _estado.value = _estado.value.copy(
                        enlace = EstadoEnlace.ERROR,
                        error = "No se pudo preparar el cifrado del enlace."
                    )
                }
            }
        }
        return null
    }

    private fun preparar(contextoNuevo: Context, nuevoRol: RolApp, ipLocal: String?): Int {
        detenerEnlace()
        generacionSesion += 1
        contexto = contextoNuevo.applicationContext
        rol = nuevoRol
        ventana = VentanaRasgunos(VENTANA_MS)
        _estado.value = EstadoSesion(
            rol = nuevoRol,
            enlace = EstadoEnlace.DESCONECTADO,
            ipLocal = ipLocal,
            notificacionesHabilitadas = Notificaciones.permisoHabilitado(contextoNuevo)
        )
        return generacionSesion
    }

    private fun observarEnlace(nuevoEnlace: Enlace, rolConexion: RolApp) {
        var estadoAnterior = nuevoEnlace.estado.value
        trabajoEstado = alcance.launch {
            nuevoEnlace.estado.collect { nuevoEstado ->
                _estado.value = _estado.value.copy(
                    enlace = nuevoEstado,
                    aviso = if (nuevoEstado == EstadoEnlace.CONECTADO) null else _estado.value.aviso,
                    error = if (nuevoEstado == EstadoEnlace.ERROR) {
                        "No se pudo iniciar o conectar. Revisa la red, la IP y el puerto 5050."
                    } else null
                )

                if (nuevoEstado != EstadoEnlace.CONECTADO) {
                    trabajoLatido?.cancel()
                    trabajoLatido = null
                }

                if (estadoAnterior == EstadoEnlace.CONECTADO && nuevoEstado != EstadoEnlace.CONECTADO) {
                    volverAEstadoSeguro()
                    _estado.value = _estado.value.copy(
                        aviso = "Enlace perdido. Comedero cerrado y solicitud reiniciada."
                    )
                    Notificaciones.mostrar(
                        contexto ?: return@collect,
                        "Enlace perdido",
                        "El comedero quedó cerrado y la solicitud se reinició."
                    )
                } else if (estadoAnterior != EstadoEnlace.CONECTADO &&
                    nuevoEstado == EstadoEnlace.CONECTADO
                ) {
                    iniciarLatido(nuevoEnlace, rolConexion)
                    if (rolConexion == RolApp.NODO) enviarEstadoInicial()
                }
                estadoAnterior = nuevoEstado
            }
        }

        trabajoMensajes = alcance.launch {
            nuevoEnlace.entrantes.collect { mensaje ->
                if (rolConexion == RolApp.PANEL) {
                    ultimoMensajeValidoMs = SystemClock.elapsedRealtime()
                }
                when (rolConexion) {
                    RolApp.NODO -> if (mensaje.permitidoParaNodo()) procesarComando(mensaje)
                    RolApp.PANEL -> if (mensaje.permitidoParaPanel()) procesarLectura(mensaje)
                }
            }
        }
    }

    private fun enviarEstadoInicial() {
        alcance.launch {
            if (enviarYEsperar(Mensaje("LECTURA", "rasgunos", "0"))) {
                enviarYEsperar(Mensaje("LECTURA", "comedero", "CERRADO"))
            }
        }
    }

    /** El Nodo publica la lectura existente cada 2 s; el Panel corta tras 6 s sin datos válidos. */
    private fun iniciarLatido(actual: Enlace, rolConexion: RolApp) {
        trabajoLatido?.cancel()
        if (rolConexion == RolApp.NODO) {
            trabajoLatido = alcance.launch {
                while (_estado.value.enlace == EstadoEnlace.CONECTADO) {
                    delay(LATIDO_MS)
                    if (_estado.value.enlace != EstadoEnlace.CONECTADO) break
                    if (!enviarYEsperar(
                            Mensaje("LECTURA", "rasgunos", _estado.value.rasgunos.toString())
                        )
                    ) break
                }
            }
        } else {
            ultimoMensajeValidoMs = SystemClock.elapsedRealtime()
            trabajoLatido = alcance.launch {
                while (_estado.value.enlace == EstadoEnlace.CONECTADO) {
                    delay(LATIDO_MS)
                    if (_estado.value.enlace != EstadoEnlace.CONECTADO) break
                    val sinMensajes = SystemClock.elapsedRealtime() - ultimoMensajeValidoMs
                    if (sinMensajes >= TIEMPO_SIN_LATIDO_MS) {
                        _estado.value = _estado.value.copy(
                            aviso = "Sin lecturas válidas por 6 segundos. Cerrando enlace por seguridad."
                        )
                        actual.cerrarConexionActual()
                        break
                    }
                }
            }
        }
    }

    fun simularRasgun() {
        if (rol != RolApp.NODO || _estado.value.enlace != EstadoEnlace.CONECTADO) return

        val resultado = ventana.registrar(SystemClock.elapsedRealtime())
        if (resultado.ignorado) return

        _estado.value = _estado.value.copy(rasgunos = resultado.cantidad, error = null)
        enviar(Mensaje("LECTURA", "rasgunos", resultado.cantidad.toString()))

        if (resultado.confirmada) {
            generacionVentana += 1
            trabajoVentana?.cancel()
            trabajoVentana = null
        } else if (resultado.cantidad == 1) {
            programarVencimiento()
        }
    }

    private fun programarVencimiento() {
        trabajoVentana?.cancel()
        val miGeneracion = ++generacionVentana
        trabajoVentana = alcance.launch {
            delay(VENTANA_MS)
            if (miGeneracion == generacionVentana &&
                _estado.value.enlace == EstadoEnlace.CONECTADO &&
                ventana.vencer(SystemClock.elapsedRealtime())
            ) {
                _estado.value = _estado.value.copy(rasgunos = 0)
                enviar(Mensaje("LECTURA", "rasgunos", "0"))
            }
        }
    }

    fun dispensarComida() {
        if (rol != RolApp.PANEL || _estado.value.enlace != EstadoEnlace.CONECTADO) return
        if (_estado.value.rasgunos != 3 || _estado.value.comedero != "CERRADO") return
        enviar(Mensaje("CMD", "comida", "DISPENSAR"))
    }

    private fun procesarComando(mensaje: Mensaje) {
        if (!mensaje.permitidoParaNodo() || !ventana.solicitudPendiente) return
        if (_estado.value.comedero != "CERRADO" || trabajoEntrega?.isActive == true) return

        _estado.value = _estado.value.copy(comedero = "DISPENSANDO")
        enviar(Mensaje("LECTURA", "comedero", "DISPENSANDO"))
        Notificaciones.mostrar(
            contexto ?: return,
            "Comando de alimentación recibido",
            "El comedero se abrirá durante aproximadamente 1,5 segundos."
        )

        trabajoEntrega = alcance.launch {
            delay(ENTREGA_MS)
            if (_estado.value.enlace != EstadoEnlace.CONECTADO) return@launch

            _estado.value = _estado.value.copy(comedero = "CERRADO")
            if (!enviarYEsperar(Mensaje("LECTURA", "comedero", "CERRADO"))) return@launch
            if (!enviarYEsperar(Mensaje("ACK", "comida", "DISPENSADA"))) return@launch

            // La solicitud se libera solo después de cerrar y confirmar la entrega.
            ventana.reiniciar()
            generacionVentana += 1
            _estado.value = _estado.value.copy(rasgunos = 0, comedero = "CERRADO")
            enviar(Mensaje("LECTURA", "rasgunos", "0"))
        }
    }

    private fun procesarLectura(mensaje: Mensaje) {
        when {
            mensaje.tipo == "LECTURA" && mensaje.clave == "rasgunos" -> {
                val cantidad = mensaje.valor.toIntOrNull() ?: return
                val anterior = _estado.value.rasgunos
                _estado.value = _estado.value.copy(rasgunos = cantidad)
                if (anterior < 3 && cantidad == 3) {
                    Notificaciones.mostrar(
                        contexto ?: return,
                        "Solicitud de alimento",
                        "El gato está solicitando comida"
                    )
                }
            }
            mensaje.tipo == "LECTURA" && mensaje.clave == "comedero" -> {
                _estado.value = _estado.value.copy(comedero = mensaje.valor)
            }
            mensaje.tipo == "ACK" && mensaje.clave == "comida" && mensaje.valor == "DISPENSADA" -> {
                _estado.value = _estado.value.copy(
                    rasgunos = 0,
                    comedero = "CERRADO",
                    ultimaEntrega = "confirmada"
                )
            }
        }
    }

    private fun enviar(mensaje: Mensaje) {
        alcance.launch { enviarYEsperar(mensaje) }
    }

    private suspend fun enviarYEsperar(mensaje: Mensaje): Boolean {
        val actual = enlace ?: return false
        val enviado = actual.enviar(mensaje)
        if (!enviado && _estado.value.enlace == EstadoEnlace.CONECTADO) {
            actual.cerrarConexionActual()
        }
        return enviado
    }

    /** Se invoca cuando Android informa que se perdió la red Wi-Fi usada por el socket. */
    fun notificarPerdidaDeRed() {
        if (_estado.value.enlace == EstadoEnlace.CONECTADO) enlace?.cerrarConexionActual()
    }

    fun desconectar() {
        detenerEnlace()
        generacionSesion += 1
        rol = null
        contexto = null
        ventana = VentanaRasgunos(VENTANA_MS)
        _estado.value = EstadoSesion(notificacionesHabilitadas = _estado.value.notificacionesHabilitadas)
    }

    private fun detenerEnlace() {
        trabajoVentana?.cancel()
        trabajoEntrega?.cancel()
        trabajoLatido?.cancel()
        trabajoLatido = null
        trabajoEstado?.cancel()
        trabajoMensajes?.cancel()
        generacionVentana += 1
        enlace?.cerrar()
        enlace = null
        monitorWifi?.detener()
        monitorWifi = null
        ventana.reiniciar()
    }

    private fun volverAEstadoSeguro() {
        trabajoEntrega?.cancel()
        trabajoEntrega = null
        trabajoVentana?.cancel()
        trabajoVentana = null
        generacionVentana += 1
        ventana.reiniciar()
        _estado.value = _estado.value.copy(rasgunos = 0, comedero = "CERRADO")
    }

    private fun esIpv4(ip: String): Boolean {
        val partes = ip.trim().split('.')
        if (partes.size != 4) return false
        return partes.all { parte ->
            parte.isNotEmpty() && parte.all { it in '0'..'9' } &&
                parte.toIntOrNull()?.let { it in 0..255 } == true
        }
    }
}
