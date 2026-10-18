package cl.inacap.comederoiot.seguridad

import java.util.Base64
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CriptoTest {
    @Test
    fun mismoCodigoDescifraYCadaMensajeUsaIvAleatorio() {
        val clave = Cripto.claveDesdeCodigo("123456")
        val primero = Cripto.cifrar("LECTURA;rasgunos;2", clave)
        val segundo = Cripto.cifrar("LECTURA;rasgunos;2", clave)

        assertEquals("LECTURA;rasgunos;2", Cripto.descifrar(primero, clave))
        assertNotEquals("El IV debe ser aleatorio por mensaje", primero, segundo)
    }

    @Test
    fun otroCodigoODatosAlteradosSeDescartan() {
        val original = Cripto.cifrar("CMD;comida;DISPENSAR", Cripto.claveDesdeCodigo("111111"))
        assertNull(Cripto.descifrar(original, Cripto.claveDesdeCodigo("222222")))

        val paquete = Base64.getDecoder().decode(original)
        paquete[paquete.lastIndex] = (paquete.last().toInt() xor 1).toByte()
        val alterado = Base64.getEncoder().encodeToString(paquete)
        assertNull(Cripto.descifrar(alterado, Cripto.claveDesdeCodigo("111111")))
    }

    @Test
    fun codigoDebeTenerSeisDigitos() {
        listOf("12345", "1234567", "12a456", "１２３４５６").forEach {
            assertNullIfInvalidCode(it)
        }
    }

    private fun assertNullIfInvalidCode(codigo: String) {
        org.junit.Assert.assertFalse(Cripto.codigoValido(codigo))
    }
}
