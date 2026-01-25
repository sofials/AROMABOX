package com.example.aromabox.data.repository

import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.seed.PerfumeSeedData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PerfumeRepository {

    private val database = FirebaseDatabase.getInstance()
    private val perfumesRef = database.getReference("perfumes")

    // ✅ Seed automatico al primo avvio
    suspend fun seedPerfumesIfNeeded() {
        try {
            val snapshot = perfumesRef.get().await()

            // Se il database è vuoto, popola con i dati di seed
            if (!snapshot.exists()) {
                val seedPerfumes = PerfumeSeedData.getSeedPerfumes()

                seedPerfumes.forEach { perfume ->
                    perfumesRef.child(perfume.id).setValue(perfume).await()
                }

                println("✅ Database popolato con ${seedPerfumes.size} profumi")
            } else {
                println("ℹ️ Database già popolato")
            }
        } catch (e: Exception) {
            println("❌ Errore nel seed: ${e.message}")
        }
    }

    fun getAllPerfumes(): Flow<List<Perfume>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val perfumes = snapshot.children.mapNotNull {
                    it.getValue(Perfume::class.java)
                }
                trySend(perfumes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        perfumesRef.addValueEventListener(listener)
        awaitClose { perfumesRef.removeEventListener(listener) }
    }

    fun getPerfumeById(id: String): Flow<Perfume?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val perfume = snapshot.getValue(Perfume::class.java)
                trySend(perfume)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        perfumesRef.child(id).addValueEventListener(listener)
        awaitClose { perfumesRef.child(id).removeEventListener(listener) }
    }
}