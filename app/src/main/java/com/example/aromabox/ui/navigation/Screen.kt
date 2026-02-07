package com.example.aromabox.ui.navigation

/**
 * Definizione delle route di navigazione dell'app
 */
sealed class Screen(val route: String) {
    // Loading
    object Loading : Screen("loading")

    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    object CompleteProfile : Screen("complete_profile")

    // Main screens (Bottom Navigation)
    object Home : Screen("home")
    object Distributori : Screen("distributori")
    object Storico : Screen("storico")
    object Catalog : Screen("catalog")
    object Profile : Screen("profile")

    // Quiz
    object Quiz : Screen("quiz")
    object NotePreferite : Screen("note_preferite")

    // Secondary screens
    object Recharge : Screen("recharge")
    object Recommended : Screen("recommended")

    // Drawer screens
    object Info : Screen("info")
    object Contatti : Screen("contatti")

    // About (click su logo)
    object About : Screen("about")

    // Filters
    object Filters : Screen("filters")
    object FilterSort : Screen("filter_sort")
    object FilterPrice : Screen("filter_price")
    object FilterBrand : Screen("filter_brand")
    object FilterGender : Screen("filter_gender")
    object FilterFamily : Screen("filter_family")
    object FilterNotes : Screen("filter_notes")
    object FilterDistributor : Screen("filter_distributor")
    // In Screen.kt
    object FilterQuiz : Screen("filter_quiz")

    // Detail screens
    object PerfumeDetail : Screen("perfume/{perfumeId}") {
        fun createRoute(perfumeId: String) = "perfume/$perfumeId"
    }
}