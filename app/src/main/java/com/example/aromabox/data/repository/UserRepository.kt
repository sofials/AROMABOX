package com.example.aromabox.data.repository

import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.data.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    fun getUserById(userId: String): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                trySend(user)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        usersRef.child(userId).addValueEventListener(listener)
        awaitClose { usersRef.child(userId).removeEventListener(listener) }
    }

    suspend fun getUserByIdOnce(userId: String): User? {
        return try {
            val snapshot = usersRef.child(userId).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUser(user: User) {
        try {
            usersRef.child(user.uid).setValue(user).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // ✅ NUOVA FUNZIONE: Aggiorna un singolo campo
    suspend fun updateUserField(userId: String, field: String, value: Any) {
        try {
            usersRef.child(userId).child(field).setValue(value).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateFavorites(userId: String, favorites: List<String>) {
        try {
            usersRef.child(userId).child("preferiti").setValue(favorites).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateProfiloOlfattivo(userId: String, profilo: ProfiloOlfattivo) {
        try {
            usersRef.child(userId).child("profiloOlfattivo").setValue(profilo).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateWallet(userId: String, newAmount: Double) {
        try {
            usersRef.child(userId).child("wallet").setValue(newAmount).await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updateConnectionStatus(userId: String, isConnected: Boolean) {
        try {
            usersRef.child(userId).child("isConnected").setValue(isConnected).await()
        } catch (e: Exception) {
            throw e
        }
    }
}