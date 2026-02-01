package com.example.aromabox.data.model

import androidx.annotation.DrawableRes
import com.example.aromabox.R

data class Perfume(
    val id: String = "",
    val nome: String = "",
    val marca: String = "",
    val prezzo: Double = 0.0,
    val categoria: String = "",
    val genere: String = "Unisex",  // ✅ NUOVO CAMPO: "Uomo", "Donna", "Unisex"
    val disponibile: Boolean = true,
    val slot: Int = 0,
    val noteOlfattive: NoteOlfattive = NoteOlfattive(),
    val imageUrl: String = ""
) {
    @DrawableRes
    fun getImageResource(): Int {
        return when (imageUrl.lowercase().trim()) {
            "perfume_chanel_no5" -> R.drawable.perfume_chanel_no5
            "perfume_dior_sauvage" -> R.drawable.perfume_dior_sauvage
            "perfume_armani_si" -> R.drawable.perfume_armani_si
            else -> R.drawable.logo
        }
    }
}

data class NoteOlfattive(
    val noteDiTesta: List<String> = emptyList(),
    val noteDiCuore: List<String> = emptyList(),
    val noteDiFondo: List<String> = emptyList()
)