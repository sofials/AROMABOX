package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.LogoutConfirmationOverlay
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
import com.example.aromabox.ui.screens.OffersHomeCard
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

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

    // Stato per overlay conferma logout
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    // Stato per tutorial
    var showTutorial by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

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
                                    navController.navigate(Screen.About.route)
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

                            // Card Collegati al Distributore con bottone "?"
                            ConnectDistributorCard(
                                onHelpClick = { showTutorial = true }
                            )

                            // Controlla se il quiz è completo
                            val hasCompletedQuiz = currentUser?.profiloOlfattivo != null

                            if (!hasCompletedQuiz) {
                                QuizCard(
                                    onClick = { navController.navigate(Screen.Quiz.route) }
                                )
                            } else {
                                RecommendedCard(
                                    onClick = { navController.navigate(Screen.Recommended.route) }
                                )
                            }
                            OffersHomeCard(
                                onClick = { navController.navigate(Screen.Offers.route) }
                            )
                        }
                    }
                }
            }
        }

        // Overlay tutorial
        if (showTutorial) {
            TutorialOverlay(
                onDismiss = { showTutorial = false }
            )
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

/**
 * Overlay Tutorial con card swipeable
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun TutorialOverlay(
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState()

    val tutorialPages = listOf(
        TutorialPage(
            title = "Benvenuto in AromaBox",
            description = "Con AromaBox puoi acquistare una coccola profumata direttamente tramite cellulare, in maniera sicura e pratica",
            // Sostituisci con le tue immagini vettoriali
            imageRes1 = R.drawable.ic_phone, // immagine cellulare
            imageRes2 = R.drawable.ic_perfume // immagine boccetta
        ),
        TutorialPage(
            title = "Paga una vaporizzazione",
            description = "Paga una vaporizzazione di profumo tramite il tuo borsellino virtuale oppure direttamente con carta di credito",
            imageRes1 = R.drawable.ic_phone,
            imageRes2 = R.drawable.ic_perfume
        ),
        TutorialPage(
            title = "Borsellino virtuale",
            description = "Puoi ricaricare il tuo borsellino virtuale tramite carta di credito",
            imageRes1 = R.drawable.ic_phone,
            imageRes2 = R.drawable.ic_perfume
        ),
        TutorialPage(
            title = "Collegati al distributore",
            description = "Puoi collegarti a un distributore abilitato scansionando il codice QR",
            imageRes1 = R.drawable.ic_phone,
            imageRes2 = R.drawable.ic_perfume
        ),
        TutorialPage(
            title = "Scegli la tua fragranza",
            description = "Scegli la fragranza che più ti piace e posizionati davanti al distributore",
            imageRes1 = R.drawable.ic_phone,
            imageRes2 = R.drawable.ic_perfume
        ),
        TutorialPage(
            title = "Termina l'acquisto",
            description = "Terminato l'acquisto chiudi il collegamento dall'applicazione: il tuo credito verrà aggiornato automaticamente",
            imageRes1 = R.drawable.ic_phone,
            imageRes2 = R.drawable.ic_perfume
        )
    )

    // Overlay scuro
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.Center
    ) {
        // Card Tutorial
        Card(
            modifier = Modifier
                .width(348.dp)
                .height(401.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header viola con titolo e X
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PrimaryColor)
                        .padding(25.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tutorialPages[pagerState.currentPage].title,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 25.sp,
                        letterSpacing = 0.2.sp,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = Color.White
                        )
                    }
                }

                // Pager per le card
                HorizontalPager(
                    count = tutorialPages.size,
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    TutorialPageContent(tutorialPages[page])
                }

                // Indicatori di pagina
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tutorialPages.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(
                                    width = if (index == pagerState.currentPage) 15.dp else 4.5.dp,
                                    height = 4.5.dp
                                )
                                .clip(RoundedCornerShape(if (index == pagerState.currentPage) 4.dp else 9999.dp))
                                .background(
                                    if (index == pagerState.currentPage) PrimaryColor
                                    else Color(0xFFC4C4C4)
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Contenuto di una pagina del tutorial con due immagini vettoriali vicine
 */
/**
 * Contenuto di una pagina del tutorial con due immagini vettoriali vicine
 */
/**
 * Contenuto di una pagina del tutorial con due immagini vettoriali vicine
 */
@Composable
fun TutorialPageContent(page: TutorialPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // Due immagini vettoriali vicine
        Row(
            modifier = Modifier.height(145.dp), // Aumentato per contenere il telefono più grande
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Prima immagine (cellulare) - PIÙ GRANDE
            Image(
                painter = painterResource(id = page.imageRes1),
                contentDescription = null,
                modifier = Modifier
                    .width(75.dp)   // Aumentato da 60dp
                    .height(140.dp), // Aumentato da 115dp
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(2.dp))  // ✅ Ridotto a 2dp per immagini molto vicine

            // Seconda immagine (boccetta profumo) - più piccola
            Image(
                painter = painterResource(id = page.imageRes2),
                contentDescription = null,
                modifier = Modifier.size(69.dp), // Mantiene dimensione originale
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Testo descrittivo
        Text(
            text = page.description,
            textAlign = TextAlign.Center,
            color = Color.Black,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 23.sp,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))
    }
}
/**
 * Data class per le pagine del tutorial con due immagini
 */
data class TutorialPage(
    val title: String,
    val description: String,
    val imageRes1: Int, // Prima immagine (cellulare)
    val imageRes2: Int  // Seconda immagine (boccetta)
)

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
 */
private const val TONE_DURATION_MS = 150
private const val DELAY_BETWEEN_TONES_MS = 100L

/**
 * Card per collegarsi al distributore tramite toni DTMF
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectDistributorCard(
    onHelpClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var pinInput by remember { mutableStateOf("") }
    var isPlaying by remember { mutableStateOf(false) }
    var currentDigitIndex by remember { mutableStateOf(-1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ConnectCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        // Box principale per posizionamento assoluto del bottone "?"
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Bottone "?" in alto a destra (posizione assoluta rispetto alla Card)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 12.dp)
                    .size(width = 48.dp, height = 45.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SaldoCardBg), // Colore cambiato da AccentColor a SaldoCardBg
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onHelpClick,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.HelpOutline,
                        contentDescription = "Aiuto",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            // Contenuto principale con padding normale
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            ) {
                // Titolo centrato (ora è veramente centrato)
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

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { newValue ->
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

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 25.dp)
                    .padding(bottom = 35.dp),
                contentAlignment = Alignment.Center
            ) {
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
    selectedScreen: Screen?,
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
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
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
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
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
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
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
                        popUpTo(Screen.Home.route) {
                            inclusive = false
                        }
                        launchSingleTop = true
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

        FloatingActionButton(
            onClick = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) {
                        inclusive = true
                    }
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