package com.gls.reportagls.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gls.reportagls.viewmodel.ReportaGlsViewModel

@Composable
fun LoginScreen(vm: ReportaGlsViewModel) {
    val state by vm.uiState.collectAsState()
    var registerMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(84.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "GLS",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "ReportaGLS", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Tu comunidad, tu voz: reporta y mejora Gabriel Leyva Solano",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (registerMode) "Crear cuenta" else "Iniciar sesión",
                    style = MaterialTheme.typography.titleLarge
                )

                if (registerMode) {
                    OutlinedTextField(
                        value = displayName,
                        onValueChange = {
                            displayName = it
                            vm.clearLoginError()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nombre para mostrar") },
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Filled.Person, contentDescription = null)
                        }
                    )
                }

                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        username = it
                        vm.clearLoginError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Usuario") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null)
                    }
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        vm.clearLoginError()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Contraseña") },
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = null)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                if (state.loginError != null) {
                    Text(
                        text = state.loginError.orEmpty(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        if (registerMode) {
                            vm.register(username, password, displayName)
                        } else {
                            vm.login(username, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (registerMode) "Crear cuenta" else "Entrar")
                }

                TextButton(
                    onClick = {
                        registerMode = !registerMode
                        vm.clearLoginError()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (registerMode) {
                            "¿Ya tienes cuenta? Inicia sesión"
                        } else {
                            "¿No tienes cuenta? Regístrate"
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Cuentas de demostración",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Ciudadano: ciudadano / 1234",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Administrador: admin / admin123",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}
