package com.example.aromabox.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object CompleteProfile : Screen("complete_profile")
    object Home : Screen("home")
    object Distributori : Screen("distributori")
    object Storico : Screen("storico")
    object Catalog : Screen("catalog")
    object Profile : Screen("profile")
    object Quiz : Screen("quiz")
    object NotePreferite : Screen("note_preferite")
    object ProductDetail : Screen("product/{productId}") {
        fun createRoute(productId: String) = "product/$productId"
    }
}