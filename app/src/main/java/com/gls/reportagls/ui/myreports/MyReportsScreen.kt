package com.gls.reportagls.ui.myreports

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gls.reportagls.domain.LatLng
import com.gls.reportagls.domain.ReportStatus
import com.gls.reportagls.ui.common.EmptyState
import com.gls.reportagls.ui.common.ReportCard
import com.gls.reportagls.util.distanceMeters
import com.gls.reportagls.viewmodel.ReportaGlsViewModel

@Composable
fun MyReportsScreen(
    vm: ReportaGlsViewModel,
    onOpenReport: (String) -> Unit
) {
    val state by vm.uiState.collectAsState()
    val userId = state.currentUser?.id

    val myReports = remember(state.reports, state.myReportsStatus, userId) {
        state.reports
            .filter { it.authorId == userId }
            .filter { state.myReportsStatus == null || it.status == state.myReportsStatus }
            .sortedByDescending { it.createdAt }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Text(
                text = "Mis reportes",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Da seguimiento al estatus de los reportes que enviaste.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = state.myReportsStatus == null,
                onClick = { vm.setMyReportsStatus(null) },
                label = { Text("Todos") }
            )
            ReportStatus.entries.forEach { status ->
                FilterChip(
                    selected = state.myReportsStatus == status,
                    onClick = { vm.setMyReportsStatus(status) },
                    label = { Text(status.label) }
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
            if (myReports.isEmpty()) {
                item {
                    EmptyState(
                        text = "Todavía no tienes reportes con este filtro. Crea uno con el botón +."
                    )
                }
            }
            items(myReports, key = { it.id }) { report ->
                ReportCard(
                    report = report,
                    distanceMeters = distanceMeters(
                        state.userLocation,
                        LatLng(report.latitude, report.longitude)
                    ),
                    onClick = { onOpenReport(report.id) }
                )
            }
        }
    }
}
