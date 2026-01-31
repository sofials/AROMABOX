package com.example.aromabox.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.Primary
import com.example.aromabox.ui.theme.Secondary
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import java.util.Locale

// Colori dal Figma
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val BackgroundColor = Color(0xFFF2F2F2)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF6B7280)
private val TextGray = Color(0xFF374151)
private val AccentPink = Color(0xFFFB2879)
private val NeutralColor = Color(0xFF737083)

// Colori piramide olfattiva
private val NotaTestaColor = Color(0xFF837ABF).copy(alpha = 0.40f)
private val NotaCuoreColor = Color(0xFF837ABF).copy(alpha = 0.24f)
private val NotaFondoColor = Color(0xFF837ABF).copy(alpha = 0.12f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfumeDetailScreen(
    perfumeId: String,
    navController: NavController,
    userViewModel: UserViewModel,
    catalogViewModel: CatalogViewModel
) {
    val allPerfumes by catalogViewModel.perfumes.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()

    val perfume = allPerfumes.find { it.id == perfumeId }
    val isFavorite = currentUser?.preferiti?.contains(perfumeId) ?: false

    if (perfume == null) {
        // Loading o profumo non trovato
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryColor)
        }
        return
    }

    Scaffold(
        topBar = {
            // TopBar con freccia indietro invece del logo
            DetailTopBar(
                onBackClick = { navController.popBackStack() },
                onMenuClick = { /* TODO: Menu */ }
            )
        },
        bottomBar = {
            // Bottom navigation bar standard + bottone Acquista
            DetailBottomSection(
                navController = navController,
                onAcquistaClick = {
                    // TODO: Navigare alla schermata di acquisto
                }
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Immagine grande del profumo
            PerfumeImageSection(
                perfume = perfume,
                isFavorite = isFavorite,
                onFavoriteClick = { userViewModel.toggleFavorite(perfumeId) }
            )

            // Contenuto dettagli
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Marca
                Text(
                    text = perfume.marca.uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF222222),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Nome e Prezzo
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = perfume.nome,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "Prezzo",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF634BE6)
                        )
                        Text(
                            text = String.format(Locale.ITALIAN, "%.2f €", perfume.prezzo),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Recensioni
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "4.5",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    // Stelle
                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < 4) Color(0xFFFFD700) else Color(0xFFE0E0E0),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "+12000",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = AccentPink
                    )
                    Text(
                        text = " Recensioni",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Descrizione
                Text(
                    text = getDescrizioneForPerfume(perfume),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Area Olfattiva
                Text(
                    text = "AREA OLFATTIVA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = perfume.categoria.replaceFirstChar { it.uppercase() },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextGray
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Piramide Olfattiva
                Text(
                    text = "PIRAMIDE OLFATTIVA",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Note di Testa
                PiramideNotaRow(
                    label = "Note di testa",
                    note = perfume.noteOlfattive.noteDiTesta.joinToString(", ") {
                        it.replaceFirstChar { c -> c.uppercase() }
                    },
                    backgroundColor = NotaTestaColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Note di Cuore
                PiramideNotaRow(
                    label = "Note di cuore",
                    note = perfume.noteOlfattive.noteDiCuore.joinToString(", ") {
                        it.replaceFirstChar { c -> c.uppercase() }
                    },
                    backgroundColor = NotaCuoreColor
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Note di Fondo
                PiramideNotaRow(
                    label = "Note di fondo",
                    note = perfume.noteOlfattive.noteDiFondo.joinToString(", ") {
                        it.replaceFirstChar { c -> c.uppercase() }
                    },
                    backgroundColor = NotaFondoColor
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// TopBar con freccia indietro (simile a CommonTopBar ma con back arrow)
@Composable
fun DetailTopBar(
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SecondaryColor.copy(alpha = 0.8f),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Freccia indietro invece del logo
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = NeutralColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Menu hamburger
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color.Black
                )
            }
        }
    }
}

// Sezione immagine profumo
@Composable
fun PerfumeImageSection(
    perfume: Perfume,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(315.dp)
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Immagine profumo
        Image(
            painter = painterResource(id = perfume.getImageResource()),
            contentDescription = perfume.nome,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )

        // Pulsante Preferiti in alto a destra
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(41.dp, 37.dp)
                .clip(RoundedCornerShape(15.dp))
                .background(SecondaryColor)
                .clickable(onClick = onFavoriteClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = if (isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}

@Composable
fun PiramideNotaRow(
    label: String,
    note: String,
    backgroundColor: Color
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = TextGray
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = note.ifEmpty { "-" },
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = TextGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

// Bottom section con navbar + bottone Acquista
@Composable
fun DetailBottomSection(
    navController: NavController,
    onAcquistaClick: () -> Unit
) {
    Column {
        // Bottone Acquista sopra la navbar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onAcquistaClick,
                    modifier = Modifier
                        .width(157.dp)
                        .height(48.dp),
                    shape = RoundedCornerShape(15.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryColor
                    )
                ) {
                    Text(
                        text = "Acquista",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }

        // Bottom Navigation Bar standard
        BottomNavigationBar(
            selectedScreen = Screen.Catalog, // Siamo nel contesto catalogo
            navController = navController
        )
    }
}

// Helper per generare descrizione
fun getDescrizioneForPerfume(perfume: Perfume): String {
    val noteTesta = perfume.noteOlfattive.noteDiTesta.take(2).joinToString(" e ") {
        it.replaceFirstChar { c -> c.lowercase() }
    }
    val noteCuore = perfume.noteOlfattive.noteDiCuore.firstOrNull()?.lowercase() ?: ""
    val noteFondo = perfume.noteOlfattive.noteDiFondo.take(2).joinToString(" e ") {
        it.replaceFirstChar { c -> c.lowercase() }
    }

    return "Una fragranza ${perfume.categoria}, una miscela di $noteTesta" +
            (if (noteCuore.isNotEmpty()) " e $noteCuore" else "") +
            ". ${noteFondo.replaceFirstChar { it.uppercase() }} rifiniscono la fragranza con un sentore evocativo e persistente."
}