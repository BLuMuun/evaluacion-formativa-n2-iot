package cl.inacap.comederoiot.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.inacap.comederoiot.EstadoSesion
import cl.inacap.comederoiot.enlace.EstadoEnlace

@Composable
fun PantallaPanel(
    estado: EstadoSesion,
    onDispensar: () -> Unit,
    onVolverAConexion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text("Panel de control", style = MaterialTheme.typography.headlineMedium)
        EstadoTarjeta("Enlace", estado.enlace.etiqueta)
        estado.aviso?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        EstadoTarjeta("Rasguños", "${estado.rasgunos} / 3")
        EstadoTarjeta("Comedero", estado.comedero)

        Button(
            enabled = estado.enlace == EstadoEnlace.CONECTADO &&
                estado.comedero == "CERRADO" && estado.rasgunos == 3,
            onClick = onDispensar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dispensar comida")
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Última entrega", style = MaterialTheme.typography.titleSmall)
            Text(estado.ultimaEntrega, style = MaterialTheme.typography.bodyLarge)
        }

        if (!estado.notificacionesHabilitadas) {
            Text("Notificaciones deshabilitadas; conexión y control siguen disponibles.")
        }
        OutlinedButton(onClick = onVolverAConexion, modifier = Modifier.fillMaxWidth()) {
            Text(if (estado.enlace == EstadoEnlace.CONECTADO) "Desconectar" else "Volver a Conexión")
        }
    }
}
