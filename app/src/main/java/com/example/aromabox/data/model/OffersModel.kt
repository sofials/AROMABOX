package com.example.aromabox.data.model

/**
 * Model singleton per le offerte.
 * Seguendo le linee guida del docente:
 * - Il Model è la fonte di verità per i dati
 * - Pattern singleton (object Kotlin)
 * - Indipendente da View e componenti Android
 * - Espone dati in formato neutrale
 */
object OffersModel {

    private val offers = listOf(
        Offer(
            id = "offer_1",
            brand = "Dior",
            discountPercent = 10,
            description = "Su tutti i prodotti Dior",
            imageRes = "logo_dior"
        ),
        Offer(
            id = "offer_2",
            brand = "Yves Saint Laurent",
            discountPercent = 5,
            description = "Su tutti i prodotti YSL",
            imageRes = "logo_ysl"
        )
    )

    /**
     * Restituisce la lista completa delle offerte disponibili.
     */
    fun getOffers(): List<Offer> = offers
}