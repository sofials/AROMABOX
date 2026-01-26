package com.example.aromabox.data.model

import androidx.annotation.DrawableRes
import com.example.aromabox.R

data class Perfume(
    val id: String = "",
    val nome: String = "",
    val marca: String = "",
    val prezzo: Double = 0.0,
    val categoria: String = "",
    val disponibile: Boolean = true,
    val slot: Int = 0,  // Slot nel distributore fisico
    val noteOlfattive: NoteOlfattive = NoteOlfattive(),
    val imageUrl: String = ""  // Nome file in drawable (es: "perfume_chanel_no5")
) {
    // ✅ Helper per ottenere la risorsa drawable
    // Aggiungi qui tutte le immagini che hai in res/drawable
    @DrawableRes
    fun getImageResource(): Int {
        return when (imageUrl.lowercase().trim()) {
            // I tuoi 3 profumi configurati
            "perfume_chanel_no5" -> R.drawable.perfume_chanel_no5
            "perfume_dior_sauvage" -> R.drawable.perfume_dior_sauvage
            "perfume_armani_si" -> R.drawable.perfume_armani_si

            // Aggiungi altri profumi qui man mano che aggiungi le immagini
            // "perfume_miss_dior" -> R.drawable.perfume_miss_dior
            // "perfume_libre" -> R.drawable.perfume_libre
            // "perfume_good_girl" -> R.drawable.perfume_good_girl
            // "perfume_black_opium" -> R.drawable.perfume_black_opium
            // "perfume_bianco_latte" -> R.drawable.perfume_bianco_latte
            // "perfume_paradoxe" -> R.drawable.perfume_paradoxe
            // "perfume_born_in_roma" -> R.drawable.perfume_born_in_roma
            // "perfume_chloe" -> R.drawable.perfume_chloe
            // "perfume_lime_basil" -> R.drawable.perfume_lime_basil
            // "perfume_bleu_chanel" -> R.drawable.perfume_bleu_chanel
            // "perfume_ombre_leather" -> R.drawable.perfume_ombre_leather

            // Fallback: usa il logo come placeholder
            else -> R.drawable.logo
        }
    }
}

data class NoteOlfattive(
    val noteDiTesta: List<String> = emptyList(),
    val noteDiCuore: List<String> = emptyList(),
    val noteDiFondo: List<String> = emptyList()
)