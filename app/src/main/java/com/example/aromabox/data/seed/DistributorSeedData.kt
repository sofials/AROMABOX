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
                "perfume_1" to 10,   // Chanel N°5 - 4 disponibili
                "perfume_2" to 20    // Dior Sauvage - 4 disponibili
            )
        ),

        // Distributore 2 - NON attivo (placeholder per il futuro)
        Distributor(
            id = "dist_002",
            nome = "Stazione Porta Nuova",
            indirizzo = "Corso Vittorio Emanuele II, 58",
            citta = "Torino",
            cap = "10128",
            provincia = "TO",
            latitudine = 45.0612,
            longitudine = 7.6780,
            attivo = false,  // Non cliccabile
            inventario = emptyMap()  // Nessun profumo (per ora)
        ),
// Distributore 3 - NON attivo (placeholder per il futuro)
        Distributor(
            id = "dist_003",
            nome = "Pinalli Torino Area12",
            indirizzo = "Strada Altessano, 141",
            citta = "Torino",
            cap = "10151",
            provincia = "TO",
            latitudine = 45.1096,
            longitudine = 7.6414,
            attivo = false,  // Non cliccabile
            inventario = emptyMap()  // Nessun profumo (per ora)
        )
    )
}