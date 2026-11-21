package com.gls.reportagls.data

import com.gls.reportagls.data.local.ReportLocalDataSource
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportStatus

/** Repositorio de reportes: combina persistencia local con operaciones de sincronía. */
class ReportRepository(private val local: ReportLocalDataSource) {

    fun getReports(): List<Report> = local.load()

    fun add(report: Report): List<Report> {
        val updated = getReports().toMutableList().apply { add(report) }
        local.save(updated)
        return updated
    }

    fun updateStatus(reportId: String, status: ReportStatus): List<Report> {
        val updated = getReports().map { report ->
            if (report.id == reportId) report.copy(status = status, synced = false) else report
        }
        local.save(updated)
        return updated
    }

    /** Reemplaza el estado local tras aplicar los cambios recibidos desde la API. */
    fun replaceAll(reports: List<Report>): List<Report> {
        local.save(reports)
        return reports
    }

    /** Marca como sincronizados los reportes enviados correctamente. */
    fun markSynced(ids: Set<String>): List<Report> {
        val updated = getReports().map { report ->
            if (report.id in ids) report.copy(synced = true) else report
        }
        local.save(updated)
        return updated
    }

    fun markAllSynced(): List<Report> = markSynced(getReports().map { it.id }.toSet())
}
