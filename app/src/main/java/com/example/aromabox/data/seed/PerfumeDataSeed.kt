package com.example.aromabox.data.seed

import com.example.aromabox.data.model.NoteOlfattive
import com.example.aromabox.data.model.Perfume

/**
 * Dati di seed per popolare Firebase al primo avvio.
 *
 * IMPORTANTE: imageUrl deve corrispondere a un nome in Perfume.getImageResource()
 * Immagini disponibili:
 * - perfume_chanel_no5
 * - perfume_dior_sauvage
 * - perfume_armani_si
 *
 * Per aggiungere nuove immagini:
 * 1. Aggiungi il file in res/drawable/ (es: perfume_miss_dior.png)
 * 2. Aggiungi il mapping in Perfume.getImageResource()
 * 3. Usa il nome nel seed (es: imageUrl = "perfume_miss_dior")
 */
object PerfumeSeedData {

    fun getSeedPerfumes(): List<Perfume> = listOf(
        // ✅ Profumi con immagini disponibili
        Perfume(
            id = "perfume_1",
            nome = "N°5",
            marca = "Chanel",
            prezzo = 2.50,
            categoria = "floreale",
            disponibile = true,
            slot = 1,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("aldeidi", "neroli", "limone"),
                noteDiCuore = listOf("rosa", "gelsomino", "iris"),
                noteDiFondo = listOf("vaniglia", "sandalo", "muschio")
            ),
            imageUrl = "perfume_chanel_no5"  // ✅ Immagine disponibile
        ),
        Perfume(
            id = "perfume_2",
            nome = "Sauvage",
            marca = "Dior",
            prezzo = 2.50,
            categoria = "legnoso",
            disponibile = true,
            slot = 2,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("bergamotto", "pepe"),
                noteDiCuore = listOf("lavanda", "geranio"),
                noteDiFondo = listOf("cedro", "vetiver", "patchouli")
            ),
            imageUrl = "perfume_dior_sauvage"  // ✅ Immagine disponibile
        ),
        Perfume(
            id = "perfume_3",
            nome = "Sì",
            marca = "Giorgio Armani",
            prezzo = 2.00,
            categoria = "fruttato",
            disponibile = true,
            slot = 3,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("ribes nero", "mandarino"),
                noteDiCuore = listOf("rosa", "freesia"),
                noteDiFondo = listOf("vaniglia", "patchouli", "muschio")
            ),
            imageUrl = "perfume_armani_si"  // ✅ Immagine disponibile
        ),

        // ⚠️ Profumi senza immagine dedicata (useranno logo come fallback)
        // Quando aggiungi le immagini, aggiorna imageUrl
        Perfume(
            id = "perfume_4",
            nome = "Miss Dior",
            marca = "Dior",
            prezzo = 1.50,
            categoria = "floreale",
            disponibile = true,
            slot = 4,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("mandarino", "rosa"),
                noteDiCuore = listOf("peonia", "iris"),
                noteDiFondo = listOf("muschio", "patchouli")
            ),
            imageUrl = "perfume_miss_dior"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_5",
            nome = "Libre",
            marca = "Yves Saint Laurent",
            prezzo = 2.50,
            categoria = "floreale",
            disponibile = false,  // Non disponibile - "SOLO IN NEGOZIO"
            slot = 5,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("lavanda", "mandarino"),
                noteDiCuore = listOf("gelsomino", "fiore d'arancio"),
                noteDiFondo = listOf("vaniglia", "cedro", "muschio")
            ),
            imageUrl = "perfume_libre"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_6",
            nome = "Good Girl",
            marca = "Carolina Herrera",
            prezzo = 2.00,
            categoria = "speziato",
            disponibile = true,
            slot = 6,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("mandorla", "caffè"),
                noteDiCuore = listOf("tuberosa", "gelsomino"),
                noteDiFondo = listOf("tonka", "cacao", "sandalo")
            ),
            imageUrl = "perfume_good_girl"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_7",
            nome = "Bianco Latte",
            marca = "Giardini di Toscana",
            prezzo = 3.50,
            categoria = "gourmand",
            disponibile = true,
            slot = 7,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("latte", "mandorla"),
                noteDiCuore = listOf("vaniglia", "cocco"),
                noteDiFondo = listOf("muschio bianco", "caramello")
            ),
            imageUrl = "perfume_bianco_latte"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_8",
            nome = "Black Opium",
            marca = "Yves Saint Laurent",
            prezzo = 2.50,
            categoria = "speziato",
            disponibile = true,
            slot = 8,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("caffè", "mandarino"),
                noteDiCuore = listOf("gelsomino", "arancia"),
                noteDiFondo = listOf("vaniglia", "patchouli", "cedro")
            ),
            imageUrl = "perfume_black_opium"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_9",
            nome = "Paradoxe",
            marca = "Prada",
            prezzo = 3.00,
            categoria = "floreale",
            disponibile = false,  // Non disponibile - "SOLO IN NEGOZIO"
            slot = 9,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("pera", "bergamotto"),
                noteDiCuore = listOf("gelsomino", "neroli"),
                noteDiFondo = listOf("ambra", "muschio")
            ),
            imageUrl = "perfume_paradoxe"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_10",
            nome = "Born in Roma Uomo",
            marca = "Valentino",
            prezzo = 2.50,
            categoria = "legnoso",
            disponibile = true,
            slot = 10,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("zenzero", "salvia"),
                noteDiCuore = listOf("vetiver", "violetta"),
                noteDiFondo = listOf("cedro", "ambra", "tonka")
            ),
            imageUrl = "perfume_born_roma"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_11",
            nome = "Chloé",
            marca = "Chloé",
            prezzo = 1.50,
            categoria = "floreale",
            disponibile = true,
            slot = 11,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("peonia", "freesia"),
                noteDiCuore = listOf("rosa", "magnolia"),
                noteDiFondo = listOf("cedro", "ambra")
            ),
            imageUrl = "perfume_chloe"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_12",
            nome = "Lime Basil & Mandarin",
            marca = "Jo Malone London",
            prezzo = 3.50,
            categoria = "fruttato",
            disponibile = true,
            slot = 12,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("lime", "mandarino"),
                noteDiCuore = listOf("basilico"),
                noteDiFondo = listOf("ambra", "vetiver")
            ),
            imageUrl = "perfume_lime_basil"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_13",
            nome = "Bleu de Chanel",
            marca = "Chanel",
            prezzo = 2.00,
            categoria = "legnoso",
            disponibile = true,
            slot = 13,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("menta", "pompelmo", "limone"),
                noteDiCuore = listOf("gelsomino", "zenzero"),
                noteDiFondo = listOf("cedro", "sandalo", "incenso")
            ),
            imageUrl = "perfume_bleu_chanel"  // TODO: Aggiungi immagine
        ),
        Perfume(
            id = "perfume_14",
            nome = "Ombré Leather",
            marca = "Tom Ford",
            prezzo = 3.50,
            categoria = "legnoso",
            disponibile = true,
            slot = 14,
            noteOlfattive = NoteOlfattive(
                noteDiTesta = listOf("cardamomo"),
                noteDiCuore = listOf("gelsomino", "pelle"),
                noteDiFondo = listOf("patchouli", "vetiver", "muschio")
            ),
            imageUrl = "perfume_ombre_leather"  // TODO: Aggiungi immagine
        )
    )
}