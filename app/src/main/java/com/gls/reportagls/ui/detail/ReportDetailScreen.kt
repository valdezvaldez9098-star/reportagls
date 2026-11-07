package com.gls.reportagls.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportStatus
import com.gls.reportagls.ui.common.StatusBadge
import com.gls.reportagls.ui.common.statusColor
import com.gls.reportagls.util.PhotoStore
import com.gls.reportagls.util.formatDateTime
import com.gls.reportagls.util.formatTime

@Composable
fun ReportDetailScreen(
    report: Report,
    isAdmin: Boolean,
    onBack: () -> Unit,
    onStatusChange: (ReportStatus) -> Unit
) {
    val photo = remember(report.photoPath) { PhotoStore.load(report.photoPath) }

    Column(modifier = Modifier.fillMaxSize()) {
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
                text = "Detalle del reporte",
                style = MaterialTheme.typography.titleLarge
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (photo != null) {
                Image(
                    bitmap = photo.asImageBitmap(),
                    contentDescription = "Fotografía del reporte",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = report.category.emoji, fontSize = 48.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${report.category.emoji} ${report.category.label}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(status = report.status)
            }

            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyLarge
            )

            StatusTimeline(current = report.status)

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Información", style = MaterialTheme.typography.titleMedium)
                    HorizontalDivider()
                    InfoRow("Ubicación", report.address)
                    InfoRow(
                        "Coordenadas",
                        "${report.latitude}, ${report.longitude}"
                    )
                    InfoRow("Detectado", formatDateTime(report.detectedAt))
                    InfoRow("Registrado", formatDateTime(report.createdAt))
                    InfoRow("Reportado por", report.authorName)
                }
            }

            if (isAdmin) {
                AdminStatusCard(
                    current = report.status,
                    onStatusChange = onStatusChange
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            modifier = Modifier.width(110.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun StatusTimeline(current: ReportStatus) {
    val currentIndex = current.ordinal
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReportStatus.entries.forEachIndexed { index, status ->
            val reached = index <= currentIndex
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (reached) statusColor(status)
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = status.label,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (index == currentIndex) FontWeight.Bold else FontWeight.Normal,
                    color = if (reached) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AdminStatusCard(
    current: ReportStatus,
    onStatusChange: (ReportStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Administración",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Como administrador puedes actualizar el estatus del reporte.",
                style = MaterialTheme.typography.bodyMedium
            )
            Box {
                OutlinedButton(onClick = { expanded = true }) {
                    Text("Estatus: ${current.label}")
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    ReportStatus.entries.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.label) },
                            onClick = {
                                expanded = false
                                onStatusChange(status)
                            }
                        )
                    }
                }
            }
        }
    }
}
