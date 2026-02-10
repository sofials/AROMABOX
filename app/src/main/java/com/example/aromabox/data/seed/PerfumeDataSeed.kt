package com.example.aromabox.data.seed

import com.example.aromabox.data.model.NoteOlfattive
import com.example.aromabox.data.model.Perfume

object PerfumeSeedData {

    fun getSeedPerfumes(): List<Perfume> = listOf(
        Perfume(
            id = "perfume_1",
            nome = "N°5",
            marca = "Chanel",
            prezzo = 2.50,
            categoria = "floreale",
            genere = "Donna",  // ✅
            disponibile = true,
            slot = 1,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("aldeidi", "neroli", "limone"),
                noteDiCuore = listOf("rosa", "gelsomino", "iris"),
                noteDiFondo = listOf("vaniglia", "sandalo", "muschio")
            ),
            imageUrl = "perfume_chanel_no5"
        ),
        Perfume(
            id = "perfume_2",
            nome = "Sauvage",
            marca = "Dior",
            prezzo = 2.50,
            categoria = "legnoso",
            genere = "Uomo",  // ✅
            disponibile = true,
            slot = 2,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("bergamotto", "pepe"),
                noteDiCuore = listOf("lavanda", "geranio"),
                noteDiFondo = listOf("cedro", "vetiver", "patchouli")
            ),
            imageUrl = "perfume_dior_sauvage"
        ),
        Perfume(
            id = "perfume_3",
            nome = "Sì",
            marca = "Giorgio Armani",
            prezzo = 2.00,
            categoria = "fruttato",
            genere = "Donna",  // ✅
            disponibile = true,
            slot = 3,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("ribes nero", "mandarino"),
                noteDiCuore = listOf("rosa", "freesia"),
                noteDiFondo = listOf("vaniglia", "patchouli", "muschio")
            ),
            imageUrl = "perfume_armani_si"
        ),
        Perfume(
            id = "perfume_4",
            nome = "Miss Dior",
            marca = "Dior",
            prezzo = 2.50,
            categoria = "floreale",
            genere = "Donna",  // ✅
            disponibile = true,
            slot = 4,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("mandarino", "rosa"),
                noteDiCuore = listOf("peonia", "iris"),
                noteDiFondo = listOf("muschio", "patchouli")
            ),
            imageUrl = "perfume_miss_dior"
        ),
        Perfume(
            id = "perfume_5",
            nome = "Libre",
            marca = "Yves Saint Laurent",
            prezzo = 2.50,
            categoria = "floreale",
            genere = "Donna",  // ✅
            disponibile = false,
            slot = 5,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("lavanda", "mandarino"),
                noteDiCuore = listOf("gelsomino", "fiore d'arancio"),
                noteDiFondo = listOf("vaniglia", "cedro", "muschio")
            ),
            imageUrl = "perfume_libre"
        ),
        Perfume(
            id = "perfume_6",
            nome = "Good Girl",
            marca = "Carolina Herrera",
            prezzo = 2.00,
            categoria = "speziato",
            genere = "Donna",  // ✅
            disponibile = true,
            slot = 6,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("mandorla", "caffè"),
                noteDiCuore = listOf("tuberosa", "gelsomino"),
                noteDiFondo = listOf("tonka", "cacao", "sandalo")
            ),
            imageUrl = "perfume_good_girl"
        ),
        Perfume(
            id = "perfume_7",
            nome = "Bianco Latte",
            marca = "Giardini di Toscana",
            prezzo = 3.50,
            categoria = "gourmand",
            genere = "Unisex",  // ✅
            disponibile = true,
            slot = 7,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("latte", "mandorla"),
                noteDiCuore = listOf("vaniglia", "cocco"),
                noteDiFondo = listOf("muschio bianco", "caramello")
            ),
            imageUrl = "perfume_bianco_latte"
        ),
        Perfume(
            id = "perfume_8",
            nome = "Black Opium",
            marca = "Yves Saint Laurent",
            prezzo = 2.50,
            categoria = "speziato",
            genere = "Donna",  // ✅
            disponibile = true,
            slot = 8,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("caffè", "mandarino"),
                noteDiCuore = listOf("gelsomino", "arancia"),
                noteDiFondo = listOf("vaniglia", "patchouli", "cedro")
            ),
            imageUrl = "perfume_black_opium"
        ),
        Perfume(
            id = "perfume_9",
            nome = "Paradoxe",
            marca = "Prada",
            prezzo = 3.00,
            categoria = "floreale",
            genere = "Donna",  // ✅
            disponibile = false,
            slot = 9,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("pera", "bergamotto"),
                noteDiCuore = listOf("gelsomino", "neroli"),
                noteDiFondo = listOf("ambra", "muschio")
            ),
            imageUrl = "perfume_paradoxe"
        ),
        Perfume(
            id = "perfume_10",
            nome = "Born in Roma Uomo",
            marca = "Valentino",
            prezzo = 2.50,
            categoria = "legnoso",
            genere = "Uomo",  // ✅
            disponibile = true,
            slot = 10,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("zenzero", "salvia"),
                noteDiCuore = listOf("vetiver", "violetta"),
                noteDiFondo = listOf("cedro", "ambra", "tonka")
            ),
            imageUrl = "perfume_born_roma"
        ),
        Perfume(
            id = "perfume_11",
            nome = "Chloé",
            marca = "Chloé",
            prezzo = 2.00,
            categoria = "floreale",
            genere = "Donna",  // ✅
            disponibile = true,
            slot = 11,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("peonia", "freesia"),
                noteDiCuore = listOf("rosa", "magnolia"),
                noteDiFondo = listOf("cedro", "ambra")
            ),
            imageUrl = "perfume_chloe"
        ),
        Perfume(
            id = "perfume_12",
            nome = "Lime Basil & Mandarin",
            marca = "Jo Malone London",
            prezzo = 2.00,
            categoria = "fruttato",
            genere = "Unisex",  // ✅
            disponibile = true,
            slot = 12,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("lime", "mandarino"),
                noteDiCuore = listOf("basilico"),
                noteDiFondo = listOf("ambra", "vetiver")
            ),
            imageUrl = "perfume_lime_basil"
        ),
        Perfume(
            id = "perfume_13",
            nome = "Bleu de Chanel",
            marca = "Chanel",
            prezzo = 2.50,
            categoria = "legnoso",
            genere = "Uomo",  // ✅
            disponibile = true,
            slot = 13,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("menta", "pompelmo", "limone"),
                noteDiCuore = listOf("gelsomino", "zenzero"),
                noteDiFondo = listOf("cedro", "sandalo", "incenso")
            ),
            imageUrl = "perfume_bleu_chanel"
        ),
        Perfume(
            id = "perfume_14",
            nome = "Ombré Leather",
            marca = "Tom Ford",
            prezzo = 3.50,
            categoria = "legnoso",
            genere = "Unisex",  // ✅
            disponibile = true,
            slot = 14,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("cardamomo"),
                noteDiCuore = listOf("gelsomino", "pelle"),
                noteDiFondo = listOf("patchouli", "vetiver", "muschio")
            ),
            imageUrl = "perfume_ombre_leather"
        )
    )
}