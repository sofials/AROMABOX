package com.example.aromabox.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private val PageBackground = Color(0xFFF2F2F2)
private val CardBackground = Color.White
private val SecondaryColor = Color(0xFFC4B9FF)
private val NeutralColor = Color(0xFF737083)
private val TextDark = Color(0xFF222222)
private val TextBody = Color(0xFF1E1E1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    catalogViewModel: CatalogViewModel
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val perfumes by catalogViewModel.perfumes.collectAsState()
    val profiloOlfattivo = currentUser?.profiloOlfattivo
    val scope = rememberCoroutineScope()

    // Stato del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val recommendedPerfumes = remember(profiloOlfattivo, perfumes) {
        catalogViewModel.getRecommendedPerfumes(profiloOlfattivo)
    }

    val favoriteIds = currentUser?.preferiti ?: emptyList()

    // Stato per overlay preferiti
    var showFavoriteOverlay by remember { mutableStateOf(false) }
    var favoriteWasAdded by remember { mutableStateOf(true) }

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
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Scaffold(
                        topBar = {
                            CommonTopBar(
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onLogoClick = {
                                    navController.navigate(Screen.About.route)  // ✅ Come in HomeScreen
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
                        ) {
                            // Header con freccia indietro
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp)
                            ) {
                                // Freccia indietro
                                IconButton(
                                    onClick = {
                                        navController.navigate(Screen.Home.route) {
                                            popUpTo(Screen.Home.route) { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.align(Alignment.CenterStart)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Torna alla Home",
                                        tint = Color.Black
                                    )
                                }
                            }

                            // Titolo e sottotitolo
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Grazie per le tue risposte!",
                                    fontSize = 25.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Ecco le fragranze più adatte ai tuoi gusti\npresenti nel distributore",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = TextBody,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 23.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Griglia profumi consigliati
                            if (recommendedPerfumes.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "Nessun profumo trovato.\nProva a rifare il quiz selezionando più note!",
                                        fontSize = 16.sp,
                                        color = NeutralColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    contentPadding = PaddingValues(
                                        start = 6.dp,
                                        end = 6.dp,
                                        bottom = 16.dp
                                    ),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(recommendedPerfumes) { perfume ->
                                        val isFavorite = favoriteIds.contains(perfume.id)
                                        RecommendedPerfumeCard(
                                            perfume = perfume,
                                            isFavorite = isFavorite,
                                            onFavoriteClick = {
                                                favoriteWasAdded = !isFavorite
                                                userViewModel.toggleFavorite(perfume.id)
                                                showFavoriteOverlay = true
                                            },
                                            onClick = {
                                                navController.navigate(Screen.PerfumeDetail.createRoute(perfume.id))
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Overlay aggiunto/rimosso preferiti
        RecommendedFavoriteOverlay(
            visible = showFavoriteOverlay,
            wasAdded = favoriteWasAdded,
            onDismiss = { showFavoriteOverlay = false }
        )
    }
}

/**
 * Overlay compatto per aggiunta/rimozione preferiti
 * Scompare automaticamente dopo 1.5 secondi
 */
@Composable
fun RecommendedFavoriteOverlay(
    visible: Boolean,
    wasAdded: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(1500) // Scompare dopo 1.5 secondi
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(initialScale = 0.8f, animationSpec = tween(200)),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(targetScale = 0.8f, animationSpec = tween(200))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            // Card compatta
            Card(
                modifier = Modifier
                    .padding(horizontal = 48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F6FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Icona cuore
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (wasAdded) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Testo
                    Text(
                        text = if (wasAdded) "Aggiunto ai preferiti!" else "Rimosso dai preferiti",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2A282F)
                    )
                }
            }
        }
    }
}

@Composable
fun RecommendedPerfumeCard(
    perfume: Perfume,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(257.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Immagine prodotto
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(PageBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = perfume.getImageResource()),
                        contentDescription = perfume.nome,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Info prodotto
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    // Marca
                    Text(
                        text = perfume.marca.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    // Nome
                    Text(
                        text = perfume.nome,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextBody,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 23.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Prezzo
                    Text(
                        text = String.format(Locale.ITALIAN, "%.2f €", perfume.prezzo),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NeutralColor,
                        lineHeight = 28.sp
                    )
                }
            }

            // Bottone cuore
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 11.dp, end = 8.dp)
                    .size(34.dp, 26.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(SecondaryColor)
                    .clickable(onClick = onFavoriteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = if (isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}