package com.example.aromabox.utils

import com.example.aromabox.R

fun getImageResForNota(nota: String): Int {
    return when (nota.lowercase()) {
        "tuberosa" -> R.drawable.tuberosa
        "gelsomino" -> R.drawable.gelsomino
        "rosa rossa" -> R.drawable.rosa_rossa
        "giglio" -> R.drawable.giglio
        "narciso" -> R.drawable.narciso
        "magnolia" -> R.drawable.magnolia
        "gardenia" -> R.drawable.gardenia
        "fiore d'arancio" -> R.drawable.fiore_arancio
        "peonia" -> R.drawable.peonia
        "viola" -> R.drawable.viola
        "ylang-ylang" -> R.drawable.ylang_ylang
        "rosa bianca" -> R.drawable.rosa_bianca
        "geranio" -> R.drawable.geranio
        "iris" -> R.drawable.iris
        "loto" -> R.drawable.loto
        "osmanto" -> R.drawable.osmanto
        "neroli" -> R.drawable.neroli
        "fresia" -> R.drawable.fresia
        "labdano" -> R.drawable.labdano
        "orchidea" -> R.drawable.orchidea
        "pesca" -> R.drawable.pesca
        "mela verde" -> R.drawable.mela_verde
        "mela rossa" -> R.drawable.mela_rossa
        "albicocca" -> R.drawable.albicocca
        "mora" -> R.drawable.mora
        "ciliegia" -> R.drawable.ciliegia
        "litchi" -> R.drawable.litchi
        "prugna" -> R.drawable.prugna
        "ananas" -> R.drawable.ananas
        "ribes" -> R.drawable.ribes
        "fico" -> R.drawable.fico
        "pera" -> R.drawable.pera
        "fragola" -> R.drawable.fragola
        "lampone" -> R.drawable.lampone
        "melograno" -> R.drawable.melograno
        "mango" -> R.drawable.mango
        "frutto della passione" -> R.drawable.frutto_della_passione
        "pepe rosa" -> R.drawable.pepe_rosa
        "pepe nero" -> R.drawable.pepe_nero
        "cannella" -> R.drawable.cannella
        "cardamomo" -> R.drawable.cardamomo
        "caffè" -> R.drawable.caffe
        "curry" -> R.drawable.curry
        "anice" -> R.drawable.anice
        "zenzero" -> R.drawable.zenzero
        "zafferano" -> R.drawable.zafferano
        "caramello" -> R.drawable.caramello
        "crema" -> R.drawable.crema
        "miele" -> R.drawable.miele
        "biscotto" -> R.drawable.biscotto
        "cocco" -> R.drawable.cocco
        "pralina" -> R.drawable.pralina
        "cioccolato" -> R.drawable.cioccolato
        "mandorla" -> R.drawable.mandorla
        "pistacchio" -> R.drawable.pistacchio
        "nocciola" -> R.drawable.nocciola
        "castagna" -> R.drawable.castagna
        "cacao" -> R.drawable.cacao
        "marshmallow" -> R.drawable.marshmallow
        "latte" -> R.drawable.latte
        "gelato" -> R.drawable.gelato
        "zucchero di canna" -> R.drawable.zucchero_canna
        "rum" -> R.drawable.rum
        "cognac" -> R.drawable.cognac
        "oud" -> R.drawable.oud
        "cedro" -> R.drawable.cedro
        "sandalo" -> R.drawable.sandalo
        "patchouli" -> R.drawable.patchouli
        "vetiver" -> R.drawable.vetiver
        "muschio" -> R.drawable.muschio
        "pelle" -> R.drawable.pelle
        "guaiaco" -> R.drawable.guaiaco
        else -> R.drawable.logo  // Fallback
    }
}