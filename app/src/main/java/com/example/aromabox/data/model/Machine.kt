package com.example.aromabox.data.model

data class Machine(
    val macchinettaId: String = "",
    val nome: String = "",
    val indirizzo: String = "",
    val citta: String = "",
    val latitudine: Double = 0.0,
    val longitudine: Double = 0.0,
    val connessa: Boolean = false,
    val ultimaConnessione: Long = 0L,
    val slots: Map<Int, String> = emptyMap(),
    val capacita: Int = 2
) {
    constructor() : this("", "", "", "", 0.0, 0.0, false, 0L, emptyMap(), 2)

    fun haSlotDisponibili(): Boolean = slots.size < capacita

    fun numeroProfumi(): Int = slots.size
}