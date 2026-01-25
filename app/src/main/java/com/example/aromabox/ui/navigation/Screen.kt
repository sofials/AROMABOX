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
    object Distributori : Screen("distributori")  // ✅ Italiano
    object Storico : Screen("storico")           // ✅ Italiano
    object Profile : Screen("profile")

    object PerfumeDetail : Screen("perfume_detail/{perfumeId}") {
        fun createRoute(perfumeId: String) = "perfume_detail/$perfumeId"
    }
}