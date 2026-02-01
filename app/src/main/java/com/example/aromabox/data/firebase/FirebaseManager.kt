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
    private val distributorsRef = database.child("distributors")  // ✅ Cambiato da "macchinette"
    private val ordersRef = database.child("erogazioni")

    // ========== USER OPERATIONS ==========

    suspend fun getUserById(uid: String): User? {
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
            usersRef.child(user.uid).setValue(user).await()
            Log.d(TAG, "User created: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error creating user", e)
            false
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            usersRef.child(user.uid).setValue(user).await()
            Log.d(TAG, "User updated: ${user.uid}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user", e)
            false
        }
    }

    suspend fun updateUserField(uid: String, field: String, value: Any): Boolean {
        return try {
            usersRef.child(uid).child(field).setValue(value).await()
            Log.d(TAG, "User field updated: $field")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating user field", e)
            false
        }
    }

    fun observeUser(uid: String): Flow<User?> = callbackFlow {
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

    // ✅ Aggiornato per usare Distributor
    suspend fun getPerfumesByDistributor(distributorId: String): List<Perfume> {
        return try {
            // Prima ottieni il distributore per vedere quali profumi ha in inventario
            val distributor = getDistributorById(distributorId)
            if (distributor != null) {
                val perfumeIds = distributor.inventario.filter { it.value > 0 }.keys
                val allPerfumes = getAllPerfumes()
                allPerfumes.filter { perfumeIds.contains(it.id) }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting perfumes by distributor", e)
            emptyList()
        }
    }

    // ========== DISTRIBUTOR OPERATIONS ==========

    suspend fun getAllDistributors(): List<Distributor> {
        return try {
            val snapshot = distributorsRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Distributor::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting distributors", e)
            emptyList()
        }
    }

    suspend fun getDistributorById(distributorId: String): Distributor? {
        return try {
            val snapshot = distributorsRef.child(distributorId).get().await()
            snapshot.getValue(Distributor::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting distributor", e)
            null
        }
    }

    fun observeDistributors(): Flow<List<Distributor>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val distributors = snapshot.children.mapNotNull {
                    it.getValue(Distributor::class.java)
                }
                trySend(distributors)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error observing distributors", error.toException())
                close(error.toException())
            }
        }

        distributorsRef.addValueEventListener(listener)

        awaitClose {
            distributorsRef.removeEventListener(listener)
        }
    }

    fun observeDistributor(distributorId: String): Flow<Distributor?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val distributor = snapshot.getValue(Distributor::class.java)
                trySend(distributor)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Error observing distributor", error.toException())
                close(error.toException())
            }
        }

        distributorsRef.child(distributorId).addValueEventListener(listener)

        awaitClose {
            distributorsRef.child(distributorId).removeEventListener(listener)
        }
    }

    suspend fun updateDistributorInventory(
        distributorId: String,
        perfumeId: String,
        newQuantity: Int
    ): Boolean {
        return try {
            distributorsRef
                .child(distributorId)
                .child("inventario")
                .child(perfumeId)
                .setValue(newQuantity)
                .await()
            Log.d(TAG, "Distributor inventory updated: $distributorId - $perfumeId = $newQuantity")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating distributor inventory", e)
            false
        }
    }

    suspend fun decrementDistributorInventory(
        distributorId: String,
        perfumeId: String
    ): Boolean {
        return try {
            val snapshot = distributorsRef
                .child(distributorId)
                .child("inventario")
                .child(perfumeId)
                .get()
                .await()

            val currentQty = snapshot.getValue(Int::class.java) ?: 0
            if (currentQty > 0) {
                distributorsRef
                    .child(distributorId)
                    .child("inventario")
                    .child(perfumeId)
                    .setValue(currentQty - 1)
                    .await()
                Log.d(TAG, "Inventory decremented: $distributorId - $perfumeId = ${currentQty - 1}")
                true
            } else {
                Log.w(TAG, "Cannot decrement: inventory is 0")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error decrementing inventory", e)
            false
        }
    }

    suspend fun seedDistributors(distributors: List<Distributor>): Boolean {
        return try {
            val snapshot = distributorsRef.get().await()
            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                distributors.forEach { distributor ->
                    distributorsRef.child(distributor.id).setValue(distributor).await()
                }
                Log.d(TAG, "Distributors seeded: ${distributors.size}")
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding distributors", e)
            false
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

    suspend fun getUserOrders(uid: String): List<Order> {
        return try {
            val snapshot = ordersRef
                .orderByChild("uid")
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

    // ========== ACTIVE DISTRIBUTORS HELPER ==========

    suspend fun getActiveDistributors(): List<Distributor> {
        return try {
            val snapshot = distributorsRef
                .orderByChild("attivo")
                .equalTo(true)
                .get()
                .await()
            snapshot.children.mapNotNull { it.getValue(Distributor::class.java) }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting active distributors", e)
            emptyList()
        }
    }

    suspend fun getDistributorsWithPerfume(perfumeId: String): List<Distributor> {
        return try {
            val allDistributors = getAllDistributors()
            allDistributors.filter { distributor ->
                distributor.attivo && distributor.getDisponibilita(perfumeId) > 0
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting distributors with perfume", e)
            emptyList()
        }
    }
}