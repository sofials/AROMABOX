package com.example.aromabox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.aromabox.data.firebase.AuthManager
import com.example.aromabox.data.repository.UserRepository
import com.example.aromabox.ui.navigation.NavGraph
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.AROMABOXTheme
import com.example.aromabox.viewmodel.UserState
import com.example.aromabox.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AuthManager.initialize(this)

        setContent {
            AROMABOXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val userViewModel: UserViewModel = viewModel()
                    val userState by userViewModel.userState.collectAsState()

                    // Determina startDestination
                    var startDestination by remember { mutableStateOf<String?>(null) }
                    var hasNavigated by remember { mutableStateOf(false) }

                    // All'avvio, controlla lo stato utente
                    LaunchedEffect(Unit) {
                        if (AuthManager.isUserLoggedIn()) {
                            val firebaseUser = AuthManager.getCurrentUser()
                            if (firebaseUser != null) {
                                UserRepository.getOrCreateUser(firebaseUser)
                            }
                            userViewModel.loadCurrentUser()
                        } else {
                            startDestination = Screen.Register.route
                            hasNavigated = true
                        }
                    }

                    // Reagisce al cambio di stato SOLO se non abbiamo già navigato
                    LaunchedEffect(userState) {
                        if (!hasNavigated) {
                            when (userState) {
                                is UserState.Success -> {
                                    startDestination = Screen.Home.route
                                    hasNavigated = true
                                }
                                is UserState.NeedsProfileCompletion -> {
                                    startDestination = Screen.CompleteProfile.route
                                    hasNavigated = true
                                }
                                is UserState.Error -> {
                                    startDestination = Screen.Register.route
                                    hasNavigated = true
                                }
                                else -> {}
                            }
                        }
                    }

                    // Mostra NavGraph solo quando sappiamo dove andare
                    if (startDestination != null) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination!!
                        )
                    }
                }
            }
        }
    }
}