package cl.inacap.comederoiot

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import cl.inacap.comederoiot.ui.PantallaConexion
import cl.inacap.comederoiot.ui.PantallaLogin
import cl.inacap.comederoiot.ui.PantallaNodo
import cl.inacap.comederoiot.ui.PantallaPanel
import cl.inacap.comederoiot.seguridad.AutenticacionFirebase

class MainActivity : ComponentActivity() {
    private lateinit var pedirNotificaciones: ActivityResultLauncher<String>
    private lateinit var autenticacion: AutenticacionFirebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        autenticacion = AutenticacionFirebase(this)
        pedirNotificaciones = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            Sesion.actualizarPermisoNotificaciones(this)
        }
        Notificaciones.crearCanal(this)

        setContent {
            val estado by Sesion.estado.collectAsState()
            var pantalla by rememberSaveable {
                mutableStateOf(if (autenticacion.haySesionActiva) "conexion" else "login")
            }

            MaterialTheme(colorScheme = lightColorScheme()) {
                BackHandler(enabled = pantalla == "nodo" || pantalla == "panel") {
                    Sesion.desconectar()
                    pantalla = "conexion"
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { relleno ->
                    val contenido = Modifier.padding(relleno)
                    when (pantalla) {
                        "login" -> PantallaLogin(
                            firebaseConfigurado = autenticacion.estaConfigurada,
                            onIniciarSesion = autenticacion::iniciarSesion,
                            onLoginCorrecto = { pantalla = "conexion" },
                            modifier = contenido
                        )
                        "nodo" -> PantallaNodo(
                            estado = estado,
                            onSimularRasgun = Sesion::simularRasgun,
                            onDesconectar = {
                                Sesion.desconectar()
                                pantalla = "conexion"
                            },
                            modifier = contenido
                        )
                        "panel" -> PantallaPanel(
                            estado = estado,
                            onDispensar = Sesion::dispensarComida,
                            onVolverAConexion = {
                                Sesion.desconectar()
                                pantalla = "conexion"
                            },
                            modifier = contenido
                        )
                        "conexion" -> PantallaConexion(
                            estado = estado,
                            onIniciarNodo = { codigo ->
                                val error = Sesion.iniciarNodo(this, codigo)
                                if (error == null) pantalla = "nodo"
                                error
                            },
                            onConectarPanel = { ip, codigo ->
                                val error = Sesion.conectarPanel(this, ip, codigo)
                                if (error == null) pantalla = "panel"
                                error
                            },
                            onSolicitarNotificaciones = ::solicitarPermisoNotificaciones,
                            onCerrarSesion = {
                                Sesion.desconectar()
                                autenticacion.cerrarSesion()
                                pantalla = "login"
                            },
                            modifier = contenido
                        )
                        else -> PantallaLogin(
                            firebaseConfigurado = autenticacion.estaConfigurada,
                            onIniciarSesion = autenticacion::iniciarSesion,
                            onLoginCorrecto = { pantalla = "conexion" },
                            modifier = contenido
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Sesion.actualizarPermisoNotificaciones(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // El usuario puede cambiar el permiso desde Ajustes o el panel rápido
        // mientras esta Activity sigue abierta; actualizamos el aviso al volver.
        if (hasFocus) Sesion.actualizarPermisoNotificaciones(this)
    }

    private fun solicitarPermisoNotificaciones() {
        if (Build.VERSION.SDK_INT < 33) {
            Sesion.actualizarPermisoNotificaciones(this)
            return
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Sesion.actualizarPermisoNotificaciones(this)
        } else {
            pedirNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
