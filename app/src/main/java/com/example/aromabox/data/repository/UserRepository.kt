package com.example.aromabox.data.repository

import com.example.aromabox.data.model.Badge
import com.example.aromabox.data.model.BadgeDefinitions
import com.example.aromabox.data.model.BadgeRequirementType
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
import java.security.MessageDigest

class UserRepository {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    // ==================== USER METHODS ====================

    fun getUserById(userId: String): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = parseUserFromSnapshot(snapshot)
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
            parseUserFromSnapshot(snapshot)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parsing manuale dell'utente per gestire la conversione HashMap → List
     */
    private fun parseUserFromSnapshot(snapshot: DataSnapshot): User? {
        if (!snapshot.exists()) return null

        return try {
            val uid = snapshot.child("uid").getValue(String::class.java) ?: ""
            val email = snapshot.child("email").getValue(String::class.java) ?: ""
            val nome = snapshot.child("nome").getValue(String::class.java) ?: ""
            val cognome = snapshot.child("cognome").getValue(String::class.java) ?: ""
            val photoUrl = snapshot.child("photoUrl").getValue(String::class.java)
            val wallet = snapshot.child("wallet").getValue(Double::class.java) ?: 0.0
            val isConnected = snapshot.child("isConnected").getValue(Boolean::class.java) ?: false

            // Parse preferiti (può essere List o HashMap)
            val preferiti = parseStringList(snapshot.child("preferiti"))

            // Parse profiloOlfattivo
            val profiloOlfattivo = parseProfiloOlfattivo(snapshot.child("profiloOlfattivo"))

            // Parse badges
            val badges = parseBadgesList(snapshot.child("badges"))

            // Parse statistiche per i badge
            val totaleAcquisti = snapshot.child("totaleAcquisti").getValue(Int::class.java) ?: 0
            val totaleRecensioni = snapshot.child("totaleRecensioni").getValue(Int::class.java) ?: 0
            val totaleErogazioni = snapshot.child("totaleErogazioni").getValue(Int::class.java) ?: 0

            User(
                uid = uid,
                email = email,
                nome = nome,
                cognome = cognome,
                photoUrl = photoUrl,
                wallet = wallet,
                preferiti = preferiti,
                profiloOlfattivo = profiloOlfattivo,
                isConnected = isConnected,
                badges = badges,
                totaleAcquisti = totaleAcquisti,
                totaleRecensioni = totaleRecensioni,
                totaleErogazioni = totaleErogazioni
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Converte un DataSnapshot in List<String>, gestendo sia List che HashMap
     */
    private fun parseStringList(snapshot: DataSnapshot): List<String> {
        if (!snapshot.exists()) return emptyList()

        return try {
            val result = mutableListOf<String>()
            for (child in snapshot.children) {
                child.getValue(String::class.java)?.let { result.add(it) }
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Parsing manuale del ProfiloOlfattivo
     */
    private fun parseProfiloOlfattivo(snapshot: DataSnapshot): ProfiloOlfattivo? {
        if (!snapshot.exists()) return null

        return try {
            ProfiloOlfattivo(
                noteFloreali = parseStringList(snapshot.child("noteFloreali")),
                noteFruttate = parseStringList(snapshot.child("noteFruttate")),
                noteSpeziate = parseStringList(snapshot.child("noteSpeziate")),
                noteGourmand = parseStringList(snapshot.child("noteGourmand")),
                noteLegnose = parseStringList(snapshot.child("noteLegnose"))
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Parsing della lista badge
     */
    private fun parseBadgesList(snapshot: DataSnapshot): List<Badge> {
        if (!snapshot.exists()) return emptyList()

        return try {
            val result = mutableListOf<Badge>()
            for (child in snapshot.children) {
                parseBadgeFromSnapshot(child)?.let { result.add(it) }
            }
            result
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Parsing di un singolo badge
     */
    private fun parseBadgeFromSnapshot(snapshot: DataSnapshot): Badge? {
        if (!snapshot.exists()) return null

        return try {
            Badge(
                id = snapshot.child("id").getValue(String::class.java) ?: "",
                nome = snapshot.child("nome").getValue(String::class.java) ?: "",
                descrizione = snapshot.child("descrizione").getValue(String::class.java) ?: "",
                category = snapshot.child("category").getValue(String::class.java) ?: "",
                livello = snapshot.child("livello").getValue(Int::class.java) ?: 1,
                iconResName = snapshot.child("iconResName").getValue(String::class.java) ?: "ic_badge_placeholder",
                dataOttenimento = snapshot.child("dataOttenimento").getValue(Long::class.java) ?: 0L,
                isUnlocked = snapshot.child("isUnlocked").getValue(Boolean::class.java) ?: false
            )
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
            // Salva come Map per evitare problemi di serializzazione
            val profiloMap = mapOf(
                "noteFloreali" to profilo.noteFloreali,
                "noteFruttate" to profilo.noteFruttate,
                "noteSpeziate" to profilo.noteSpeziate,
                "noteGourmand" to profilo.noteGourmand,
                "noteLegnose" to profilo.noteLegnose
            )
            usersRef.child(userId).child("profiloOlfattivo").setValue(profiloMap).await()
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

    // ==================== BADGE METHODS ====================

    /**
     * Salva un badge sbloccato per l'utente
     */
    suspend fun unlockBadge(userId: String, badge: Badge): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val badgeMap = mapOf(
                "id" to badge.id,
                "nome" to badge.nome,
                "descrizione" to badge.descrizione,
                "category" to badge.category,
                "livello" to badge.livello,
                "iconResName" to badge.iconResName,
                "dataOttenimento" to badge.dataOttenimento,
                "isUnlocked" to badge.isUnlocked
            )
            usersRef.child(userId).child("badges").child(badge.id).setValue(badgeMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Recupera tutti i badge sbloccati dell'utente
     */
    suspend fun getUnlockedBadges(userId: String): List<Badge> = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).child("badges").get().await()
            parseBadgesList(snapshot).filter { it.isUnlocked }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Verifica se un badge specifico è sbloccato
     */
    suspend fun isBadgeUnlocked(userId: String, badgeId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).child("badges").child(badgeId).get().await()
            snapshot.child("isUnlocked").getValue(Boolean::class.java) ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Incrementa il contatore acquisti e verifica badge
     */
    suspend fun incrementAcquisti(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).child("totaleAcquisti").get().await()
            val current = snapshot.getValue(Int::class.java) ?: 0
            val newValue = current + 1
            usersRef.child(userId).child("totaleAcquisti").setValue(newValue).await()
            newValue
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Incrementa il contatore recensioni
     */
    suspend fun incrementRecensioni(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).child("totaleRecensioni").get().await()
            val current = snapshot.getValue(Int::class.java) ?: 0
            val newValue = current + 1
            usersRef.child(userId).child("totaleRecensioni").setValue(newValue).await()
            newValue
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Incrementa il contatore erogazioni
     */
    suspend fun incrementErogazioni(userId: String): Int = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).child("totaleErogazioni").get().await()
            val current = snapshot.getValue(Int::class.java) ?: 0
            val newValue = current + 1
            usersRef.child(userId).child("totaleErogazioni").setValue(newValue).await()
            newValue
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Recupera le statistiche utente per i badge
     */
    suspend fun getUserStats(userId: String): UserStats = withContext(Dispatchers.IO) {
        try {
            val snapshot = usersRef.child(userId).get().await()
            UserStats(
                totaleAcquisti = snapshot.child("totaleAcquisti").getValue(Int::class.java) ?: 0,
                totaleRecensioni = snapshot.child("totaleRecensioni").getValue(Int::class.java) ?: 0,
                totaleErogazioni = snapshot.child("totaleErogazioni").getValue(Int::class.java) ?: 0,
                totalePreferiti = parseStringList(snapshot.child("preferiti")).size
            )
        } catch (e: Exception) {
            UserStats()
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
                    parseOrderFromSnapshot(orderSnapshot)?.let { ordersList.add(it) }
                }
                // Ordina per timestamp decrescente (più recenti prima)
                trySend(ordersList.sortedByDescending { it.timestamp })
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
     * Parsing manuale dell'ordine
     */
    private fun parseOrderFromSnapshot(snapshot: DataSnapshot): Order? {
        if (!snapshot.exists()) return null

        return try {
            Order(
                orderId = snapshot.child("orderId").getValue(String::class.java) ?: snapshot.key ?: "",
                perfumeId = snapshot.child("perfumeId").getValue(String::class.java) ?: "",
                perfumeName = snapshot.child("perfumeName").getValue(String::class.java) ?: "",
                perfumeBrand = snapshot.child("perfumeBrand").getValue(String::class.java) ?: "",
                perfumeImageUrl = snapshot.child("perfumeImageUrl").getValue(String::class.java) ?: "",
                price = snapshot.child("price").getValue(Double::class.java) ?: 0.0,
                timestamp = snapshot.child("timestamp").getValue(Long::class.java) ?: 0L,
                distributorId = snapshot.child("distributorId").getValue(String::class.java) ?: "",
                distributorName = snapshot.child("distributorName").getValue(String::class.java) ?: "",
                pin = snapshot.child("pin").getValue(String::class.java) ?: "",
                ritirato = snapshot.child("ritirato").getValue(Boolean::class.java) ?: false,
                dataRitiro = snapshot.child("dataRitiro").getValue(Long::class.java)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Aggiunge un nuovo ordine all'utente.
     * Genera il PIN usando una funzione hash SHA-256 basata sui dati dell'ordine.
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

            val timestamp = System.currentTimeMillis()

            // Genera PIN usando funzione hash SHA-256
            val pin = generatePinFromHash(
                orderId = orderId,
                userId = userId,
                perfumeId = perfume.id,
                timestamp = timestamp
            )

            val order = Order(
                orderId = orderId,
                perfumeId = perfume.id,
                perfumeName = perfume.nome,
                perfumeBrand = perfume.marca,
                perfumeImageUrl = perfume.imageUrl,
                price = perfume.prezzo,
                timestamp = timestamp,
                distributorId = distributorId,
                distributorName = distributorName,
                pin = pin,
                ritirato = false,
                dataRitiro = null
            )

            // Salva come Map per evitare problemi di serializzazione
            val orderMap = mapOf(
                "orderId" to order.orderId,
                "perfumeId" to order.perfumeId,
                "perfumeName" to order.perfumeName,
                "perfumeBrand" to order.perfumeBrand,
                "perfumeImageUrl" to order.perfumeImageUrl,
                "price" to order.price,
                "timestamp" to order.timestamp,
                "distributorId" to order.distributorId,
                "distributorName" to order.distributorName,
                "pin" to order.pin,
                "ritirato" to order.ritirato,
                "dataRitiro" to order.dataRitiro
            )

            newOrderRef.setValue(orderMap).await()
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Genera un PIN a 6 cifre usando SHA-256.
     * Il PIN è deterministico: gli stessi input producono sempre lo stesso PIN.
     *
     * @param orderId ID univoco dell'ordine
     * @param userId ID dell'utente
     * @param perfumeId ID del profumo
     * @param timestamp Timestamp della creazione dell'ordine
     * @return PIN a 6 cifre come stringa
     */
    private fun generatePinFromHash(
        orderId: String,
        userId: String,
        perfumeId: String,
        timestamp: Long
    ): String {
        // Combina i dati univoci dell'ordine
        val dataToHash = "$orderId-$userId-$perfumeId-$timestamp"

        // Calcola hash SHA-256
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(dataToHash.toByteArray())

        // Converti i primi 4 byte dell'hash in un numero intero positivo
        val hashInt = ((hashBytes[0].toInt() and 0xFF) shl 24) or
                ((hashBytes[1].toInt() and 0xFF) shl 16) or
                ((hashBytes[2].toInt() and 0xFF) shl 8) or
                (hashBytes[3].toInt() and 0xFF)

        // Prendi il valore assoluto e riduci a 6 cifre (100000-999999)
        val pinNumber = 100000 + (kotlin.math.abs(hashInt) % 900000)

        return pinNumber.toString()
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
                parseOrderFromSnapshot(snapshot)
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
                    parseOrderFromSnapshot(orderSnapshot)?.let { ordersList.add(it) }
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
                    parseOrderFromSnapshot(orderSnapshot)?.let { ordersList.add(it) }
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

/**
 * Data class per le statistiche utente relative ai badge
 */
data class UserStats(
    val totaleAcquisti: Int = 0,
    val totaleRecensioni: Int = 0,
    val totaleErogazioni: Int = 0,
    val totalePreferiti: Int = 0
)