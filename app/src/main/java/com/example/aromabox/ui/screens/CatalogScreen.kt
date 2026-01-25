package com.example.aromabox.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel

// ✅ Colori definiti localmente (o importali da Theme.kt se esistono)
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val AccentColor = Color(0xFF605882)
private val NeutralColor = Color(0xFF737083)

@Composable
fun CatalogScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    // ✅ Corretto: collectAsState() per StateFlow
    val perfumes by catalogViewModel.perfumes.collectAsState()
    val isLoading by catalogViewModel.isLoading.collectAsState()
    val selectedCategory by catalogViewModel.selectedCategory.collectAsState()

    // ✅ Usa funzione pubblica invece di accedere a currentUser privato
    val currentUserId = userViewModel.getCurrentUserId()
    val favoriteIds = userViewModel.getFavoriteIds() // Dovrai creare questa funzione

    val categories = listOf("Tutti", "Floreale", "Fruttato", "Speziato", "Gourmand", "Legnoso")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryColor, SecondaryColor)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Text(
                text = "Catalogo",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Filtro categorie
            ScrollableTabRow(
                selectedTabIndex = categories.indexOf(selectedCategory),
                containerColor = Color.Transparent,
                contentColor = Color.White,
                edgePadding = 0.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                categories.forEach { category ->
                    Tab(
                        selected = selectedCategory == category,
                        onClick = { catalogViewModel.filterByCategory(category) },
                        text = {
                            Text(
                                text = category,
                                fontWeight = if (selectedCategory == category)
                                    FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Loading
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            } else {
                // Grid profumi
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(catalogViewModel.getFilteredPerfumes()) { perfume ->
                        PerfumeCard(
                            perfume = perfume,
                            isFavorite = favoriteIds.contains(perfume.id),
                            onFavoriteClick = {
                                currentUserId?.let { uid ->
                                    userViewModel.toggleFavorite(perfume.id)
                                }
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

@Composable
fun PerfumeCard(
    perfume: Perfume,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                // Immagine
                Image(
                    painter = painterResource(id = perfume.getImageResource()),
                    contentDescription = perfume.nome,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Info
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                ) {
                    Text(
                        text = perfume.marca,
                        fontSize = 12.sp,
                        color = NeutralColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = perfume.nome,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "€ ${String.format("%.2f", perfume.prezzo)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentColor
                    )
                }
            }

            // Bottone Preferiti
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.9f), CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Rimuovi preferito"
                    else "Aggiungi preferito",
                    tint = if (isFavorite) Color.Red else NeutralColor
                )
            }

            // Badge "Non disponibile"
            if (!perfume.disponibile) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Red, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Non disponibile",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}