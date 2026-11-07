package com.gls.reportagls.domain

/**
 * Modelo de dominio de un reporte ciudadano.
 *
 * Representa una falla de un servicio público levantada por un habitante de
 * Gabriel Leyva Solano. Es independiente de cómo se persiste o se pinta en la UI.
 */
data class Report(
    val id: String,
    val category: ReportCategory,
    val description: String,
    val photoPath: String?,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val status: ReportStatus,
    val createdAt: Long,
    val detectedAt: Long,
    val authorId: String,
    val authorName: String
)

enum class ReportCategory(val label: String, val emoji: String) {
    BACHE("Bache", "\uD83D\uDD73\uFE0F"),
    ALUMBRADO("Alumbrado", "\uD83D\uDCA1"),
    AGUA("Agua", "\uD83D\uDCA7"),
    BASURA("Basura", "\uD83D\uDDD1\uFE0F"),
    OTRO("Otro", "\u26A0\uFE0F")
}

enum class ReportStatus(val label: String) {
    RECIBIDO("Recibido"),
    EN_PROCESO("En proceso"),
    RESUELTO("Resuelto")
}

/** Coordenada geográfica simple, independiente de las APIs de Android. */
data class LatLng(val latitude: Double, val longitude: Double) {
    companion object {
        /** Centro aproximado de la comisaría de Gabriel Leyva Solano, Guasave, Sinaloa. */
        val GABRIEL_LEYVA_SOLANO = LatLng(25.6625, -108.6289)
    }
}
