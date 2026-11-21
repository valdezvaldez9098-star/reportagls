package com.gls.reportagls.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gls.reportagls.ReportaGlsApplication
import com.gls.reportagls.domain.LatLng
import com.gls.reportagls.domain.Report
import com.gls.reportagls.domain.ReportCategory
import com.gls.reportagls.domain.ReportStatus
import com.gls.reportagls.domain.User
import com.gls.reportagls.util.isOnline
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/** Estado único que consume toda la UI de ReportaGLS. */
data class UiState(
    val currentUser: User? = null,
    val reports: List<Report> = emptyList(),
    val selectedCategories: Set<ReportCategory> = ReportCategory.entries.toSet(),
    val radiusMeters: Int = 2000,
    val myReportsStatus: ReportStatus? = null,
    val userLocation: LatLng = LatLng.GABRIEL_LEYVA_SOLANO,
    val loginError: String? = null,
    val isOnline: Boolean = true,
    val isSyncing: Boolean = false,
    val syncMessage: String? = null,
    val lastSyncAt: Long? = null,
    val simulateOffline: Boolean = false
)

/**
 * ViewModel principal (patrón MVVM).
 *
 * Mantiene sesión, reportes, filtros, ubicación y estado de conectividad.
 * Toda la UI observa [uiState].
 */
class ReportaGlsViewModel(application: Application) : AndroidViewModel(application) {

    private val appModule = (application as ReportaGlsApplication).appModule
    private val reportRepository = appModule.reportRepository
    private val userRepository = appModule.userRepository

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(
            currentUser = userRepository.currentUser(),
            reports = reportRepository.getReports(),
            isOnline = getApplication<Application>().isOnline()
        )
    }

    // ---------------------------------------------------------------------
    // Sesión
    // ---------------------------------------------------------------------

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(loginError = "Escribe tu usuario y contraseña.")
            return
        }
        val user = userRepository.login(username, password)
        _uiState.value = if (user == null) {
            _uiState.value.copy(loginError = "Usuario o contraseña incorrectos.")
        } else {
            _uiState.value.copy(currentUser = user, loginError = null)
        }
    }

    fun register(username: String, password: String, displayName: String) {
        userRepository.register(username, password, displayName).fold(
            onSuccess = { user ->
                _uiState.value = _uiState.value.copy(currentUser = user, loginError = null)
            },
            onFailure = { error ->
                _uiState.value = _uiState.value.copy(
                    loginError = error.message ?: "No fue posible crear la cuenta."
                )
            }
        )
    }

    fun logout() {
        userRepository.logout()
        _uiState.value = _uiState.value.copy(currentUser = null, loginError = null)
    }

    fun clearLoginError() {
        if (_uiState.value.loginError != null) {
            _uiState.value = _uiState.value.copy(loginError = null)
        }
    }

    // ---------------------------------------------------------------------
    // Reportes
    // ---------------------------------------------------------------------

    fun createReport(
        category: ReportCategory,
        description: String,
        photoPath: String?,
        address: String,
        detectedAt: Long
    ) {
        val user = _uiState.value.currentUser ?: return
        val location = _uiState.value.userLocation
        val report = Report(
            id = UUID.randomUUID().toString(),
            category = category,
            description = description.trim(),
            photoPath = photoPath,
            latitude = location.latitude,
            longitude = location.longitude,
            address = address.trim().ifBlank { "Gabriel Leyva Solano, Guasave, Sinaloa" },
            status = ReportStatus.RECIBIDO,
            createdAt = System.currentTimeMillis(),
            detectedAt = detectedAt,
            authorId = user.id,
            authorName = user.displayName,
            synced = false
        )
        val updated = reportRepository.add(report)
        _uiState.value = _uiState.value.copy(
            reports = updated,
            syncMessage = "Reporte guardado en el dispositivo; pendiente de sincronizar."
        )
    }

    fun updateStatus(reportId: String, status: ReportStatus) {
        val updated = reportRepository.updateStatus(reportId, status)
        _uiState.value = _uiState.value.copy(
            reports = updated,
            syncMessage = "Estatus actualizado; pendiente de sincronizar."
        )
    }

    // ---------------------------------------------------------------------
    // Conectividad y sincronización
    // ---------------------------------------------------------------------

    fun synchronize() {
        if (_uiState.value.isSyncing) return
        _uiState.value = _uiState.value.copy(isSyncing = true, syncMessage = null)

        viewModelScope.launch {
            val result = appModule.syncManager.synchronize()
            _uiState.value = _uiState.value.copy(
                isSyncing = false,
                isOnline = getApplication<Application>().isOnline() && !_uiState.value.simulateOffline,
                syncMessage = result.message,
                lastSyncAt = if (result.success) System.currentTimeMillis() else _uiState.value.lastSyncAt,
                reports = reportRepository.getReports()
            )
        }
    }

    fun setSimulateOffline(enabled: Boolean) {
        appModule.mockBackend.forceOffline = enabled
        _uiState.value = _uiState.value.copy(
            simulateOffline = enabled,
            isOnline = if (enabled) false else getApplication<Application>().isOnline(),
            syncMessage = if (enabled) {
                "Modo sin conexión activado (prueba de resiliencia)."
            } else {
                "Modo en línea restablecido."
            }
        )
    }

    fun refreshConnectivity() {
        _uiState.value = _uiState.value.copy(
            isOnline = getApplication<Application>().isOnline() && !_uiState.value.simulateOffline
        )
    }

    // ---------------------------------------------------------------------
    // Filtros y ubicación
    // ---------------------------------------------------------------------

    fun toggleCategory(category: ReportCategory) {
        val current = _uiState.value.selectedCategories
        val next = when {
            category in current && current.size > 1 -> current - category
            category in current -> current
            else -> current + category
        }
        _uiState.value = _uiState.value.copy(selectedCategories = next)
    }

    fun setRadius(meters: Int) {
        _uiState.value = _uiState.value.copy(radiusMeters = meters)
    }

    fun setMyReportsStatus(status: ReportStatus?) {
        _uiState.value = _uiState.value.copy(myReportsStatus = status)
    }

    fun setUserLocation(location: LatLng) {
        _uiState.value = _uiState.value.copy(userLocation = location)
    }
}
