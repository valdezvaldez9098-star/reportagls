package com.gls.reportagls.data.local

import android.content.Context
import com.gls.reportagls.domain.UserRole
import org.json.JSONArray
import org.json.JSONObject
import java.security.MessageDigest

/** Usuario tal como se guarda localmente (incluye el hash de la contraseña). */
data class StoredUser(
    val username: String,
    val passwordHash: String,
    val displayName: String,
    val role: UserRole
)

/**
 * Registro e inicio de sesión local.
 *
 * Para este avance la autenticación es local y las contraseñas se guardan con
 * SHA-256 (no en texto plano). En la entrega final se sustituirá por el backend
 * real (Firebase/Auth), conservando esta misma interfaz de repositorio.
 */
class UserLocalDataSource(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    init {
        seedIfNeeded()
    }

    fun find(username: String): StoredUser? {
        val normalized = username.trim().lowercase()
        return readUsers().firstOrNull { it.username == normalized }
    }

    fun exists(username: String): Boolean = find(username) != null

    fun authenticate(username: String, password: String): StoredUser? {
        val user = find(username) ?: return null
        return if (user.passwordHash == sha256(password)) user else null
    }

    fun insert(username: String, password: String, displayName: String, role: UserRole): StoredUser {
        val normalized = username.trim().lowercase()
        val stored = StoredUser(
            username = normalized,
            passwordHash = sha256(password),
            displayName = displayName.trim(),
            role = role
        )
        val users = readUsers().toMutableList()
        users.removeAll { it.username == normalized }
        users.add(stored)
        writeUsers(users)
        return stored
    }

    fun currentUsername(): String? = prefs.getString(KEY_SESSION, null)

    fun setCurrentUsername(username: String?) {
        prefs.edit().apply {
            if (username == null) remove(KEY_SESSION) else putString(KEY_SESSION, username)
        }.apply()
    }

    private fun readUsers(): List<StoredUser> {
        val raw = prefs.getString(KEY_USERS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)
                    add(
                        StoredUser(
                            username = item.getString("username"),
                            passwordHash = item.getString("passwordHash"),
                            displayName = item.getString("displayName"),
                            role = UserRole.valueOf(item.getString("role"))
                        )
                    )
                }
            }
        }.getOrDefault(emptyList())
    }

    private fun writeUsers(users: List<StoredUser>) {
        val array = JSONArray()
        users.forEach { user ->
            array.put(
                JSONObject().apply {
                    put("username", user.username)
                    put("passwordHash", user.passwordHash)
                    put("displayName", user.displayName)
                    put("role", user.role.name)
                }
            )
        }
        prefs.edit().putString(KEY_USERS, array.toString()).apply()
    }

    private fun seedIfNeeded() {
        if (prefs.contains(KEY_USERS)) return
        writeUsers(
            listOf(
                StoredUser(
                    username = "admin",
                    passwordHash = sha256("admin123"),
                    displayName = "Administrador GLS",
                    role = UserRole.ADMIN
                ),
                StoredUser(
                    username = "ciudadano",
                    passwordHash = sha256("1234"),
                    displayName = "Vecino de Gabriel Leyva",
                    role = UserRole.CIUDADANO
                )
            )
        )
    }

    private fun sha256(text: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(text.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }

    private companion object {
        const val PREFS = "reportagls_users"
        const val KEY_USERS = "users"
        const val KEY_SESSION = "session"
    }
}
