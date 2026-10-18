package cl.inacap.comederoiot.ui

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import cl.inacap.comederoiot.EstadoSesion
import cl.inacap.comederoiot.RolApp
import cl.inacap.comederoiot.enlace.WifiEnlace

@Composable
fun PantallaConexion(
    estado: EstadoSesion,
    onIniciarNodo: (String) -> String?,
    onConectarPanel: (String, String) -> String?,
    onSolicitarNotificaciones: () -> Unit,
    modifier: Modifier = Modifier
) {
    var rol by rememberSaveable { mutableStateOf(RolApp.NODO) }
    var codigo by rememberSaveable { mutableStateOf("") }
    var ipNodo by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    var ipLocal by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(rol) {
        if (rol == RolApp.NODO) {
            ipLocal = withContext(Dispatchers.IO) { WifiEnlace.ipLocal() }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Comedero IoT", style = MaterialTheme.typography.headlineMedium)
        Text("Conexión · Unidad 2 Formativa", style = MaterialTheme.typography.titleMedium)

        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Rol", style = MaterialTheme.typography.titleSmall)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = rol == RolApp.NODO,
                        onClick = { rol = RolApp.NODO; error = null },
                        label = { Text("Nodo") }
                    )
                    FilterChip(
                        selected = rol == RolApp.PANEL,
                        onClick = { rol = RolApp.PANEL; error = null },
                        label = { Text("Panel") }
                    )
                }
                Text("Medio: Wi-Fi local · TCP ${WifiEnlace.PUERTO}")
            }
        }

        OutlinedTextField(
            value = codigo,
            onValueChange = { nuevo ->
                codigo = nuevo.filter { it in '0'..'9' }.take(6)
                error = null
            },
            label = { Text("Código de vinculación") },
            supportingText = { Text("Ingresa los mismos 6 dígitos en ambos teléfonos.") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            modifier = Modifier.fillMaxWidth()
        )

        if (rol == RolApp.NODO) {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("IP de este teléfono", style = MaterialTheme.typography.titleSmall)
                    Text(ipLocal ?: "Sin IP Wi-Fi disponible")
                    Text("El Panel debe usar esta IP en la misma red o hotspot.",
                        style = MaterialTheme.typography.bodySmall)
                }
            }
        } else {
            OutlinedTextField(
                value = ipNodo,
                onValueChange = { nuevo ->
                    ipNodo = nuevo.filter { it.isDigit() || it == '.' }.take(15)
                    error = null
                },
                label = { Text("IP del Nodo") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Text("Enlace: ${estado.enlace.etiqueta}", style = MaterialTheme.typography.bodyMedium)
        Text(
            if (estado.notificacionesHabilitadas) "Notificaciones: habilitadas"
            else "Notificaciones: deshabilitadas; conexión y control siguen disponibles.",
            color = if (estado.notificacionesHabilitadas) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.error
        )
        if (!estado.notificacionesHabilitadas && Build.VERSION.SDK_INT >= 33) {
            OutlinedButton(onClick = onSolicitarNotificaciones, modifier = Modifier.fillMaxWidth()) {
                Text("Permitir notificaciones")
            }
        }

        Spacer(Modifier.height(2.dp))
        Button(
            enabled = codigo.length == 6 && (rol != RolApp.NODO || ipLocal != null),
            onClick = {
                error = if (rol == RolApp.NODO) {
                    onIniciarNodo(codigo)
                } else {
                    onConectarPanel(ipNodo.trim(), codigo)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (rol == RolApp.NODO) "Iniciar Nodo" else "Conectar")
        }
    }
}
