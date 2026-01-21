package com.example.aromabox.data.model
import com.example.aromabox.data.model.Order
import com.example.aromabox.data.model.Badge

data class User(
    val userId: String = "",
    val email: String = "",
    val nome: String = "",
    val cognome: String = "",
    val nickname: String = "",
    val photoUrl: String = "",
    val connesso: Boolean = false,
    val macchinettaConnessaId: String = "",
    val borsellino: Double = 0.0,
    val profiloOlfattivo: ProfiloOlfattivo? = null,
    val preferitiIds: List<String> = emptyList(),
    val storico: List<Order> = emptyList(), // ← MODIFICATO QUI
    val badge: List<Badge> = emptyList(),
    val dataRegistrazione: Long = 0L
) {
    constructor() : this("", "", "", "", "", "", false, "", 0.0, null, emptyList(), emptyList(), emptyList(), 0L)

    fun nomeCompleto(): String = "$nome $cognome"

    fun haProfiloCompleto(): Boolean = nome.isNotEmpty() && cognome.isNotEmpty() && nickname.isNotEmpty()

    fun isConnessoAMacchinetta(): Boolean = connesso && macchinettaConnessaId.isNotEmpty()

    fun haCompletatoQuiz(): Boolean = profiloOlfattivo?.isCompleto() == true

    fun numeroProfumiComprati(): Int = storico.size // ← MODIFICATO QUI

    fun puoComprare(prezzo: Double): Boolean = borsellino >= prezzo
}