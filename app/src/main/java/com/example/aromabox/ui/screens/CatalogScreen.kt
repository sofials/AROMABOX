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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
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

// Colori dal Figma
private val PageBackground = Color(0xFFF2F2F2)
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val NeutralColor = Color(0xFF737083)
private val CardBackground = Color.White
private val ImageBackground = Color(0xFFF2F2F2)
private val UnavailableOverlay = Color(0xFFCACACA)
private val UnavailableImageBg = Color(0xFF9A9A9A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel,
    userViewModel: UserViewModel,
) {
    val perfumes by catalogViewModel.perfumes.collectAsState()
    val isLoading by catalogViewModel.isLoading.collectAsState()
    val selectedCategory by catalogViewModel.selectedCategory.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showFavoriteOverlay by remember { mutableStateOf(false) }
    var favoriteWasAdded by remember { mutableStateOf(true) }

    val favoriteIds = currentUser?.preferiti ?: emptyList()

    // Drawer state
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

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
                            onCloseClick = { scope.launch { drawerState.close() } },
                            onInfoClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Screen.Info.route)  // ✅ AGGIUNGI QUESTA RIGA
                                }
                            },
                            onContattiClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Screen.Contatti.route)  // ✅ AGGIUNGI QUESTA RIGA
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
                Box(modifier = Modifier.fillMaxSize()) {
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
                                selectedScreen = Screen.Catalog,
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
                            SearchBarWithFilters(
                                searchQuery = searchQuery,
                                onSearchChange = { searchQuery = it },
                                onFilterClick = { navController.navigate(Screen.Filters.route) }
                            )

                            if (isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryColor)
                                }
                            } else {
                                val filteredPerfumes = catalogViewModel.getFilteredPerfumes(
                                    currentUser?.profiloOlfattivo
                                ).filter {
                                    searchQuery.isEmpty() ||
                                            it.nome.contains(searchQuery, ignoreCase = true) ||
                                            it.marca.contains(searchQuery, ignoreCase = true)
                                }

                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 5.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp),
                                    contentPadding = PaddingValues(
                                        start = 0.dp,
                                        end = 0.dp,
                                        top = 8.dp,
                                        bottom = 16.dp
                                    )
                                ) {
                                    items(filteredPerfumes) { perfume ->
                                        val isFavorite = favoriteIds.contains(perfume.id)
                                        PerfumeCardFigma(
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

                    FavoriteOverlay(
                        visible = showFavoriteOverlay,
                        wasAdded = favoriteWasAdded,
                        onDismiss = { showFavoriteOverlay = false }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteOverlay(
    visible: Boolean,
    wasAdded: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(1500)
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
            Card(
                modifier = Modifier.padding(horizontal = 48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F6FA)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (wasAdded) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

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
fun SearchBarWithFilters(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onFilterClick,
                modifier = Modifier.height(36.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeutralColor,
                    contentColor = PageBackground
                ),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtri",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Filtri",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = "cerca...",
                    color = Color.Black.copy(alpha = 0.57f),
                    fontSize = 16.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cerca",
                    tint = Color(0xFF6B7280)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFFD1D5DB),
                unfocusedBorderColor = Color(0xFFD1D5DB)
            ),
            shape = RoundedCornerShape(63.dp),
            singleLine = true
        )
    }
}

@Composable
fun PerfumeCardFigma(
    perfume: Perfume,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val isAvailable = perfume.disponibile

    Card(
        modifier = Modifier
            .width(188.dp)
            .height(257.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAvailable) CardBackground else UnavailableOverlay
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(136.dp)
                        .background(if (isAvailable) ImageBackground else UnavailableImageBg),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = perfume.getImageResource()),
                        contentDescription = perfume.nome,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )

                    if (!isAvailable) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.20f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "SOLO IN NEGOZIO",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = perfume.marca.uppercase(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF222222),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    Text(
                        text = perfume.nome,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1E1E1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 23.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = String.format(Locale.ITALIAN, "%.2f €", perfume.prezzo),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NeutralColor,
                        lineHeight = 28.64.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(11.dp)
                    .size(34.dp, 26.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(SecondaryColor)
                    .clickable(onClick = onFavoriteClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Rimuovi preferito" else "Aggiungi preferito",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp, 18.dp)
                )
            }
        }
    }
}