package com.example.aromabox.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.*
import com.example.aromabox.ui.viewmodels.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompleteProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current

    // ✅ Stati locali per i campi
    var nome by remember { mutableStateOf("") }
    var cognome by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // ✅ Osserva l'utente corrente
    val currentUser by userViewModel.currentUser.collectAsState()
    val errorMessage by userViewModel.errorMessage.collectAsState()

    // ✅ Pre-compila con i dati di Google se disponibili
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (nome.isEmpty() && user.nome.isNotEmpty()) {
                nome = user.nome
            }
            if (cognome.isEmpty() && user.cognome.isNotEmpty()) {
                cognome = user.cognome
            }
        }
    }

    // ✅ Gestisci errori
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(LoginGradientStart, LoginGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Benvenuto!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTitleText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Completa il tuo profilo per iniziare",
                fontSize = 16.sp,
                color = LoginSubtitleText,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Campo Nome
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") },
                placeholder = { Text("Il tuo nome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo Cognome
            OutlinedTextField(
                value = cognome,
                onValueChange = { cognome = it },
                label = { Text("Cognome") },
                placeholder = { Text("Il tuo cognome") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    focusedLabelColor = Primary,
                    cursorColor = Primary
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pulsante Continua
            Button(
                onClick = {
                    when {
                        nome.isBlank() -> {
                            Toast.makeText(context, "Inserisci il nome", Toast.LENGTH_SHORT).show()
                        }
                        cognome.isBlank() -> {
                            Toast.makeText(context, "Inserisci il cognome", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            isLoading = true

                            // ✅ Aggiorna nome e cognome dell'utente
                            val userId = currentUser?.uid
                            if (userId != null) {
                                userViewModel.updateUserProfile(
                                    nome = nome.trim(),
                                    cognome = cognome.trim()
                                )

                                // ✅ Naviga al quiz
                                Toast.makeText(context, "Profilo aggiornato!", Toast.LENGTH_SHORT).show()
                                navController.navigate(Screen.Quiz.route) {
                                    popUpTo(Screen.CompleteProfile.route) { inclusive = true }
                                }
                            } else {
                                Toast.makeText(context, "Errore: utente non trovato", Toast.LENGTH_SHORT).show()
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Primary,
                    contentColor = Surface
                ),
                shape = RoundedCornerShape(28.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Surface
                    )
                } else {
                    Text(
                        text = "Continua",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}