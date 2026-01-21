package com.example.aromabox.data.repository

import com.example.aromabox.data.firebase.AuthManager
import com.example.aromabox.data.firebase.FirebaseManager
import com.example.aromabox.data.model.Badge
import com.example.aromabox.data.model.Order
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.data.model.User
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow


object UserRepository {

    /**
     * Controlla se l'utente esiste nel database.
     * Se non esiste, crea un nuovo profilo base.
     */
    suspend fun getOrCreateUser(firebaseUser: FirebaseUser): User? {
        val existingUser = FirebaseManager.getUserById(firebaseUser.uid)

        return if (existingUser != null) {
            existingUser
        } else {
            // Splitta il displayName: primo pezzo = nome, tutto il resto = cognome
            val displayName = firebaseUser.displayName ?: ""
            val nameParts = displayName.split(" ")
            val nome = nameParts.firstOrNull() ?: ""
            val cognome = if (nameParts.size > 1) {
                nameParts.drop(1).joinToString(" ")  // Prende TUTTO dopo il primo spazio
            } else {
                ""
            }

            val newUser = User(
                userId = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                nome = nome,
                cognome = cognome,
                nickname = "",
                photoUrl = firebaseUser.photoUrl?.toString() ?: "",
                connesso = false,
                macchinettaConnessaId = "",
                borsellino = 0.0,
                profiloOlfattivo = null,
                preferitiIds = emptyList(),
                storico = emptyList(),
                badge = emptyList(),
                dataRegistrazione = System.currentTimeMillis()
            )

            val success = FirebaseManager.createUser(newUser)
            if (success) newUser else null
        }
    }

    suspend fun getCurrentUser(): User? {
        val firebaseUser = AuthManager.getCurrentUser() ?: return null
        return FirebaseManager.getUserById(firebaseUser.uid)
    }

    suspend fun updateUser(user: User): Boolean {
        return FirebaseManager.updateUser(user)
    }

    suspend fun updateNickname(userId: String, nickname: String): Boolean {
        return FirebaseManager.updateUserField(userId, "nickname", nickname)
    }

    suspend fun updateProfiloOlfattivo(userId: String, profilo: ProfiloOlfattivo): Boolean {
        return FirebaseManager.updateUserField(userId, "profiloOlfattivo", profilo)
    }

    suspend fun addToFavorites(userId: String, perfumeId: String): Boolean {
        val user = FirebaseManager.getUserById(userId) ?: return false
        val updatedFavorites = user.preferitiIds.toMutableList().apply {
            if (!contains(perfumeId)) add(perfumeId)
        }
        return FirebaseManager.updateUserField(userId, "preferitiIds", updatedFavorites)
    }

    suspend fun removeFromFavorites(userId: String, perfumeId: String): Boolean {
        val user = FirebaseManager.getUserById(userId) ?: return false
        val updatedFavorites = user.preferitiIds.toMutableList().apply {
            remove(perfumeId)
        }
        return FirebaseManager.updateUserField(userId, "preferitiIds", updatedFavorites)
    }

    suspend fun addOrderToHistory(userId: String, order: Order): Boolean {
        val user = FirebaseManager.getUserById(userId) ?: return false
        val updatedHistory = user.storico.toMutableList().apply {
            add(order)
        }
        return FirebaseManager.updateUserField(userId, "storico", updatedHistory)
    }

    suspend fun addBadge(userId: String, badge: Badge): Boolean {
        val user = FirebaseManager.getUserById(userId) ?: return false
        val updatedBadges = user.badge.toMutableList().apply {
            if (none { it.id == badge.id }) add(badge)
        }
        return FirebaseManager.updateUserField(userId, "badge", updatedBadges)
    }

    suspend fun updateBorsellino(userId: String, amount: Double): Boolean {
        return FirebaseManager.updateUserField(userId, "borsellino", amount)
    }

    suspend fun updateConnessioneMacchinetta(userId: String, connected: Boolean, machineId: String = ""): Boolean {
        val updates = mapOf(
            "connesso" to connected,
            "macchinettaConnessaId" to machineId
        )

        val user = FirebaseManager.getUserById(userId) ?: return false
        val updatedUser = user.copy(
            connesso = connected,
            macchinettaConnessaId = machineId
        )
        return FirebaseManager.updateUser(updatedUser)
    }

    fun observeUser(userId: String): Flow<User?> {
        return FirebaseManager.observeUser(userId)
    }
}