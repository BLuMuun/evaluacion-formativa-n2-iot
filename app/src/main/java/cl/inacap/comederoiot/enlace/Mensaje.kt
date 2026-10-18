package cl.inacap.comederoiot.enlace

/** Mensaje de aplicación una vez descifrado. Solo admite el protocolo de la Formativa. */
data class Mensaje(val tipo: String, val clave: String, val valor: String) {
    fun aLinea(): String = "$tipo;$clave;$valor"

    fun permitidoParaNodo(): Boolean =
        tipo == "CMD" && clave == "comida" && valor == "DISPENSAR"

    fun permitidoParaPanel(): Boolean = when {
        tipo == "LECTURA" && clave == "rasgunos" -> valor in RASGUNOS
        tipo == "LECTURA" && clave == "comedero" -> valor in ESTADOS_COMEDERO
        tipo == "ACK" && clave == "comida" -> valor == "DISPENSADA"
        else -> false
    }

    companion object {
        private const val MAX_CAMPO = 16
        private val RASGUNOS = setOf("0", "1", "2", "3")
        private val ESTADOS_COMEDERO = setOf("CERRADO", "DISPENSANDO")

        /** Devuelve null para cualquier tipo, combinación o campo que no pertenezca al protocolo. */
        fun desdeLinea(linea: String): Mensaje? {
            if (linea.isEmpty() || linea.length > 41 || linea.any(Char::isISOControl)) return null

            val campos = linea.split(';', limit = 4)
            if (campos.size != 3) return null

            val (tipo, clave, valor) = campos
            if (tipo.isEmpty() || clave.isEmpty() || valor.isEmpty()) return null
            if (clave.length > MAX_CAMPO || valor.length > MAX_CAMPO) return null

            val mensaje = Mensaje(tipo, clave, valor)
            return mensaje.takeIf { it.permitidoParaNodo() || it.permitidoParaPanel() }
        }
    }
}
