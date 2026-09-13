package com.gls.reportagls

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text

/**
 * Punto de entrada de la aplicación ReportaGLS.
 * La navegación entre pantallas (home, report, detail) se define en ui/.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface {
                    Text(text = "ReportaGLS - proyecto en configuración inicial")
                }
            }
        }
    }
}
