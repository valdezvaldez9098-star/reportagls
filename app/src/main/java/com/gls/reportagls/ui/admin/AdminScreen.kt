package com.gls.reportagls.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportStatus
import com.gls.reportagls.ui.common.StatusBadge
import com.gls.reportagls.ui.common.statusColor
import com.gls.reportagls.viewmodel.ReportaGlsViewModel

@Composable
fun AdminScreen(
    vm: ReportaGlsViewModel,
    onOpenReport: (String) -> Unit
) {
    val state by vm.uiState.collectAsState()
    val reports = remember(state.reports) { state.reports.sortedByDescending { it.createdAt } }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Administración",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Actualiza el estatus de los reportes de la comunidad.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ReportStatus.entries.forEach { status ->
                StatusSummary(
                    status = status,
                    count = reports.count { it.status == status },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(reports, key = { it.id }) { report ->
                AdminReportItem(
                    report = report,
                    onOpen = { onOpenReport(report.id) },
                    onStatusChange = { status -> vm.updateStatus(report.id, status) }
                )
            }
        }
    }
}

@Composable
private fun StatusSummary(
    status: ReportStatus,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = statusColor(status).copy(alpha = 0.14f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = statusColor(status)
            )
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                color = statusColor(status)
            )
        }
    }
}

@Composable
private fun AdminReportItem(
    report: Report,
    onOpen: () -> Unit,
    onStatusChange: (ReportStatus) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = report.category.emoji, fontSize = 22.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.category.label,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = report.address,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                StatusBadge(status = report.status)
            }
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Por ${report.authorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.weight(1f))
                Box {
                    OutlinedButton(onClick = { expanded = true }) {
                        Text("Cambiar estatus")
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
}
