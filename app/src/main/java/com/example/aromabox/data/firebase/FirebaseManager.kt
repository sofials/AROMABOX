package com.example.aromabox.data.firebase

import android.util.Log
import com.example.aromabox.data.model.*
import com.google.firebase.database.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    // Riferimenti alle collezioni
    private val usersRef = database.child("users")
    private val perfumesRef = database.child("profumi")
    private val machinesRef = database.child("macchinette")
    private val ordersRef = database.child("erogazioni")

    // ========== USER OPERATIONS ==========

    suspend fun getUserById(uid: String): User? {  // ✅ Cambiato da userId a uid
        return try {
            val snapshot = usersRef.child(uid).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user", e)
            null
        }
    }

    suspend fun createUser(user: User): Boolean {
        return try {
            usersRef.child(user.uid).setValue(user).await()  // ✅ user.uid invece di user.userId
            Log.d(TAG, "User created: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user", e)
            false
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            usersRef.child(user.uid).setValue(user).await()  // ✅ user.uid
            Log.d(TAG, "User updated: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user", e)
            false
        }
    }

    suspend fun updateUserField(uid: String, field: String, value: Any): Boolean {  // ✅ uid
        return try {
            usersRef.child(uid).child(field).setValue(value).await()
            Log.d(TAG, "User field updated: $field")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user field", e)
            false
        }
    }

    fun observeUser(uid: String): Flow<User?> = callbackFlow {  // ✅ uid
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                trySend(user)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error observing user", error.toException())
                close(error.toException())
            }
        }

        usersRef.child(uid).addValueEventListener(listener)

        awaitClose {
            usersRef.child(uid).removeEventListener(listener)
        }
    }

    // ========== PERFUME OPERATIONS ==========

    suspend fun getAllPerfumes(): List<Perfume> {
        return try {
            val snapshot = perfumesRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Perfume::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting perfumes", e)
            emptyList()
        }
    }

    suspend fun getPerfumeById(perfumeId: String): Perfume? {
        return try {
            val snapshot = perfumesRef.child(perfumeId).get().await()
            snapshot.getValue(Perfume::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting perfume", e)
            null
        }
    }

    suspend fun getPerfumesByMachine(machineId: String): List<Perfume> {
        return try {
            val snapshot = perfumesRef
                .orderByChild("macchinettaId")
                .equalTo(machineId)
                .get()
                .await()
            snapshot.children.mapNotNull { it.getValue(Perfume::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting perfumes by machine", e)
            emptyList()
        }
    }

    // ========== MACHINE OPERATIONS ==========

    suspend fun getAllMachines(): List<Machine> {
        return try {
            val snapshot = machinesRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Machine::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting machines", e)
            emptyList()
        }
    }

    suspend fun getMachineById(machineId: String): Machine? {
        return try {
            val snapshot = machinesRef.child(machineId).get().await()
            snapshot.getValue(Machine::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting machine", e)
            null
        }
    }

    // ========== ORDER OPERATIONS ==========

    suspend fun createOrder(order: Order): Boolean {
        return try {
            ordersRef.child(order.pin).setValue(order).await()
            Log.d(TAG, "Order created: ${order.pin}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating order", e)
            false
        }
    }

    suspend fun getOrderByPin(pin: String): Order? {
        return try {
            val snapshot = ordersRef.child(pin).get().await()
            snapshot.getValue(Order::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting order", e)
            null
        }
    }

    suspend fun getUserOrders(uid: String): List<Order> {  // ✅ uid
        return try {
            val snapshot = ordersRef
                .orderByChild("uid")  // ✅ Assicurati che Order abbia uid, non userId
                .equalTo(uid)
                .get()
                .await()
            snapshot.children.mapNotNull { it.getValue(Order::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user orders", e)
            emptyList()
        }
    }

    suspend fun updateOrderStatus(pin: String, erogato: Boolean): Boolean {
        return try {
            val updates = mapOf(
                "erogato" to erogato,
                "dataErogazione" to if (erogato) System.currentTimeMillis() else 0L
            )
            ordersRef.child(pin).updateChildren(updates).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating order status", e)
            false
        }
    }
}