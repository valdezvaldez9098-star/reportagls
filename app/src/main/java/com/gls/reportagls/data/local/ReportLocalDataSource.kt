package com.gls.reportagls.data.local

import android.content.Context
import com.gls.reportagls.domain.LatLng
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportCategory
import com.gls.reportagls.domain.ReportStatus
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * Persistencia local de reportes usando un archivo JSON dentro del almacenamiento
 * interno. Permite que la app conserve los reportes entre sesiones y funcione sin
 * conexión. En una iteración posterior se migrará a Room (ver reporte de avance).
 */
class ReportLocalDataSource(context: Context) {

    private val file = File(context.filesDir, "reports.json")

    fun load(): List<Report> {
        if (!file.exists()) {
            val seeded = seedReports()
            save(seeded)
            return seeded
        }
        return runCatching {
            val array = JSONArray(file.readText())
            buildList {
                for (index in 0 until array.length()) {
                    add(array.getJSONObject(index).toReport())
                }
            }
        }.getOrDefault(emptyList())
    }

    fun save(reports: List<Report>) {
        val array = JSONArray()
        reports.forEach { array.put(it.toJson()) }
        file.writeText(array.toString())
    }

    private fun JSONObject.toReport(): Report = Report(
        id = getString("id"),
        category = ReportCategory.valueOf(getString("category")),
        description = getString("description"),
        photoPath = if (isNull("photoPath")) null else optString("photoPath").ifBlank { null },
        latitude = getDouble("latitude"),
        longitude = getDouble("longitude"),
        address = optString("address"),
        status = ReportStatus.valueOf(getString("status")),
        createdAt = getLong("createdAt"),
        detectedAt = getLong("detectedAt"),
        authorId = getString("authorId"),
        authorName = getString("authorName")
    )

    private fun Report.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("category", category.name)
        put("description", description)
        put("photoPath", photoPath ?: JSONObject.NULL)
        put("latitude", latitude)
        put("longitude", longitude)
        put("address", address)
        put("status", status.name)
        put("createdAt", createdAt)
        put("detectedAt", detectedAt)
        put("authorId", authorId)
        put("authorName", authorName)
    }

    /**
     * Reportes de ejemplo para que la demostración del avance tenga contenido
     * inmediato en el mapa y en la lista, incluso en un emulador sin datos.
     */
    private fun seedReports(): List<Report> {
        val base = LatLng.GABRIEL_LEYVA_SOLANO
        val now = System.currentTimeMillis()
        val day = 24L * 60L * 60L * 1000L

        fun report(
            id: String,
            category: ReportCategory,
            description: String,
            status: ReportStatus,
            dLat: Double,
            dLon: Double,
            ageDays: Long,
            address: String,
            authorId: String = "vecino-gls",
            authorName: String = "Vecino de GLS"
        ) = Report(
            id = id,
            category = category,
            description = description,
            photoPath = null,
            latitude = base.latitude + dLat,
            longitude = base.longitude + dLon,
            address = address,
            status = status,
            createdAt = now - ageDays * day,
            detectedAt = now - ageDays * day,
            authorId = authorId,
            authorName = authorName
        )

        return listOf(
            report(
                "seed-1", ReportCategory.BACHE,
                "Bache profundo sobre el bulevar principal; ya dañó dos llantas.",
                ReportStatus.EN_PROCESO, 0.0011, 0.0007, 6,
                "Bulevar principal, frente a la plaza",
                authorId = "ciudadano",
                authorName = "Vecino de Gabriel Leyva"
            ),
            report(
                "seed-2", ReportCategory.ALUMBRADO,
                "Dos luminarias apagadas desde hace varias semanas en la calle del mercado.",
                ReportStatus.RECIBIDO, -0.0008, 0.0014, 4,
                "Calle del Mercado",
                authorId = "ciudadano",
                authorName = "Vecino de Gabriel Leyva"
            ),
            report(
                "seed-3", ReportCategory.AGUA,
                "Fuga de agua potable que genera encharcamiento junto a la escuela.",
                ReportStatus.RECIBIDO, 0.0005, -0.0012, 3,
                "Calle Juárez, junto a la primaria"
            ),
            report(
                "seed-4", ReportCategory.BASURA,
                "Acumulación de basura en el callejón, ya hay malos olores.",
                ReportStatus.RESUELTO, -0.0013, -0.0006, 12,
                "Callejón de la colonia Centro",
                authorId = "ciudadano",
                authorName = "Vecino de Gabriel Leyva"
            ),
            report(
                "seed-5", ReportCategory.BACHE,
                "Bache grande en la esquina, difícil de ver por la noche.",
                ReportStatus.RESUELTO, 0.0018, -0.0018, 20,
                "Avenida Revolución esquina con Allende"
            ),
            report(
                "seed-6", ReportCategory.OTRO,
                "Señal de tránsito caída y poste inclinado.",
                ReportStatus.EN_PROCESO, -0.0016, 0.0009, 2,
                "Camino a la sindicatura"
            ),
            report(
                "seed-7", ReportCategory.ALUMBRADO,
                "Foco fundido en el parque; los vecinos caminan a oscuras.",
                ReportStatus.RECIBIDO, 0.0009, 0.0021, 1,
                "Parque de la comunidad"
            )
        )
    }
}
