package com.gls.reportagls.data

import android.content.Context
import com.gls.reportagls.data.remote.MockBackend
import com.gls.reportagls.data.remote.ReportApiService
import com.gls.reportagls.data.remote.toDto
import com.gls.reportagls.data.remote.toDomain
import com.gls.reportagls.util.isOnline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

/** Resultado de una sincronización, mostrado en la bitácora de la UI. */
data class SyncResult(
    val success: Boolean,
    val message: String,
    val pulled: Int = 0,
    val pushed: Int = 0
)

/**
 * Orquesta la sincronización entre la persistencia local y la API REST.
 * Si el dispositivo está sin red, conserva los cambios locales para enviarlos
 * en la siguiente ejecución.
 */
class SyncManager(
    private val context: Context,
    private val repository: ReportRepository,
    private val api: ReportApiService,
    private val backend: MockBackend
) {

    suspend fun synchronize(): SyncResult = withContext(Dispatchers.IO) {
        if (!context.isOnline()) {
            return@withContext SyncResult(
                success = false,
                message = "Sin conexión a red. Los cambios se conservan en el dispositivo."
            )
        }

        try {
            val local = repository.getReports()

            if (backend.isEmpty()) {
                local.forEach { api.createReport(it.toDto().copy(synced = true)) }
                repository.markAllSynced()
                return@withContext SyncResult(
                    success = true,
                    message = "Base remota inicializada con ${local.size} reportes.",
                    pushed = local.size
                )
            }

            val remote = api.getReports().map { it.toDomain() }
            val merged = local.associateBy { it.id }.toMutableMap()
            var pulled = 0

            remote.forEach { remoteReport ->
                val existing = merged[remoteReport.id]
                when {
                    existing == null -> {
                        merged[remoteReport.id] = remoteReport.copy(synced = true)
                        pulled++
                    }
                    existing.status != remoteReport.status -> {
                        merged[remoteReport.id] = existing.copy(
                            status = remoteReport.status,
                            synced = true
                        )
                    }
                }
            }

            val all = merged.values.toList()
            repository.replaceAll(all)

            val pending = all.filter { !it.synced }
            pending.forEach { api.createReport(it.toDto()) }
            repository.markSynced(pending.map { it.id }.toSet())

            SyncResult(
                success = true,
                message = "Sincronización completada: $pulled recibidos, ${pending.size} enviados.",
                pulled = pulled,
                pushed = pending.size
            )
        } catch (error: IOException) {
            SyncResult(false, "Error de red: ${error.message ?: "desconocido"}")
        } catch (error: Exception) {
            SyncResult(false, "No se pudo sincronizar: ${error.message ?: "error inesperado"}")
        }
    }
}
