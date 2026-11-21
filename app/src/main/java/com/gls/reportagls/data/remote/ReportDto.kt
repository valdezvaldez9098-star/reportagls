package com.gls.reportagls.data.remote

import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportCategory
import com.gls.reportagls.domain.ReportStatus

/** Representación del reporte en la API REST (contrato de red). */
data class ReportDto(
    val id: String,
    val category: String,
    val description: String,
    val photoPath: String? = null,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val status: String,
    val createdAt: Long,
    val detectedAt: Long,
    val authorId: String,
    val authorName: String,
    val synced: Boolean = true
)

fun Report.toDto(): ReportDto = ReportDto(
    id = id,
    category = category.name,
    description = description,
    photoPath = photoPath,
    latitude = latitude,
    longitude = longitude,
    address = address,
    status = status.name,
    createdAt = createdAt,
    detectedAt = detectedAt,
    authorId = authorId,
    authorName = authorName,
    synced = synced
)

fun ReportDto.toDomain(): Report = Report(
    id = id,
    category = runCatching { ReportCategory.valueOf(category) }
        .getOrDefault(ReportCategory.OTRO),
    description = description,
    photoPath = photoPath,
    latitude = latitude,
    longitude = longitude,
    address = address,
    status = runCatching { ReportStatus.valueOf(status) }
        .getOrDefault(ReportStatus.RECIBIDO),
    createdAt = createdAt,
    detectedAt = detectedAt,
    authorId = authorId,
    authorName = authorName,
    synced = synced
)
