package com.example.aromabox.data.model

data class Badge(
    val id: String = "",
    val nome: String = "",
    val descrizione: String = "",
    val iconaUrl: String = "",
    val dataOttenimento: Long = 0L
) {
    constructor() : this("", "", "", "", 0L)
}