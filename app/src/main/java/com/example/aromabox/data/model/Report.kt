package com.example.aromabox.data.model

/**
 * Data class che rappresenta una segnalazione nel sistema AromaBox.
 * Rappresentazione neutrale guidata dai requisiti del dominio,
 * indipendente dalle tecnologie di storage (linee guida Prof. Malnati).
 */
data class Report(
    val id: String = "",
    val uid: String = "",
    val type: String = "bug",          // "bug" | "suggestion" | "other"
    val message: String = "",
    val appVersion: String = "",
    val device: String = "",
    val createdAt: Long = 0L,          // Timestamp
    val status: String = "open"        // "open" | "closed"
)