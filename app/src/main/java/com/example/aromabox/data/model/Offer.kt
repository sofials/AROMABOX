package com.example.aromabox.data.model

/**
 * Rappresentazione neutrale di un'offerta/sconto.
 * Guidata solo dai requisiti del dominio, non dalla tecnologia di storage.
 */
data class Offer(
    val id: String,
    val brand: String,
    val discountPercent: Int,
    val description: String,
    val imageRes: String   // nome drawable del logo brand
)