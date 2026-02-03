package com.example.aromabox.data.model

import com.google.firebase.database.Exclude

data class ProfiloOlfattivo(
    val noteFloreali: List<String> = emptyList(),
    val noteFruttate: List<String> = emptyList(),
    val noteSpeziate: List<String> = emptyList(),
    val noteGourmand: List<String> = emptyList(),
    val noteLegnose: List<String> = emptyList()
) {
    // Costruttore vuoto richiesto da Firebase
    constructor() : this(emptyList(), emptyList(), emptyList(), emptyList(), emptyList())

    @Exclude
    fun isCompleto(): Boolean {
        return noteFloreali.isNotEmpty() &&
                noteFruttate.isNotEmpty() &&
                noteSpeziate.isNotEmpty() &&
                noteGourmand.isNotEmpty() &&
                noteLegnose.isNotEmpty()
    }

    @Exclude
    fun getTotaleNote(): Int {
        return noteFloreali.size + noteFruttate.size + noteSpeziate.size +
                noteGourmand.size + noteLegnose.size
    }

    @Exclude
    fun getPercentualeFloreale(): Float {
        val totale = getTotaleNote()
        return if (totale > 0) (noteFloreali.size.toFloat() / totale) * 100 else 0f
    }

    @Exclude
    fun getPercentualeFruttata(): Float {
        val totale = getTotaleNote()
        return if (totale > 0) (noteFruttate.size.toFloat() / totale) * 100 else 0f
    }

    @Exclude
    fun getPercentualeSpeziata(): Float {
        val totale = getTotaleNote()
        return if (totale > 0) (noteSpeziate.size.toFloat() / totale) * 100 else 0f
    }

    @Exclude
    fun getPercentualeGourmand(): Float {
        val totale = getTotaleNote()
        return if (totale > 0) (noteGourmand.size.toFloat() / totale) * 100 else 0f
    }

    @Exclude
    fun getPercentualeLegnosa(): Float {
        val totale = getTotaleNote()
        return if (totale > 0) (noteLegnose.size.toFloat() / totale) * 100 else 0f
    }

    @Exclude
    fun getTutteLeNote(): List<String> {
        return noteFloreali + noteFruttate + noteSpeziate + noteGourmand + noteLegnose
    }
}