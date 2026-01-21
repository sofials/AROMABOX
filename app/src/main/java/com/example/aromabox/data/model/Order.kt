package com.example.aromabox.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val profumoId: String = "",
    val profumoNome: String = "",
    val profumoBrand: String = "",
    val macchinettaId: String = "",
    val macchinettaNome: String = "",
    val slot: Int = 0,
    val pin: String = "",
    val frequenze: List<Int> = emptyList(),
    val dataAcquisto: Long = 0L,
    val erogato: Boolean = false,
    val dataErogazione: Long = 0L
) {
    constructor() : this("", "", "", "", "", "", "", 0, "", emptyList(), 0L, false, 0L)
}