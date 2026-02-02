package com.example.aromabox.data.repository

import com.example.aromabox.data.model.Order
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.data.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserRepository {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    // ==================== USER METHODS ====================

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

    // ==================== ORDER METHODS ====================

    /**
     * Recupera gli ordini dell'utente come Flow.
     * Utilizza un listener real-time per aggiornamenti automatici.
     */
    fun getUserOrders(userId: String): Flow<List<Order>> = callbackFlow {
        val ordersRef = usersRef.child(userId).child("ordini")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ordersList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { ordersList.add(it) }
                }
                trySend(ordersList)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        ordersRef.addValueEventListener(listener)

        awaitClose {
            ordersRef.removeEventListener(listener)
        }
    }

    /**
     * Aggiunge un nuovo ordine all'utente.
     * Genera automaticamente un PIN casuale per il ritiro.
     */
    suspend fun addOrder(
        userId: String,
        perfume: Perfume,
        distributorId: String,
        distributorName: String
    ): Result<Order> = withContext(Dispatchers.IO) {
        try {
            val ordersRef = usersRef.child(userId).child("ordini")
            val newOrderRef = ordersRef.push()
            val orderId = newOrderRef.key
                ?: return@withContext Result.failure(Exception("Impossibile generare ID ordine"))

            // Genera PIN casuale a 6 cifre
            val pin = (100000..999999).random().toString()

            val order = Order(
                orderId = orderId,
                perfumeId = perfume.id,
                perfumeName = perfume.nome,
                perfumeBrand = perfume.marca,
                perfumeImageUrl = perfume.imageUrl,
                price = perfume.prezzo,
                timestamp = System.currentTimeMillis(),
                distributorId = distributorId,
                distributorName = distributorName,
                pin = pin,
                ritirato = false,
                dataRitiro = null
            )

            newOrderRef.setValue(order).await()
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Segna un ordine come ritirato.
     */
    suspend fun markOrderAsRitirato(userId: String, orderId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                val orderRef = usersRef.child(userId).child("ordini").child(orderId)
                val updates = mapOf(
                    "ritirato" to true,
                    "dataRitiro" to System.currentTimeMillis()
                )
                orderRef.updateChildren(updates).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Recupera un singolo ordine per ID.
     */
    suspend fun getOrderById(userId: String, orderId: String): Order? =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = usersRef.child(userId).child("ordini").child(orderId).get().await()
                snapshot.getValue(Order::class.java)
            } catch (e: Exception) {
                null
            }
        }

    /**
     * Recupera tutti gli ordini da ritirare (non ancora ritirati).
     */
    suspend fun getOrdersDaRitirare(userId: String): List<Order> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = usersRef.child(userId).child("ordini")
                    .orderByChild("ritirato")
                    .equalTo(false)
                    .get()
                    .await()

                val ordersList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { ordersList.add(it) }
                }
                ordersList.sortedByDescending { it.timestamp }
            } catch (e: Exception) {
                emptyList()
            }
        }

    /**
     * Recupera tutti gli ordini già ritirati.
     */
    suspend fun getOrdersRitirati(userId: String): List<Order> =
        withContext(Dispatchers.IO) {
            try {
                val snapshot = usersRef.child(userId).child("ordini")
                    .orderByChild("ritirato")
                    .equalTo(true)
                    .get()
                    .await()

                val ordersList = mutableListOf<Order>()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    order?.let { ordersList.add(it) }
                }
                ordersList.sortedByDescending { it.dataRitiro }
            } catch (e: Exception) {
                emptyList()
            }
        }

    /**
     * Conta il numero totale di ordini dell'utente.
     */
    suspend fun countOrders(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).child("ordini").get().await()
            snapshot.childrenCount.toInt()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Elimina un ordine (opzionale, per admin o casi speciali).
     */
    suspend fun deleteOrder(userId: String, orderId: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            try {
                usersRef.child(userId).child("ordini").child(orderId).removeValue().await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}