package com.example.aromabox.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
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
    catalogViewModel: CatalogViewModel = viewModel(),
    userViewModel: UserViewModel,
) {
    // StateFlow observers
    val perfumes by catalogViewModel.perfumes.collectAsState()
    val isLoading by catalogViewModel.isLoading.collectAsState()
    val selectedCategory by catalogViewModel.selectedCategory.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    // Stato locale per la ricerca
    var searchQuery by remember { mutableStateOf("") }

    // Preferiti dell'utente
    val favoriteIds = currentUser?.preferiti ?: emptyList()

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
            // ✅ NUOVA Barra di ricerca con Filtri in alto a destra
            SearchBarWithFilters(
                searchQuery = searchQuery,
                onSearchChange = { searchQuery = it },
                onFilterClick = {
                    navController.navigate(Screen.Filters.route)
                }
            )

            // Contenuto principale
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                }
            } else {
                // Filtra per ricerca
                val filteredPerfumes = catalogViewModel.getFilteredPerfumes().filter {
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
                        PerfumeCardFigma(
                            perfume = perfume,
                            isFavorite = favoriteIds.contains(perfume.id),
                            onFavoriteClick = {
                                userViewModel.toggleFavorite(perfume.id)
                            },
                            onClick = {
                                navController.navigate("perfume_detail/${perfume.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

// ✅ NUOVA Barra di ricerca con Filtri in alto a destra
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
        // Riga superiore: Titolo + Filtri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pulsante Filtri in alto a destra
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

        // Campo di ricerca sotto
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
                // Area Immagine
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

                    // Overlay "SOLO IN NEGOZIO" se non disponibile
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
                        color = Color(0xFF222222),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 18.sp
                    )

                    // Nome prodotto
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

                    // Prezzo
                    Text(
                        text = String.format(Locale.ITALIAN, "%.2f €", perfume.prezzo),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = NeutralColor,
                        lineHeight = 28.64.sp
                    )
                }
            }

            // Pulsante Preferiti (in alto a destra)
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