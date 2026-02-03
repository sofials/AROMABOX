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
                val distributors = snapshot.children.mapNotNull { childSnapshot ->
                    parseDistributorFromSnapshot(childSnapshot)
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

    /**
     * Parsing manuale del Distributor per evitare problemi di deserializzazione Firebase
     */
    private fun parseDistributorFromSnapshot(snapshot: DataSnapshot): Distributor? {
        if (!snapshot.exists()) return null

        return try {
            val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: ""
            val nome = snapshot.child("nome").getValue(String::class.java) ?: ""
            val indirizzo = snapshot.child("indirizzo").getValue(String::class.java) ?: ""
            val citta = snapshot.child("citta").getValue(String::class.java) ?: ""
            val cap = snapshot.child("cap").getValue(String::class.java) ?: ""
            val provincia = snapshot.child("provincia").getValue(String::class.java) ?: ""
            val latitudine = snapshot.child("latitudine").getValue(Double::class.java) ?: 0.0
            val longitudine = snapshot.child("longitudine").getValue(Double::class.java) ?: 0.0
            val attivo = snapshot.child("attivo").getValue(Boolean::class.java) ?: false

            // Parse inventario manualmente (Map<String, Int>)
            val inventario = parseInventario(snapshot.child("inventario"))

            Distributor(
                id = id,
                nome = nome,
                indirizzo = indirizzo,
                citta = citta,
                cap = cap,
                provincia = provincia,
                latitudine = latitudine,
                longitudine = longitudine,
                attivo = attivo,
                inventario = inventario
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Parse inventario da Firebase (gestisce sia Map che strutture diverse)
     */
    private fun parseInventario(snapshot: DataSnapshot): Map<String, Int> {
        if (!snapshot.exists()) return emptyMap()

        val result = mutableMapOf<String, Int>()
        for (child in snapshot.children) {
            val key = child.key ?: continue
            val value = child.getValue(Int::class.java) ?: child.getValue(Long::class.java)?.toInt() ?: 0
            result[key] = value
        }
        return result
    }

    suspend fun getDistributorById(distributorId: String): Distributor? {
        return try {
            val snapshot = distributorsRef.child(distributorId).get().await()
            parseDistributorFromSnapshot(snapshot)
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
                    // Salva come Map per evitare problemi di serializzazione
                    val distributorMap = mapOf(
                        "id" to distributor.id,
                        "nome" to distributor.nome,
                        "indirizzo" to distributor.indirizzo,
                        "citta" to distributor.citta,
                        "cap" to distributor.cap,
                        "provincia" to distributor.provincia,
                        "latitudine" to distributor.latitudine,
                        "longitudine" to distributor.longitudine,
                        "attivo" to distributor.attivo,
                        "inventario" to distributor.inventario
                    )
                    distributorsRef.child(distributor.id).setValue(distributorMap).await()
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

            val currentQty = snapshot.getValue(Int::class.java)
                ?: snapshot.getValue(Long::class.java)?.toInt()
                ?: 0

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
            e.printStackTrace()
            false
        }
    }
}