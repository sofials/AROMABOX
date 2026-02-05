package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.AppDrawerContent
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.aromabox.R
import java.util.Locale
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.*
import com.example.aromabox.ui.viewmodels.UserViewModel
import com.example.aromabox.utils.DTMFGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val SaldoCardBg = Color(0xFF737083)
private val SaldoTextSecondary = Color(0xFFFFFFFF).copy(alpha = 0.72f)
private val QuizCardBg = Color(0xFFD5CFF6)
private val ConnectCardBg = Color(0xFFFAFAFA)
private val PageBackground = Color(0xFFF2F2F2)
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val AccentColor = Color(0xFF918DAA)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    // Stato del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    // ModalNavigationDrawer che si apre da DESTRA
    // Per aprire da destra, invertiamo la direzione del layout
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = false,
            drawerContent = {
                // Reimposta LTR per il contenuto del drawer
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
                                    // TODO: Naviga a Info
                                }
                            },
                            onContattiClick = {
                                scope.launch {
                                    drawerState.close()
                                    // TODO: Naviga a Contatti
                                }
                            },
                            onDisconnessioneClick = {
                                scope.launch {
                                    drawerState.close()
                                    userViewModel.logout()
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                        )
                    }
                }
            }
        ) {
            // Reimposta LTR per il contenuto principale
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Scaffold(
                    topBar = {
                        CommonTopBar(
                            onMenuClick = {
                                scope.launch { drawerState.open() }
                            },
                            onLogoClick = {
                                // TODO: Azione al click sul logo
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
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Card Saldo
                        SaldoCard(
                            saldo = currentUser?.wallet ?: 0.0,
                            onRicaricaClick = { navController.navigate(Screen.Recharge.route) }
                        )

                        // Card Collegati al Distributore
                        ConnectDistributorCard()

                        // Controlla se il quiz è completo
                        val hasCompletedQuiz = currentUser?.profiloOlfattivo != null

                        if (!hasCompletedQuiz) {
                            QuizCard(
                                onClick = { navController.navigate(Screen.Quiz.route) }
                            )
                        } else {
                            // Card Profumi Consigliati (dopo quiz completato)
                            RecommendedCard(
                                onClick = { navController.navigate(Screen.Recommended.route) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SaldoCard(
    saldo: Double,
    onRicaricaClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(131.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SaldoCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(25.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SALDO DISPONIBILE",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = SaldoTextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = String.format(Locale.ITALIAN, "%.2f €", saldo),
                    fontSize = 33.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Button(
                onClick = onRicaricaClick,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Neutral
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Ricarica",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}

/**
 * Card per collegarsi al distributore tramite toni DTMF
 *
 * CONFIGURAZIONE TIMING (modificare questi valori per sincronizzarsi col microfono):
 */
private const val TONE_DURATION_MS = 150      // Durata di ogni singolo tono in millisecondi
private const val DELAY_BETWEEN_TONES_MS = 100L  // Pausa tra un tono e l'altro in millisecondi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectDistributorCard() {
    val scope = rememberCoroutineScope()

    // Stati
    var pinInput by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var currentDigitIndex by remember { mutableStateOf(-1) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ConnectCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(25.dp)
        ) {
            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Collegati",
                    fontSize = 33.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "al distributore automatico",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Campo PIN
            OutlinedTextField(
                value = pinInput,
                onValueChange = { newValue ->
                    // Accetta solo numeri, max 6 cifre
                    if (newValue.all { it.isDigit() } && newValue.length <= 6) {
                        pinInput = newValue
                    }
                },
                label = { Text("Inserisci PIN") },
                placeholder = { Text("123456") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !isPlaying,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    focusedLabelColor = PrimaryColor,
                    cursorColor = PrimaryColor
                ),
                trailingIcon = {
                    if (pinInput.isNotEmpty() && !isPlaying) {
                        IconButton(onClick = { pinInput = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Cancella"
                            )
                        }
                    }
                }
            )

            // Visualizzazione PIN con highlight cifra corrente
            if (pinInput.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    pinInput.forEachIndexed { index, digit ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (index == currentDigitIndex) PrimaryColor
                                    else SecondaryColor.copy(alpha = 0.3f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = digit.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (index == currentDigitIndex) Color.White else Color.Black
                            )
                        }
                        if (index < pinInput.length - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bottone Play
            Button(
                onClick = {
                    if (!isPlaying && pinInput.isNotEmpty()) {
                        isPlaying = true
                        currentDigitIndex = -1

                        scope.launch {
                            withContext(Dispatchers.IO) {
                                DTMFGenerator.playSequence(
                                    sequence = pinInput,
                                    toneDurationMs = TONE_DURATION_MS,
                                    delayBetweenTonesMs = DELAY_BETWEEN_TONES_MS,
                                    onDigitPlayed = { _, index ->
                                        currentDigitIndex = index
                                    }
                                )
                            }
                            // Reset dopo il completamento
                            currentDigitIndex = -1
                            isPlaying = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(40.dp),
                enabled = pinInput.isNotEmpty() && !isPlaying,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(SecondaryColor, Color(0xFF998DDB))
                            ),
                            shape = RoundedCornerShape(40.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.VolumeUp else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "In riproduzione" else "Riproduci",
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isPlaying) "Trasmissione..." else "Trasmetti PIN",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }

            // Indicatore di stato
            if (isPlaying) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = PrimaryColor,
                    trackColor = SecondaryColor.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
fun QuizCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(378.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = QuizCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header con testo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Fai il nostro quiz",
                    fontSize = 33.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.5.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Trova le fragranze adatte a te",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1E1E1E),
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Area immagine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 25.dp)
                    .padding(bottom = 35.dp),
                contentAlignment = Alignment.Center
            ) {
                // Cerchio sfondo con gradient
                Box(
                    modifier = Modifier
                        .size(230.dp)
                        .clip(RoundedCornerShape(200.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1E1E1E),
                                    Color(0xFFC4B9FF)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Immagine profumi
                    Image(
                        painter = painterResource(id = R.drawable.quiz_perfumes),
                        contentDescription = "Profumi",
                        modifier = Modifier.size(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = QuizCardBg),
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
                    text = "Profumi consigliati",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 28.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scopri le fragranze\nadatte a te",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1E1E1E),
                    lineHeight = 20.sp
                )
            }

            // Icona/immagine
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF1E1E1E),
                                Color(0xFFC4B9FF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.quiz_perfumes),
                    contentDescription = "Profumi",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedScreen: Screen,
    navController: NavController
) {
    Box {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Primary,
            modifier = Modifier.height(84.dp)
        ) {
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedScreen == Screen.Distributori)
                            Icons.Filled.LocationOn else Icons.Outlined.LocationOn,
                        contentDescription = "Mappa"
                    )
                },
                label = { Text("Mappa", fontSize = 12.sp) },
                selected = selectedScreen == Screen.Distributori,
                onClick = {
                    navController.navigate(Screen.Distributori.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Secondary
                )
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedScreen == Screen.Storico)
                            Icons.Filled.Receipt else Icons.Outlined.Receipt,
                        contentDescription = "Storico"
                    )
                },
                label = { Text("Storico", fontSize = 12.sp) },
                selected = selectedScreen == Screen.Storico,
                onClick = {
                    navController.navigate(Screen.Storico.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Secondary
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedScreen == Screen.Catalog)
                            Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                        contentDescription = "Catalogo"
                    )
                },
                label = { Text("Catalogo", fontSize = 12.sp) },
                selected = selectedScreen == Screen.Catalog,
                onClick = {
                    navController.navigate(Screen.Catalog.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Secondary
                )
            )
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selectedScreen == Screen.Profile)
                            Icons.Filled.Person else Icons.Outlined.Person,
                        contentDescription = "Profilo"
                    )
                },
                label = { Text("Profilo", fontSize = 12.sp) },
                selected = selectedScreen == Screen.Profile,
                onClick = {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Primary,
                    selectedTextColor = Primary,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                    indicatorColor = Secondary
                )
            )
        }

        // Bottone Home centrale flottante
        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                    launchSingleTop = true
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-10).dp)
                .size(56.dp),
            shape = RoundedCornerShape(50.dp),
            containerColor = Secondary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}