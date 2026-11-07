package com.gls.reportagls.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportCategory
import com.gls.reportagls.domain.ReportStatus
import com.gls.reportagls.ui.theme.CategoryAgua
import com.gls.reportagls.ui.theme.CategoryAlumbrado
import com.gls.reportagls.ui.theme.CategoryBasura
import com.gls.reportagls.ui.theme.CategoryBache
import com.gls.reportagls.ui.theme.CategoryOtro
import com.gls.reportagls.ui.theme.StatusEnProceso
import com.gls.reportagls.ui.theme.StatusRecibido
import com.gls.reportagls.ui.theme.StatusResuelto
import com.gls.reportagls.util.distanceLabel
import com.gls.reportagls.util.formatDate

fun categoryColor(category: ReportCategory): Color = when (category) {
    ReportCategory.BACHE -> CategoryBache
    ReportCategory.ALUMBRADO -> CategoryAlumbrado
    ReportCategory.AGUA -> CategoryAgua
    ReportCategory.BASURA -> CategoryBasura
    ReportCategory.OTRO -> CategoryOtro
}

fun statusColor(status: ReportStatus): Color = when (status) {
    ReportStatus.RECIBIDO -> StatusRecibido
    ReportStatus.EN_PROCESO -> StatusEnProceso
    ReportStatus.RESUELTO -> StatusResuelto
}

@Composable
fun StatusBadge(status: ReportStatus, modifier: Modifier = Modifier) {
    val color = statusColor(status)
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.14f),
        contentColor = color,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = status.label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CategoryDot(category: ReportCategory, modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .size(10.dp)
            .clip(RoundedCornerShape(50))
            .background(categoryColor(category))
    )
}

@Composable
fun ReportCard(
    report: Report,
    distanceMeters: Double?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
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
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (distanceMeters != null) {
                    Text(
                        text = distanceLabel(distanceMeters),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = formatDate(report.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Por ${report.authorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "\uD83D\uDCCD", fontSize = 34.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
