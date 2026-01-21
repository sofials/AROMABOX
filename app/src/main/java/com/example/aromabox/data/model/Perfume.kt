package com.example.aromabox.data.model

data class Perfume(
    val profumoId: String = "",
    val nome: String = "",
    val brand: String = "",
    val prezzo: Double = 0.0,
    val descrizione: String = "",
    val areaOlfattiva: String = "",
    val piramideOlfattiva: PiramideOlfattiva = PiramideOlfattiva(),
    val imageUrl: String = "",
    val macchinettaId: String = "",
    val macchinettaNome: String = "",
    val slot: Int = 0,
    val disponibile: Boolean = true,
    val inErogazione: Boolean = false
) {
    constructor() : this("", "", "", 0.0, "", "", PiramideOlfattiva(), "", "", "", 0, true, false)

    fun prezzoFormattato(): String = "€ %.2f".format(prezzo)
}