package com.example.aromabox.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.LogoutConfirmationOverlay
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

private val PageBackground = Color(0xFFF2F2F2)
private val CardBackground = Color(0xFFFAFAFA)
private val PrimaryColor = Color(0xFF8378BF)
private val TextPrimary = Color(0xFF222222)
private val ButtonTextColor = Color(0xFFF2F2F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContattiScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Stato per overlay conferma logout
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        // ModalNavigationDrawer che si apre da DESTRA
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = false,
                drawerContent = {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                        ModalDrawerSheet(
                            drawerContainerColor = Color.Transparent,
                            drawerContentColor = Color.Black,
                            modifier = Modifier.width(300.dp)
                        ) {
                            AppDrawerContent(
                                onCloseClick = {
                                    scope.launch { drawerState.close() }
                                },
                                onInfoClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Screen.Info.route)
                                    }
                                },
                                onContattiClick = {
                                    scope.launch { drawerState.close() }
                                    // Già su Contatti
                                },
                                onDisconnessioneClick = {
                                    scope.launch { drawerState.close() }
                                    showLogoutConfirmation = true
                                }
                            )
                        }
                    }
                }
            ) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Scaffold(
                        topBar = {
                            CommonTopBar(
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(
                                selectedScreen = Screen.Home,
                                navController = navController
                            )
                        },
                        containerColor = PageBackground
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Titolo
                            Text(
                                text = "Contatti",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 0.5.sp,
                                lineHeight = 32.5.sp,
                                modifier = Modifier.padding(start = 46.dp, top = 24.dp, bottom = 24.dp)
                            )

                            // Card Segnalazione
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp)
                                    .height(139.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(25.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Testo "Qualche problema?"
                                    Text(
                                        text = "Qualche problema?",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = Color.Black,
                                        letterSpacing = 0.5.sp,
                                        lineHeight = 23.sp
                                    )

                                    // Bottone "Invia segnalazione"
                                    Button(
                                        onClick = {
                                            // TODO: Implementare invio segnalazione
                                            Toast.makeText(
                                                context,
                                                "Funzionalità in arrivo!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryColor
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 4.dp
                                        )
                                    ) {
                                        Text(
                                            text = "INVIA SEGNALAZIONE",
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = ButtonTextColor,
                                            textAlign = TextAlign.Center,
                                            lineHeight = 18.sp
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }

        // Overlay conferma logout
        LogoutConfirmationOverlay(
            visible = showLogoutConfirmation,
            onConfirm = {
                showLogoutConfirmation = false
                userViewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = {
                showLogoutConfirmation = false
            }
        )
    }
}