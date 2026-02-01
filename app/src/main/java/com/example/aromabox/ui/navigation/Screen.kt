package com.example.aromabox.ui.navigation

sealed class Screen(val route: String) {
    object Loading : Screen("loading")
    object Login : Screen("login")
    object Register : Screen("register")
    object CompleteProfile : Screen("complete_profile")
    object Quiz : Screen("quiz")
    object NotePreferite : Screen("note_preferite")
    object Home : Screen("home")
    object Catalog : Screen("catalog")
    object Recommended : Screen("recommended")
    object Recharge : Screen("recharge")  // ✅ NUOVA

    // Filtri
    object Filters : Screen("filters")
    object FilterSort : Screen("filter_sort")
    object FilterPrice : Screen("filter_price")
    object FilterBrand : Screen("filter_brand")
    object FilterGender : Screen("filter_gender")
    object FilterFamily : Screen("filter_family")
    object FilterNotes : Screen("filter_notes")

    object Distributori : Screen("distributori")
    object Storico : Screen("storico")
    object Profile : Screen("profile")

    object PerfumeDetail : Screen("perfume_detail/{perfumeId}") {
        fun createRoute(perfumeId: String) = "perfume_detail/$perfumeId"
    }
}