package com.example.aromabox.data.model

data class User(
    val uid: String = "",  // ✅ AGGIUNGI questo campo
    val email: String = "",
    val nome: String = "",
    val cognome: String = "",
    val photoUrl: String? = null,
    val isConnected: Boolean = false,
    val wallet: Double = 0.0,
    val profiloOlfattivo: ProfiloOlfattivo? = null,
    val preferiti: List<String> = emptyList(),
    val ordini: List<Order> = emptyList(),
    val badges: List<Badge> = emptyList()
)