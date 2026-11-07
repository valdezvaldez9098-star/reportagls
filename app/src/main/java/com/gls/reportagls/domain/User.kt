package com.gls.reportagls.domain

/** Rol del usuario dentro de ReportaGLS. */
enum class UserRole { CIUDADANO, ADMIN }

/**
 * Usuario autenticado en la aplicación.
 *
 * El rol ADMIN permite cambiar el estatus de los reportes (simula al ayuntamiento).
 */
data class User(
    val id: String,
    val username: String,
    val displayName: String,
    val role: UserRole
) {
    val isAdmin: Boolean get() = role == UserRole.ADMIN
}
