package cl.inacap.comederoiot.seguridad

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/** Cifrado de mensajes de la Formativa con APIs criptográficas incluidas en Android/JVM. */
object Cripto {
    private const val ITERACIONES = 120_000
    private const val BITS_CLAVE = 256
    private const val BYTES_IV = 12
    private const val BITS_ETIQUETA = 128
    private const val MAX_TAMANO_CIFRADO = 256
    private val aleatorio = SecureRandom()

    // Sal fija de contexto tomada del ejemplo de la guía. No es una contraseña ni un secreto.
    private val salProyecto = "MonitorIoT-v1".toByteArray(Charsets.UTF_8)

    fun codigoValido(codigo: String): Boolean =
        codigo.length == 6 && codigo.all { it in '0'..'9' }

    /** PBKDF2-HMAC-SHA256 convierte el código compartido en una clave AES de 256 bits. */
    fun claveDesdeCodigo(codigo: String): SecretKey {
        require(codigoValido(codigo)) { "El código debe tener exactamente 6 dígitos." }
        val caracteres = codigo.toCharArray()
        val spec = PBEKeySpec(caracteres, salProyecto, ITERACIONES, BITS_CLAVE)
        return try {
            val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).encoded
            SecretKeySpec(bytes, "AES")
        } finally {
            spec.clearPassword()
            caracteres.fill('\u0000')
        }
    }

    /** Base64(IV aleatorio + ciphertext y etiqueta GCM). */
    fun cifrar(texto: String, clave: SecretKey): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, clave, aleatorio)
        val iv = cipher.iv
        require(iv.size == BYTES_IV) { "Android devolvió un IV GCM inesperado." }
        val cifrado = cipher.doFinal(texto.toByteArray(Charsets.UTF_8))
        val paquete = ByteArray(iv.size + cifrado.size)
        iv.copyInto(paquete)
        cifrado.copyInto(paquete, destinationOffset = iv.size)
        return Base64.getEncoder().encodeToString(paquete)
    }

    /** Devuelve null ante Base64 inválido, código distinto, truncamiento o datos alterados. */
    fun descifrar(linea: String, clave: SecretKey): String? {
        return try {
            if (linea.isEmpty() || linea.length > MAX_TAMANO_CIFRADO) return null
            val paquete = Base64.getDecoder().decode(linea)
            if (paquete.size < BYTES_IV + BITS_ETIQUETA / 8) return null

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val parametros = GCMParameterSpec(BITS_ETIQUETA, paquete, 0, BYTES_IV)
            cipher.init(Cipher.DECRYPT_MODE, clave, parametros)
            String(cipher.doFinal(paquete, BYTES_IV, paquete.size - BYTES_IV), Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }
}
