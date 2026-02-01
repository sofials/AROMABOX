package com.example.aromabox.data.seed

import com.example.aromabox.data.model.Distributor

object DistributorSeedData {

    fun getDistributors(): List<Distributor> = listOf(
        // Il NOSTRO distributore - attivo e cliccabile
        // Contiene solo 2 profumi come da requisito (scalabile in futuro)
        Distributor(
            id = "dist_001",
            nome = "Politecnico di Torino",
            indirizzo = "Corso Duca degli Abruzzi 24",
            citta = "Torino",
            cap = "10129",
            provincia = "TO",
            latitudine = 45.0628,
            longitudine = 7.6627,
            attivo = true,  // ✅ Solo questo è attivo e cliccabile
            inventario = mapOf(
                // Usa gli ID esatti da PerfumeSeedData
                "perfume_1" to 5,   // Chanel N°5 - 5 disponibili
                "perfume_2" to 3    // Dior Sauvage - 3 disponibili
            )
        ),

        // Distributore 2 - NON attivo (placeholder per il futuro)
        Distributor(
            id = "dist_002",
            nome = "Galleria Aurora",
            indirizzo = "Via delle Marze 23",
            citta = "Borgo Luminato",
            cap = "48012",
            provincia = "RA",
            latitudine = 44.4184,
            longitudine = 12.2035,
            attivo = false,  // Non cliccabile
            inventario = emptyMap()  // Nessun profumo (per ora)
        ),

        // Distributore 3 - NON attivo (placeholder per il futuro)
        Distributor(
            id = "dist_003",
            nome = "Le Vele Center",
            indirizzo = "Corso Azzurro 7",
            citta = "Collevento",
            cap = "00127",
            provincia = "RM",
            latitudine = 41.9028,
            longitudine = 12.4964,
            attivo = false,  // Non cliccabile
            inventario = emptyMap()  // Nessun profumo (per ora)
        )
    )
}