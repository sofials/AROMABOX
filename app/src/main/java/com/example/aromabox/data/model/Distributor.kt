package com.example.aromabox.data.model

data class Distributor(
    val id: String = "",
    val nome: String = "",
    val indirizzo: String = "",
    val citta: String = "",
    val cap: String = "",
    val provincia: String = "",
    val latitudine: Double = 0.0,
    val longitudine: Double = 0.0,
    val attivo: Boolean = false,  // Solo il nostro sarà cliccabile
    val inventario: Map<String, Int> = emptyMap()  // perfumeId -> quantità disponibile
) {
    fun getIndirizzoCompleto(): String {
        return "$indirizzo, $cap $citta ($provincia)"
    }

    fun getDisponibilita(perfumeId: String): Int {
        return inventario[perfumeId] ?: 0
    }
}