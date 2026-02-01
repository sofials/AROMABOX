package com.example.aromabox.data.repository

import com.example.aromabox.data.model.Distributor
import com.example.aromabox.data.seed.DistributorSeedData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class DistributorRepository {

    private val database = FirebaseDatabase.getInstance()
    private val distributorsRef = database.getReference("distributors")

    fun getAllDistributors(): Flow<List<Distributor>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val distributors = snapshot.children.mapNotNull {
                    it.getValue(Distributor::class.java)
                }
                trySend(distributors)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        distributorsRef.addValueEventListener(listener)
        awaitClose { distributorsRef.removeEventListener(listener) }
    }

    suspend fun getDistributorById(distributorId: String): Distributor? {
        return try {
            val snapshot = distributorsRef.child(distributorId).get().await()
            snapshot.getValue(Distributor::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun seedDistributorsIfNeeded() {
        try {
            val snapshot = distributorsRef.get().await()
            if (!snapshot.exists() || snapshot.childrenCount == 0L) {
                val distributors = DistributorSeedData.getDistributors()
                distributors.forEach { distributor ->
                    distributorsRef.child(distributor.id).setValue(distributor).await()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun updateInventory(distributorId: String, perfumeId: String, newQuantity: Int) {
        try {
            distributorsRef
                .child(distributorId)
                .child("inventario")
                .child(perfumeId)
                .setValue(newQuantity)
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun decrementInventory(distributorId: String, perfumeId: String): Boolean {
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
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}