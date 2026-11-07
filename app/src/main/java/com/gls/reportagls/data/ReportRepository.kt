package com.gls.reportagls.data

import com.gls.reportagls.data.local.ReportLocalDataSource
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportStatus

/** Repositorio de reportes. Por ahora respaldado por el almacenamiento local. */
class ReportRepository(private val local: ReportLocalDataSource) {

    fun getReports(): List<Report> = local.load()

    fun add(report: Report): List<Report> {
        val updated = getReports().toMutableList().apply { add(report) }
        local.save(updated)
        return updated
    }

    fun updateStatus(reportId: String, status: ReportStatus): List<Report> {
        val updated = getReports().map { report ->
            if (report.id == reportId) report.copy(status = status) else report
        }
        local.save(updated)
        return updated
    }
}
