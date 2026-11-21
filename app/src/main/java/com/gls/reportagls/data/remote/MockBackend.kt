package com.gls.reportagls.data.remote

/**
 * Backend simulado en memoria. Permite demostrar el flujo completo de
 * conectividad (Retrofit + OkHttp + Gson) sin depender de un servidor externo
 * ni de llaves de API. Se sustituye por el servicio real cambiando el cliente
 * en AppModule.
 */
class MockBackend {
    private val lock = Any()
    private val items = LinkedHashMap<String, ReportDto>()

    @Volatile
    var forceOffline: Boolean = false

    fun isEmpty(): Boolean = synchronized(lock) { items.isEmpty() }

    fun snapshot(): List<ReportDto> = synchronized(lock) { items.values.toList() }

    fun seedIfEmpty(reports: List<ReportDto>) {
        synchronized(lock) {
            if (items.isEmpty()) reports.forEach { items[it.id] = it }
        }
    }

    fun add(report: ReportDto) {
        synchronized(lock) { items[report.id] = report }
    }

    fun updateStatus(id: String, status: String): ReportDto? = synchronized(lock) {
        items[id]?.copy(status = status)?.also { items[id] = it }
    }
}
