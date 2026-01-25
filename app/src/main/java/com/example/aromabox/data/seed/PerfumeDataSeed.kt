package com.example.aromabox.data.seed

import com.example.aromabox.data.model.NoteOlfattive
import com.example.aromabox.data.model.Perfume

object PerfumeSeedData {

    fun getSeedPerfumes(): List<Perfume> = listOf(
        Perfume(
            id = "perfume_1",
            nome = "N°5",
            marca = "Chanel",
            prezzo = 120.00,
            categoria = "floreale",
            disponibile = true,
            slot = 1,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("aldeidi", "neroli", "limone"),
                noteDiCuore = listOf("rosa", "gelsomino", "iris"),
                noteDiFondo = listOf("vaniglia", "sandalo", "muschio")
            ),
            imageUrl = "logo"  // ✅ Usa logo come placeholder finché non hai le immagini
        ),
        Perfume(
            id = "perfume_2",
            nome = "Sauvage",
            marca = "Dior",
            prezzo = 95.00,
            categoria = "legnoso",
            disponibile = true,
            slot = 2,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("bergamotto", "pepe"),
                noteDiCuore = listOf("lavanda", "geranio"),
                noteDiFondo = listOf("cedro", "vetiver", "patchouli")
            ),
            imageUrl = "logo"  // ✅ Placeholder
        ),
        Perfume(
            id = "perfume_3",
            nome = "Sì",
            marca = "Giorgio Armani",
            prezzo = 110.00,
            categoria = "fruttato",
            disponibile = true,
            slot = 3,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("ribes nero", "mandarino"),
                noteDiCuore = listOf("rosa", "freesia"),
                noteDiFondo = listOf("vaniglia", "patchouli", "muschio")
            ),
            imageUrl = "logo"  // ✅ Placeholder
        ),
        Perfume(
            id = "perfume_4",
            nome = "La Vie Est Belle",
            marca = "Lancôme",
            prezzo = 105.00,
            categoria = "gourmand",
            disponibile = true,
            slot = 4,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("ribes nero", "pera"),
                noteDiCuore = listOf("iris", "gelsomino", "arancia"),
                noteDiFondo = listOf("pralinato", "vaniglia", "patchouli", "tonka")
            ),
            imageUrl = "logo"  // ✅ Placeholder
        ),
        Perfume(
            id = "perfume_5",
            nome = "Black Opium",
            marca = "Yves Saint Laurent",
            prezzo = 98.00,
            categoria = "speziato",
            disponibile = true,
            slot = 5,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("caffè", "mandarino"),
                noteDiCuore = listOf("gelsomino", "arancia", "liquirizia"),
                noteDiFondo = listOf("vaniglia", "patchouli", "cedro")
            ),
            imageUrl = "logo"  // ✅ Placeholder
        ),
        Perfume(
            id = "perfume_6",
            nome = "Good Girl",
            marca = "Carolina Herrera",
            prezzo = 115.00,
            categoria = "floreale",
            disponibile = false,
            slot = 6,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("mandorla", "caffè"),
                noteDiCuore = listOf("tuberosa", "gelsomino"),
                noteDiFondo = listOf("tonka", "cacao", "sandalo")
            ),
            imageUrl = "logo"  // ✅ Placeholder
        )
    )
}