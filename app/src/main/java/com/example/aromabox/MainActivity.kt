package com.example.aromabox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.screens.*
import com.example.aromabox.ui.theme.AROMABOXTheme
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel

class MainActivity : ComponentActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AROMABOXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AromaBoxApp(userViewModel = userViewModel)
                }
            }
        }
    }
}

@Composable
fun AromaBoxApp(userViewModel: UserViewModel) {
    val navController = rememberNavController()

    // CatalogViewModel condiviso tra le schermate
    val catalogViewModel: CatalogViewModel = viewModel()

    val currentUser by userViewModel.currentUser.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()

    var hasNavigatedInitially by remember { mutableStateOf(false) }

    if (isLoading && !hasNavigatedInitially) {
        LoadingScreen()
        return
    }

    val startDestination = remember(hasNavigatedInitially) {
        if (!hasNavigatedInitially) {
            hasNavigatedInitially = true
            when {
                currentUser == null -> Screen.Login.route
                currentUser?.profiloOlfattivo == null -> Screen.Quiz.route
                else -> Screen.Home.route
            }
        } else {
            Screen.Home.route
        }
    }

    LaunchedEffect(currentUser) {
        if (hasNavigatedInitially && currentUser == null && !isLoading) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Loading.route) { LoadingScreen() }
        composable(Screen.Login.route) { LoginScreen(navController = navController) }
        composable(Screen.Register.route) { RegisterScreen(navController = navController) }

        composable(Screen.CompleteProfile.route) {
            CompleteProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Screen.Quiz.route) {
            QuizScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Screen.NotePreferite.route) {
            NotePreferiteScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Screen.Home.route) { HomeScreen(navController = navController) }

        composable(Screen.Catalog.route) {
            CatalogScreen(
                navController = navController,
                catalogViewModel = catalogViewModel,
                userViewModel = userViewModel
            )
        }

        // === FILTRI ===
        composable(Screen.Filters.route) {
            FilterScreen(navController = navController, catalogViewModel = catalogViewModel)
        }

        composable(Screen.FilterSort.route) {
            FilterSortScreen(navController = navController, catalogViewModel = catalogViewModel)
        }

        composable(Screen.FilterPrice.route) {
            FilterPriceScreen(navController = navController, catalogViewModel = catalogViewModel)
        }

        composable(Screen.FilterBrand.route) {
            FilterBrandScreen(navController = navController, catalogViewModel = catalogViewModel)
        }

        composable(Screen.FilterGender.route) {
            FilterGenderScreen(navController = navController, catalogViewModel = catalogViewModel)
        }

        composable(Screen.FilterFamily.route) {
            FilterFamilyScreen(navController = navController, catalogViewModel = catalogViewModel)
        }

        composable(Screen.FilterNotes.route) {
            FilterNotesScreen(navController = navController, catalogViewModel = catalogViewModel)
        }
        // === FINE FILTRI ===

        composable(Screen.Distributori.route) { DistributoriScreen(navController = navController) }
        composable(Screen.Storico.route) { StoricoScreen(navController = navController) }

        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController, userViewModel = userViewModel)
        }

        composable(Screen.PerfumeDetail.route) { backStackEntry ->
            val perfumeId = backStackEntry.arguments?.getString("perfumeId")
            if (perfumeId != null) {
                PerfumeDetailScreen(
                    perfumeId = perfumeId,
                    navController = navController,
                    userViewModel = userViewModel,
                    catalogViewModel = catalogViewModel
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}