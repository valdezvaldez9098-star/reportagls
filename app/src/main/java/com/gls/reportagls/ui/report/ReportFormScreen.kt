package com.gls.reportagls.ui.report

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gls.reportagls.domain.ReportCategory
import com.gls.reportagls.util.LocationHelper
import com.gls.reportagls.util.PhotoStore
import com.gls.reportagls.util.formatDate
import com.gls.reportagls.util.formatTime
import com.gls.reportagls.viewmodel.ReportaGlsViewModel
import java.util.Calendar

@Composable
fun ReportFormScreen(
    vm: ReportaGlsViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val state by vm.uiState.collectAsState()
    val context = LocalContext.current

    var category by remember { mutableStateOf<ReportCategory?>(null) }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var detectedNow by remember { mutableStateOf(true) }
    var detectedMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { result -> if (result != null) bitmap = result }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) {
            LocationHelper.lastKnownLocation(context)?.let(vm::setUserLocation)
        }
    }
    val refreshLocation: () -> Unit = {
        if (LocationHelper.hasPermission(context)) {
            LocationHelper.lastKnownLocation(context)?.let(vm::setUserLocation)
        } else {
            locationLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val submit: () -> Unit = {
        val selected = category
        when {
            selected == null -> errorMessage = "Selecciona la categoría de la falla."
            description.isBlank() -> errorMessage = "Agrega una descripción del problema."
            else -> {
                val photoPath = bitmap?.let { PhotoStore.save(context, it) }
                val detectedAt = if (detectedNow) System.currentTimeMillis() else detectedMillis
                vm.createReport(
                    category = selected,
                    description = description,
                    photoPath = photoPath,
                    address = address,
                    detectedAt = detectedAt
                )
                onSaved()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Regresar")
            }
            Text(
                text = "Nuevo reporte",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Evidencia fotográfica",
                        style = MaterialTheme.typography.titleMedium
                    )
                    val currentBitmap = bitmap
                    if (currentBitmap != null) {
                        Image(
                            bitmap = currentBitmap.asImageBitmap(),
                            contentDescription = "Fotografía del reporte",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin fotografía",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { cameraLauncher.launch(null) }) {
                            Icon(Icons.Filled.DateRange, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (currentBitmap == null) "Tomar foto" else "Cambiar foto")
                        }
                        if (currentBitmap != null) {
                            TextButton(onClick = { bitmap = null }) {
                                Text("Quitar")
                            }
                        }
                    }
                }
            }

            Text(
                text = "Categoría de la falla",
                style = MaterialTheme.typography.titleMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ReportCategory.entries.forEach { option ->
                    FilterChip(
                        selected = category == option,
                        onClick = {
                            category = option
                            errorMessage = null
                        },
                        label = { Text("${option.emoji} ${option.label}") }
                    )
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = {
                    description = it
                    errorMessage = null
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Descripción del problema") },
                minLines = 3,
                maxLines = 6
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "¿La falla ocurrió ahora mismo?",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Si no, indica la fecha y hora aproximadas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = detectedNow,
                            onCheckedChange = { detectedNow = it }
                        )
                    }

                    if (!detectedNow) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = {
                                    val calendar = Calendar.getInstance().apply {
                                        timeInMillis = detectedMillis
                                    }
                                    DatePickerDialog(
                                        context,
                                        { _, year, month, day ->
                                            val updated = Calendar.getInstance().apply {
                                                timeInMillis = detectedMillis
                                                set(Calendar.YEAR, year)
                                                set(Calendar.MONTH, month)
                                                set(Calendar.DAY_OF_MONTH, day)
                                            }
                                            detectedMillis = updated.timeInMillis
                                        },
                                        calendar.get(Calendar.YEAR),
                                        calendar.get(Calendar.MONTH),
                                        calendar.get(Calendar.DAY_OF_MONTH)
                                    ).show()
                                }
                            ) {
                                Text("Fecha: ${formatDate(detectedMillis)}")
                            }
                            OutlinedButton(
                                onClick = {
                                    val calendar = Calendar.getInstance().apply {
                                        timeInMillis = detectedMillis
                                    }
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val updated = Calendar.getInstance().apply {
                                                timeInMillis = detectedMillis
                                                set(Calendar.HOUR_OF_DAY, hour)
                                                set(Calendar.MINUTE, minute)
                                            }
                                            detectedMillis = updated.timeInMillis
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        true
                                    ).show()
                                }
                            ) {
                                Text("Hora: ${formatTime(detectedMillis)}")
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Ubicación GPS",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${state.userLocation.latitude}, ${state.userLocation.longitude}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedButton(onClick = refreshLocation) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Usar mi ubicación actual")
                    }
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Referencia o dirección (opcional)") },
                        singleLine = true
                    )
                }
            }

            if (errorMessage != null) {
                Text(
                    text = errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        Button(
            onClick = submit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Enviar reporte")
        }
    }
}
