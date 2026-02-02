package com.example.aromabox.data.model

/**
 * Modello per gli ordini/acquisti dell'utente.
 *
 * Un ordine viene creato quando l'utente acquista un profumo dal catalogo.
 * Lo stato "ritirato" indica se l'utente ha già prelevato il prodotto
 * dal distributore usando il PIN generato.
 */
data class Order(
    val orderId: String = "",
    val perfumeId: String = "",
    val perfumeName: String = "",
    val perfumeBrand: String = "",
    val perfumeImageUrl: String = "",
    val price: Double = 0.0,
    val timestamp: Long = 0L,           // Data acquisto (milliseconds)
    val distributorId: String = "",
    val distributorName: String = "",
    val pin: String = "",               // PIN per ritiro alla macchinetta
    val ritirato: Boolean = false,      // true = già prelevato, false = da ritirare
    val dataRitiro: Long? = null        // Data del ritiro (se ritirato)
) {
    // Costruttore vuoto richiesto da Firebase
    constructor() : this(
        orderId = "",
        perfumeId = "",
        perfumeName = "",
        perfumeBrand = "",
        perfumeImageUrl = "",
        price = 0.0,
        timestamp = 0L,
        distributorId = "",
        distributorName = "",
        pin = "",
        ritirato = false,
        dataRitiro = null
    )

    /**
     * Formatta la data di acquisto in formato italiano (dd/MM/yyyy)
     */
    fun getFormattedDate(): String {
        if (timestamp == 0L) return ""
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALY)
        return sdf.format(java.util.Date(timestamp))
    }

    /**
     * Formatta la data di ritiro in formato italiano (dd/MM/yyyy)
     */
    fun getFormattedDataRitiro(): String {
        if (dataRitiro == null || dataRitiro == 0L) return ""
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.ITALY)
        return sdf.format(java.util.Date(dataRitiro))
    }

    /**
     * Formatta il prezzo con simbolo euro
     */
    fun getFormattedPrice(): String {
        return String.format(java.util.Locale.ITALY, "%.2f €", price)
    }
}