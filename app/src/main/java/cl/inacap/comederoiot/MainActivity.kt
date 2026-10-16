package cl.inacap.comederoiot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

/** Hito 0: comprueba que Kotlin, Compose y Material 3 pueden iniciar en el teléfono. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                Scaffold(modifier = Modifier.fillMaxSize()) { espacioSistema ->
                    Column(
                        modifier = Modifier
                            .padding(espacioSistema)
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(stringResource(R.string.base_title),
                            style = MaterialTheme.typography.headlineMedium)
                        Text(stringResource(R.string.base_subtitle),
                            style = MaterialTheme.typography.titleMedium)
                        Text(stringResource(R.string.base_description))
                        Text(stringResource(R.string.base_pending))
                    }
                }
            }
        }
    }
}
