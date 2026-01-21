package com.example.aromabox.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.aromabox.ui.screens.*
import com.example.aromabox.viewmodel.AuthViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    val authViewModel: AuthViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Login.route)
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.CompleteProfile.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    authViewModel.resetState()
                    navController.navigate(Screen.Register.route)
                },
                viewModel = authViewModel
            )
        }

        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(
                onProfileCompleted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Distributori.route) {
            DistributoriScreen(navController = navController)
        }

        composable(Screen.Storico.route) {
            StoricoScreen(navController = navController)
        }

        composable(Screen.Catalog.route) {
            CatalogScreen(navController = navController)
        }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }

        composable(Screen.Quiz.route) {
            QuizScreen(navController = navController)
        }

        // AGGIUNGI QUESTA ROUTE
        composable(Screen.NotePreferite.route) {
            NotePreferiteScreen(navController = navController)
        }
    }
}