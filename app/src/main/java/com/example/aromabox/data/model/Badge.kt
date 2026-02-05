package com.example.aromabox.data.model

/**
 * Categorie di badge disponibili nell'app
 */
enum class BadgeCategory {
    APPRENDISTA,
    TESTER,
    ESPLORATORE
}

/**
 * Tipo di requisito per sbloccare un badge
 */
enum class BadgeRequirementType {
    ACQUISTI,      // Numero di acquisti presso distributori
    EROGAZIONI,    // Numero di erogazioni acquistate
    PREFERITI      // Numero di profumi nei preferiti
}

/**
 * Definizione statica di un badge (template)
 */
data class BadgeDefinition(
    val id: String,
    val nome: String,
    val descrizione: String,
    val category: BadgeCategory,
    val livello: Int,                    // 1, 2, 3, 4 per badge con più livelli
    val requirementType: BadgeRequirementType,
    val requirementValue: Int,           // Valore richiesto per sbloccare
    val iconResName: String = "beautyicon"  // Nome risorsa drawable
)

/**
 * Badge dell'utente con stato di completamento
 */
data class Badge(
    val id: String = "",
    val nome: String = "",
    val descrizione: String = "",
    val category: String = "",           // Stringa per Firebase
    val livello: Int = 1,
    val iconResName: String = "beautyicon",
    val dataOttenimento: Long = 0L,
    val isUnlocked: Boolean = false
) {
    // Costruttore vuoto per Firebase
    constructor() : this("", "", "", "", 1, "beautyicon", 0L, false)

    companion object {
        /**
         * Crea un Badge da una BadgeDefinition
         */
        fun fromDefinition(definition: BadgeDefinition, isUnlocked: Boolean, dataOttenimento: Long = 0L): Badge {
            return Badge(
                id = definition.id,
                nome = definition.nome,
                descrizione = definition.descrizione,
                category = definition.category.name,
                livello = definition.livello,
                iconResName = definition.iconResName,
                dataOttenimento = dataOttenimento,
                isUnlocked = isUnlocked
            )
        }
    }
}

/**
 * Repository statico con tutti i badge disponibili nell'app
 */
object BadgeDefinitions {

    val allBadges: List<BadgeDefinition> = listOf(
        // APPRENDISTA - 1 solo livello
        BadgeDefinition(
            id = "apprendista_1",
            nome = "Apprendista",
            descrizione = "Effettua un acquisto presso un distributore AromaBox",
            category = BadgeCategory.APPRENDISTA,
            livello = 1,
            requirementType = BadgeRequirementType.ACQUISTI,
            requirementValue = 1,
            iconResName = "beautyicon"
        ),

        // TESTER - 4 livelli (erogazioni)
        BadgeDefinition(
            id = "tester_1",
            nome = "Tester",
            descrizione = "Acquista un'erogazione di profumo",
            category = BadgeCategory.TESTER,
            livello = 1,
            requirementType = BadgeRequirementType.EROGAZIONI,
            requirementValue = 1,
            iconResName = "beautyicon"
        ),
        BadgeDefinition(
            id = "tester_2",
            nome = "Tester",
            descrizione = "Acquista 10 erogazioni di profumo",
            category = BadgeCategory.TESTER,
            livello = 2,
            requirementType = BadgeRequirementType.EROGAZIONI,
            requirementValue = 10,
            iconResName = "beautyicon"
        ),
        BadgeDefinition(
            id = "tester_3",
            nome = "Tester",
            descrizione = "Acquista 50 erogazioni di profumo",
            category = BadgeCategory.TESTER,
            livello = 3,
            requirementType = BadgeRequirementType.EROGAZIONI,
            requirementValue = 50,
            iconResName = "beautyicon"
        ),
        BadgeDefinition(
            id = "tester_4",
            nome = "Tester",
            descrizione = "Acquista 100 erogazioni di profumo",
            category = BadgeCategory.TESTER,
            livello = 4,
            requirementType = BadgeRequirementType.EROGAZIONI,
            requirementValue = 100,
            iconResName = "beautyicon"
        ),

        // ESPLORATORE - 2 livelli (preferiti)
        BadgeDefinition(
            id = "esploratore_1",
            nome = "Esploratore",
            descrizione = "Aggiungi 5 profumi ai tuoi preferiti",
            category = BadgeCategory.ESPLORATORE,
            livello = 1,
            requirementType = BadgeRequirementType.PREFERITI,
            requirementValue = 5,
            iconResName = "beautyicon"
        ),
        BadgeDefinition(
            id = "esploratore_2",
            nome = "Esploratore",
            descrizione = "Aggiungi 10 profumi ai tuoi preferiti",
            category = BadgeCategory.ESPLORATORE,
            livello = 2,
            requirementType = BadgeRequirementType.PREFERITI,
            requirementValue = 10,
            iconResName = "beautyicon"
        )
    )

    /**
     * Raggruppa i badge per categoria
     */
    val badgesByCategory: Map<BadgeCategory, List<BadgeDefinition>> =
        allBadges.groupBy { it.category }

    /**
     * Trova un badge per ID
     */
    fun findById(id: String): BadgeDefinition? =
        allBadges.find { it.id == id }

    /**
     * Trova tutti i badge di una categoria
     */
    fun findByCategory(category: BadgeCategory): List<BadgeDefinition> =
        allBadges.filter { it.category == category }

    /**
     * Trova badge per tipo di requisito
     */
    fun findByRequirementType(type: BadgeRequirementType): List<BadgeDefinition> =
        allBadges.filter { it.requirementType == type }
}