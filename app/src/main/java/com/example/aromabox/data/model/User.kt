package com.example.aromabox.data.model

/**
 * Modello utente con supporto per badge e statistiche
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val nome: String = "",
    val cognome: String = "",
    val photoUrl: String? = null,
    val wallet: Double = 0.0,
    val preferiti: List<String> = emptyList(),
    val profiloOlfattivo: ProfiloOlfattivo? = null,
    val isConnected: Boolean = false,

    // Badge e statistiche
    val badges: List<Badge> = emptyList(),
    val totaleAcquisti: Int = 0,
    val totaleRecensioni: Int = 0,
    val totaleErogazioni: Int = 0
) {
    // Costruttore vuoto per Firebase
    constructor() : this(
        uid = "",
        email = "",
        nome = "",
        cognome = "",
        photoUrl = null,
        wallet = 0.0,
        preferiti = emptyList(),
        profiloOlfattivo = null,
        isConnected = false,
        badges = emptyList(),
        totaleAcquisti = 0,
        totaleRecensioni = 0,
        totaleErogazioni = 0
    )

    /**
     * Restituisce il nome completo dell'utente
     */
    fun getFullName(): String {
        return when {
            nome.isNotBlank() && cognome.isNotBlank() -> "$nome $cognome"
            nome.isNotBlank() -> nome
            cognome.isNotBlank() -> cognome
            email.isNotBlank() -> email.substringBefore("@")
            else -> "Utente"
        }
    }

    /**
     * Restituisce l'iniziale per l'avatar
     */
    fun getInitial(): String {
        return when {
            nome.isNotBlank() -> nome.first().uppercase()
            email.isNotBlank() -> email.first().uppercase()
            else -> "?"
        }
    }

    /**
     * Verifica se l'utente ha completato il quiz olfattivo
     */
    fun hasCompletedQuiz(): Boolean {
        return profiloOlfattivo != null && profiloOlfattivo.getTutteLeNote().isNotEmpty()
    }

    /**
     * Conta i badge sbloccati
     */
    fun getUnlockedBadgeCount(): Int {
        return badges.count { it.isUnlocked }
    }
}