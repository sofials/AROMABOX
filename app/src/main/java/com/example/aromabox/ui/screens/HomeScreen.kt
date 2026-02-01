package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.*
import com.example.aromabox.ui.viewmodels.UserViewModel

private val SaldoCardBg = Color(0xFF737083)
private val SaldoTextSecondary = Color(0xFFFFFFFF).copy(alpha = 0.72f)
private val QuizCardBg = Color(0xFFD5CFF6)
private val PageBackground = Color(0xFFF2F2F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    Scaffold(
        topBar = {
            CommonTopBar(
                onMenuClick = { /* TODO: Menu */ }
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
                onRicaricaClick = { navController.navigate(Screen.Recharge.route) }  // ✅ Navigazione
            )

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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = "Trova le fragranze adatte a te",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF1E1E1E),
                    letterSpacing = 0.5.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                    if (selectedScreen != Screen.Distributori) {
                        navController.navigate(Screen.Distributori.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    if (selectedScreen != Screen.Storico) {
                        navController.navigate(Screen.Storico.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
            // Spazio vuoto per il bottone centrale
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
                    if (selectedScreen != Screen.Catalog) {
                        navController.navigate(Screen.Catalog.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                    if (selectedScreen != Screen.Profile) {
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
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
                if (selectedScreen != Screen.Home) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                        launchSingleTop = true
                    }
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