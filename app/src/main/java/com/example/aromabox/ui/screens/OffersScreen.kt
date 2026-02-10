package com.example.aromabox.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.R
import com.example.aromabox.data.model.Offer
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.LogoutConfirmationOverlay
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.OffersViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

// Colori coerenti con il tema dell'app
private val PageBackground = Color(0xFFF2F2F2)
private val CardBackground = Color.White
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val AccentColor = Color(0xFF605882)
private val TextPrimary = Color(0xFF222222)
private val TextSecondary = Color(0xFF737083)

// Colori card Home (in linea con QuizCardBg)
private val OffersCardBg = Color(0xFFD5CFF6)

// ============================================================
// OFFERS SCREEN (pagina completa)
// ============================================================

/**
 * Schermata Offerte - View layer che osserva il ViewModel.
 * Seguendo pattern MVVM del docente:
 * - Nessuna logica di business, solo visualizzazione
 * - Osserva lo stato dal ViewModel tramite collectAsState()
 * - Le interazioni utente vengono delegate al ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OffersScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    offersViewModel: OffersViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showLogoutConfirmation by remember { mutableStateOf(false) }

    // Osserva gli stati dal ViewModel tramite collectAsState()
    val offers by offersViewModel.offers.collectAsState()
    val isLoading by offersViewModel.isLoading.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Drawer da DESTRA (pattern consistente con le altre schermate)
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
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onLogoClick = {
                                    navController.navigate(Screen.About.route)
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(
                                selectedScreen = null,
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
                                text = "Sconti per te!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 0.5.sp,
                                lineHeight = 32.5.sp,
                                modifier = Modifier.padding(
                                    start = 46.dp,
                                    top = 24.dp
                                )
                            )

                            // Sottotitolo
                            Text(
                                text = "Ecco gli sconti speciali che abbiamo\npensato per te",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color(0xFF1E1E1E),
                                lineHeight = 23.sp,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(
                                    start = 46.dp,
                                    top = 4.dp,
                                    bottom = 24.dp
                                )
                            )

                            // Lista offerte
                            offers.forEach { offer ->
                                OfferCard(
                                    offer = offer,
                                    modifier = Modifier.padding(
                                        horizontal = 27.dp,
                                        vertical = 8.dp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }

        // Overlay logout
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

// ============================================================
// OFFER CARD (singola offerta nella schermata Offerte)
// ============================================================

/**
 * Card singola offerta - segue il design Figma:
 * Logo brand a sinistra, percentuale sconto + descrizione a destra.
 */
@Composable
private fun OfferCard(
    offer: Offer,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(104.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo brand
            val logoResId = context.resources.getIdentifier(
                offer.imageRes, "drawable", context.packageName
            )
            if (logoResId != 0) {
                Image(
                    painter = painterResource(id = logoResId),
                    contentDescription = "Logo ${offer.brand}",
                    modifier = Modifier
                        .width(86.dp)
                        .height(38.dp),
                    contentScale = ContentScale.Fit
                )
            } else {
                // Placeholder se il logo non esiste
                Box(
                    modifier = Modifier
                        .width(86.dp)
                        .height(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SecondaryColor.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = offer.brand.take(3).uppercase(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentColor
                    )
                }
            }

            Spacer(modifier = Modifier.width(30.dp))

            // Info sconto
            Column {
                Text(
                    text = "${offer.discountPercent}%",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = offer.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}

// ============================================================
// OFFERS HOME CARD (terza card nella HomeScreen)
// ============================================================

/**
 * Card cliccabile per la HomeScreen che porta alla schermata Offerte.
 * Appare come terza card dopo Saldo e Quiz/Profumi Consigliati.
 * Colori in linea con la palette dell'app.
 */
@Composable
fun OffersHomeCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = OffersCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Offerte Speciali",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scopri le promozioni\nesclusive per te",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1E1E1E),
                    letterSpacing = 0.5.sp,
                    lineHeight = 22.sp
                )
            }

            // Tondino con immagine custom - gradiente chiaro
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.White,
                                SecondaryColor.copy(alpha = 0.3f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.perfume_miss_dior),
                    contentDescription = "Offerte",
                    modifier = Modifier.size(92.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}