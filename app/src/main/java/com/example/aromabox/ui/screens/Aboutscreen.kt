package com.example.aromabox.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.R
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.LogoutConfirmationOverlay
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

private val PageBackground = Color(0xFFF2F2F2)
private val TitleColor = Color(0xFF353244)
private val TextSecondary = Color(0xFF737083)

// Colore ombra leggera
private val ShadowColor = Color(0x40000000) // nero al 25%

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
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
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Screen.Contatti.route)
                                    }
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
                                },
                                onLogoClick = {
                                    // Già su About, non fare nulla
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
                                .padding(paddingValues),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Logo AromaBox grande con ombreggiatura
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "AromaBox Logo",
                                modifier = Modifier
                                    .width(200.dp)
                                    .height(111.dp)
                                    .shadow(
                                        elevation = 4.dp,
                                        spotColor = ShadowColor,
                                        ambientColor = ShadowColor
                                    ),
                                contentScale = ContentScale.Fit
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Titolo/Slogan con ombreggiatura testo
                            Text(
                                text = "La tua esperienza olfattiva\npersonalizzata",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TitleColor,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                                lineHeight = 32.5.sp,
                                style = LocalTextStyle.current.copy(
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = ShadowColor,
                                        offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                                        blurRadius = 4f
                                    )
                                )
                            )

                            // Immagine lineart che occupa TUTTO lo spazio disponibile
                            Image(
                                painter = painterResource(id = R.drawable.lineart_profumi),
                                contentDescription = "Illustrazione profumi",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f) // Occupa tutto lo spazio rimanente
                                    .padding(horizontal = 16.dp),
                                contentScale = ContentScale.Fit
                            )

                            // Testo descrittivo con ombreggiatura
                            Text(
                                text = "Scopri nuove fragranze, prova, cambia, gioca con gli aromi. Ogni scelta è un modo diverso di esprimerti!",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                textAlign = TextAlign.Center,
                                letterSpacing = 0.5.sp,
                                lineHeight = 32.5.sp,
                                modifier = Modifier.padding(horizontal = 40.dp),
                                style = LocalTextStyle.current.copy(
                                    shadow = androidx.compose.ui.graphics.Shadow(
                                        color = ShadowColor,
                                        offset = androidx.compose.ui.geometry.Offset(0f, 4f),
                                        blurRadius = 4f
                                    )
                                )
                            )

                            Spacer(modifier = Modifier.height(24.dp))
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