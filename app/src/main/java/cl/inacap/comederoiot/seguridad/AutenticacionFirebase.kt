package cl.inacap.comederoiot.seguridad

import android.content.Context
import android.util.Patterns
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

/** Autenticación por correo y contraseña con Firebase Authentication. */
class AutenticacionFirebase(context: Context) {
    private val app = FirebaseApp.initializeApp(context.applicationContext)
    private val auth = app?.let(FirebaseAuth::getInstance)

    val estaConfigurada: Boolean
        get() = auth != null

    val haySesionActiva: Boolean
        get() = auth?.currentUser != null

    fun iniciarSesion(usuario: String, contrasena: String, alTerminar: (String?) -> Unit) {
        val correo = usuario.trim()
        val instancia = auth

        if (instancia == null) {
            alTerminar("Firebase aún no está configurado. Sigue los pasos de docs/FIREBASE_LOGIN.md.")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            alTerminar("Escribe el correo electrónico registrado como usuario.")
            return
        }
        if (contrasena.isBlank()) {
            alTerminar("Escribe tu contraseña.")
            return
        }

        instancia.signInWithEmailAndPassword(correo, contrasena)
            .addOnCompleteListener { tarea ->
                if (tarea.isSuccessful) {
                    alTerminar(null)
                } else {
                    alTerminar(mensajeDeError(tarea.exception))
                }
            }
    }

    fun cerrarSesion() {
        auth?.signOut()
    }

    private fun mensajeDeError(error: Exception?): String {
        val codigo = (error as? FirebaseAuthException)?.errorCode.orEmpty()
        return when (codigo) {
            "ERROR_INVALID_EMAIL" -> "El correo electrónico no tiene un formato válido."
            "ERROR_USER_DISABLED" -> "Esta cuenta está deshabilitada."
            "ERROR_TOO_MANY_REQUESTS" -> "Hubo demasiados intentos. Espera un momento y vuelve a probar."
            "ERROR_NETWORK_REQUEST_FAILED" -> "No se pudo conectar con Firebase. Revisa Internet e inténtalo otra vez."
            "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD", "ERROR_INVALID_CREDENTIAL",
            "INVALID_LOGIN_CREDENTIALS" -> "Usuario o contraseña incorrectos."
            else -> "No se pudo iniciar sesión. Revisa tus datos y la conexión a Internet."
        }
    }
}
