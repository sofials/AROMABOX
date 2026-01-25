package com.example.aromabox.data.model

data class Order(
    val orderId: String = "",
    val uid: String = "",  // ✅ Cambiato da userId a uid per consistenza
    val perfumeId: String = "",
    val perfumeName: String = "",
    val price: Double = 0.0,
    val timestamp: Long = 0L,
    val distributorId: String = "",
    val pin: String = "",  // ✅ Aggiungi se manca
    val erogato: Boolean = false,  // ✅ Aggiungi se manca
    val dataErogazione: Long = 0L  // ✅ Aggiungi se manca
)