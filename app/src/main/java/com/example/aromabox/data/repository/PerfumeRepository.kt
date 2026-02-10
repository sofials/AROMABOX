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


    suspend fun seedPerfumesIfNeeded() {
        try {
            perfumesRef.removeValue().await()
            println("🟡 Nodo perfumes cancellato")

            val seedPerfumes = PerfumeSeedData.getSeedPerfumes()
            seedPerfumes.forEach { perfume ->
                val perfumeMap = mapOf(
                    "id" to perfume.id,
                    "nome" to perfume.nome,
                    "marca" to perfume.marca,
                    "prezzo" to perfume.prezzo,
                    "categoria" to perfume.categoria,
                    "genere" to perfume.genere,
                    "disponibile" to perfume.disponibile,
                    "slot" to perfume.slot,
                    "noteOlfattive" to mapOf(
                        "noteDiTesta" to perfume.noteOlfattive.noteDiTesta,
                        "noteDiCuore" to perfume.noteOlfattive.noteDiCuore,
                        "noteDiFondo" to perfume.noteOlfattive.noteDiFondo
                    ),
                    "imageUrl" to perfume.imageUrl
                )
                perfumesRef.child(perfume.id).setValue(perfumeMap).await()
                println("🟡 Scritto: ${perfume.nome} - prezzo: ${perfume.prezzo}")
            }

            println("✅ Seed completato con ${seedPerfumes.size} profumi")
        } catch (e: Exception) {
            println("❌ ERRORE SEED: ${e.message}")
            e.printStackTrace()
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