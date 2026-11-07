package com.gls.reportagls.data

import com.gls.reportagls.data.local.StoredUser
import com.gls.reportagls.data.local.UserLocalDataSource
import com.gls.reportagls.domain.User
import com.gls.reportagls.domain.UserRole

/** Repositorio de autenticación y sesión de usuario. */
class UserRepository(private val local: UserLocalDataSource) {

    fun login(username: String, password: String): User? {
        val stored = local.authenticate(username, password) ?: return null
        local.setCurrentUsername(stored.username)
        return stored.toUser()
    }

    fun register(username: String, password: String, displayName: String): Result<User> {
        val normalized = username.trim()
        if (normalized.length < 3) {
            return Result.failure(IllegalArgumentException("El usuario debe tener al menos 3 caracteres."))
        }
        if (password.length < 4) {
            return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 4 caracteres."))
        }
        if (displayName.isBlank()) {
            return Result.failure(IllegalArgumentException("Escribe un nombre para mostrar."))
        }
        if (local.exists(normalized)) {
            return Result.failure(IllegalArgumentException("Ese usuario ya está registrado."))
        }
        val stored = local.insert(normalized, password, displayName, UserRole.CIUDADANO)
        local.setCurrentUsername(stored.username)
        return Result.success(stored.toUser())
    }

    fun currentUser(): User? {
        val username = local.currentUsername() ?: return null
        return local.find(username)?.toUser()
    }

    fun logout() {
        local.setCurrentUsername(null)
    }

    private fun StoredUser.toUser() = User(
        id = username,
        username = username,
        displayName = displayName,
        role = role
    )
}
