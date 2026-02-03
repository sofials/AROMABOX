package com.example.aromabox.data.model

import com.google.firebase.database.Exclude

data class Distributor(
    val id: String = "",
    val nome: String = "",
    val indirizzo: String = "",
    val citta: String = "",
    val cap: String = "",
    val provincia: String = "",
    val latitudine: Double = 0.0,
    val longitudine: Double = 0.0,
    val attivo: Boolean = false,
    val inventario: Map<String, Int> = emptyMap()
) {
    @Exclude
    fun getIndirizzoCompleto(): String {
        return "$indirizzo, $cap $citta ($provincia)"
    }

    @Exclude
    fun getDisponibilita(perfumeId: String): Int {
        return inventario[perfumeId] ?: 0
    }
}