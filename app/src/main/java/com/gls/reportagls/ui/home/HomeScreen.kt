package com.gls.reportagls.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gls.reportagls.domain.LatLng
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportCategory
import com.gls.reportagls.ui.common.CategoryDot
import com.gls.reportagls.ui.common.EmptyState
import com.gls.reportagls.ui.common.ReportCard
import com.gls.reportagls.ui.common.categoryColor
import com.gls.reportagls.util.LocationHelper
import com.gls.reportagls.util.distanceLabel
import com.gls.reportagls.util.distanceMeters
import com.gls.reportagls.viewmodel.ReportaGlsViewModel
import kotlin.math.cos

@Composable
fun HomeScreen(
    vm: ReportaGlsViewModel,
    onOpenReport: (String) -> Unit
) {
    val state by vm.uiState.collectAsState()
    var showMap by remember { mutableStateOf(false) }

    val context = LocalContext.current
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

    val filtered = remember(
        state.reports,
        state.selectedCategories,
        state.radiusMeters,
        state.userLocation
    ) {
        state.reports
            .filter { it.category in state.selectedCategories }
            .map { report ->
                report to distanceMeters(
                    state.userLocation,
                    LatLng(report.latitude, report.longitude)
                )
            }
            .filter { (_, distance) -> distance <= state.radiusMeters }
            .sortedBy { (_, distance) -> distance }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Reportes cercanos",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "Hola, ${state.currentUser?.displayName.orEmpty()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = vm::logout) { Text("Salir") }
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ReportCategory.entries) { category ->
                FilterChip(
                    selected = category in state.selectedCategories,
                    onClick = { vm.toggleCategory(category) },
                    label = { Text("${category.emoji} ${category.label}") }
                )
            }
        }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Radio de búsqueda: ${distanceLabel(state.radiusMeters.toDouble())}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Slider(
                value = state.radiusMeters.toFloat(),
                onValueChange = { vm.setRadius(it.toInt()) },
                valueRange = 500f..5000f,
                steps = 8
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !showMap,
                        onClick = { showMap = false },
                        label = { Text("Lista") }
                    )
                    FilterChip(
                        selected = showMap,
                        onClick = { showMap = true },
                        label = { Text("Mapa") }
                    )
                }
                OutlinedButton(onClick = refreshLocation) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Mi ubicación")
                }
            }
            Text(
                text = "${filtered.size} reportes dentro del radio",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (showMap) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    SchematicMap(
                        reports = filtered.map { it.first },
                        center = state.userLocation,
                        radiusMeters = state.radiusMeters,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
                MapLegend(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp
                ),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (filtered.isEmpty()) {
                    item {
                        EmptyState(
                            text = "No hay reportes con estos filtros. Ajusta el radio o las categorías."
                        )
                    }
                }
                items(filtered, key = { it.first.id }) { (report, distance) ->
                    ReportCard(
                        report = report,
                        distanceMeters = distance,
                        onClick = { onOpenReport(report.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MapLegend(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "Referencias",
                style = MaterialTheme.typography.titleMedium
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ReportCategory.entries.forEach { category ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        CategoryDot(category = category)
                        Text(
                            text = "  ${category.label}",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mapa esquemático dibujado con Canvas: cada reporte se ubica según su distancia
 * real al centro (la ubicación del usuario). No requiere llave de Google Maps ni
 * conexión, por lo que funciona en la demostración sin configuración externa.
 */
@Composable
private fun SchematicMap(
    reports: List<Report>,
    center: LatLng,
    radiusMeters: Int,
    modifier: Modifier = Modifier
) {
    val background = MaterialTheme.colorScheme.surfaceVariant
    val grid = MaterialTheme.colorScheme.outlineVariant
    val primary = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val radiusPx = size.minDimension / 2f - 10f
        val centerOffset = Offset(size.width / 2f, size.height / 2f)

        drawCircle(color = background, radius = radiusPx, center = centerOffset)
        for (index in 1..3) {
            drawCircle(
                color = grid,
                radius = radiusPx * index / 3f,
                center = centerOffset,
                style = Stroke(width = 2f)
            )
        }

        val metersPerDegreeLat = 111_320.0
        val metersPerDegreeLon = 111_320.0 * cos(Math.toRadians(center.latitude))
        val scale = radiusPx / radiusMeters

        reports.forEach { report ->
            val dx = (report.longitude - center.longitude) * metersPerDegreeLon
            val dy = (report.latitude - center.latitude) * metersPerDegreeLat
            val point = Offset(
                x = centerOffset.x + (dx * scale).toFloat(),
                y = centerOffset.y - (dy * scale).toFloat()
            )
            drawCircle(color = categoryColor(report.category), radius = 11f, center = point)
            drawCircle(color = Color.White, radius = 4f, center = point)
        }

        drawCircle(color = primary, radius = 12f, center = centerOffset)
        drawCircle(color = Color.White, radius = 5f, center = centerOffset)
    }
}
