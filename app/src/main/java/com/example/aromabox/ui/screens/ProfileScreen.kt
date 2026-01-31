package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.*
import com.example.aromabox.ui.viewmodels.UserViewModel
import com.example.aromabox.ui.viewmodels.AuthViewModel
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.utils.getImageResForNota
import java.util.Locale

private val TabActiveBg = Color(0xFFCFC5FF)
private val TabInactiveBg = Color(0xFFF2F2F2)
private val SecondaryColor = Color(0xFFC4B9FF)
private val NeutralColor = Color(0xFF737083)

enum class ProfileTab {
    PREFERITI, PROFILO_OLFATTIVO, BADGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel = viewModel(),
    catalogViewModel: CatalogViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val allPerfumes by catalogViewModel.perfumes.collectAsState()

    var selectedTab by remember { mutableStateOf(ProfileTab.PROFILO_OLFATTIVO) }

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
                selectedScreen = Screen.Profile,
                navController = navController
            )
        }
    ) { paddingValues ->

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Impossibile caricare il profilo",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { userViewModel.loadCurrentUser() },
                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                    ) {
                        Text("Riprova")
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    ) {
                        Text("Torna al login", color = Color.Red)
                    }
                }
            }
            return@Scaffold
        }

        val user = currentUser!!

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Profile Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(90.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        val initial = when {
                            user.nome.isNotBlank() -> user.nome.first().uppercase()
                            user.email.isNotBlank() -> user.email.first().uppercase()
                            else -> "?"
                        }
                        Text(
                            text = initial,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable { /* TODO: Edit profile */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Modifica",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val displayName = when {
                    user.nome.isNotBlank() || user.cognome.isNotBlank() ->
                        "${user.nome} ${user.cognome}".trim()
                    user.email.isNotBlank() -> user.email.substringBefore("@")
                    else -> "Utente"
                }
                Text(
                    text = displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                if (user.email.isNotBlank()) {
                    Text(
                        text = user.email,
                        fontSize = 13.sp,
                        color = Color(0xFF777777)
                    )
                }
            }

            // Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(TabInactiveBg)
                    .padding(4.dp)
            ) {
                ProfileTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) TabActiveBg.copy(alpha = 0.8f) else Color.Transparent)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (tab) {
                                ProfileTab.PREFERITI -> "Preferiti"
                                ProfileTab.PROFILO_OLFATTIVO -> "Profilo olfattivo"
                                ProfileTab.BADGE -> "Badge"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) Color.Black else Color(0xFF484C52)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contenuto tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    ProfileTab.PREFERITI -> {
                        val favoritePerfumes = allPerfumes.filter { perfume ->
                            user.preferiti.contains(perfume.id)
                        }
                        PreferitiContentGrid(
                            favoritePerfumes = favoritePerfumes,
                            onPerfumeClick = { perfumeId ->
                                navController.navigate("perfume_detail/$perfumeId")
                            },
                            onRemoveFavorite = { perfumeId ->
                                userViewModel.toggleFavorite(perfumeId)
                            }
                        )
                    }
                    ProfileTab.PROFILO_OLFATTIVO -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            ProfiloOlfattivoContent(
                                profilo = user.profiloOlfattivo,
                                onRifaiQuiz = {
                                    navController.navigate(Screen.Quiz.route)
                                },
                                onNotePreferiteClick = {
                                    navController.navigate(Screen.NotePreferite.route)
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                    ProfileTab.BADGE -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            BadgeContent(badges = user.badges)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }

            // Logout button
            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Logout", color = Color.Red)
            }
        }
    }
}

// ✅ SEZIONE PREFERITI CON GRID (stile Figma)
@Composable
fun PreferitiContentGrid(
    favoritePerfumes: List<Perfume>,
    onPerfumeClick: (String) -> Unit,
    onRemoveFavorite: (String) -> Unit
) {
    if (favoritePerfumes.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Non hai ancora profumi preferiti",
                    color = Color.Gray,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esplora il catalogo e salva i tuoi preferiti!",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(favoritePerfumes) { perfume ->
                FavoritePerfumeCard(
                    perfume = perfume,
                    onClick = { onPerfumeClick(perfume.id) },
                    onRemoveFavorite = { onRemoveFavorite(perfume.id) }
                )
            }
        }
    }
}

// ✅ CARD PROFUMO PREFERITO (stile Figma)
@Composable
fun FavoritePerfumeCard(
    perfume: Perfume,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(257.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                // Area Immagine
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(136.dp)
                        .background(Color(0xFFF2F2F2)),
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

            // Pulsante Cuore pieno (è nei preferiti)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(11.dp)
                    .size(34.dp, 26.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(SecondaryColor)
                    .clickable(onClick = onRemoveFavorite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Rimuovi dai preferiti",
                    tint = Color.White,
                    modifier = Modifier.size(19.dp, 18.dp)
                )
            }
        }
    }
}

@Composable
fun ProfiloOlfattivoContent(
    profilo: ProfiloOlfattivo?,
    onRifaiQuiz: () -> Unit,
    onNotePreferiteClick: () -> Unit
) {
    if (profilo == null || profilo.getTutteLeNote().isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Non hai ancora completato il quiz olfattivo",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onRifaiQuiz,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Fai il quiz")
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PieChart(profilo = profilo)

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { onNotePreferiteClick() },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Note preferite",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "›",
                            fontSize = 18.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        val tutteLeNote = profilo.getTutteLeNote()
                        tutteLeNote.take(3).forEach { nota ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFF5F5F5))
                                ) {
                                    Image(
                                        painter = painterResource(id = getImageResForNota(nota)),
                                        contentDescription = nota,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = nota,
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onRifaiQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF737083)
                )
            ) {
                Text("RIFAI IL TEST")
            }
        }
    }
}

@Composable
fun PieChart(profilo: ProfiloOlfattivo) {
    val percentuali = listOf(
        profilo.getPercentualeFloreale(),
        profilo.getPercentualeFruttata(),
        profilo.getPercentualeSpeziata(),
        profilo.getPercentualeGourmand(),
        profilo.getPercentualeLegnosa()
    )

    val colori = listOf(
        Color(0xFFCCB4E1),
        Color(0xFFDCCDC5),
        Color(0xFFB8D2D6),
        Color(0xFFD8EFFF),
        Color(0xFFC5D2BE)
    )

    val coloriTesto = listOf(
        Color(0xFF9A7BB8),
        Color(0xFFA89A92),
        Color(0xFF7A9EA3),
        Color(0xFF6BA3C7),
        Color(0xFF8A9E7D)
    )

    val nomi = listOf("floreale", "fruttato", "speziato", "gourmand", "legnoso")

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        val total = percentuali.sum()

        if (total <= 0f) {
            Text(
                text = "Nessun dato disponibile",
                color = Color.Gray,
                fontSize = 14.sp
            )
            return@Box
        }

        val angles = mutableListOf<Float>()
        var currentAngle = -90f

        percentuali.forEach { percentuale ->
            val sweepAngle = (percentuale / total) * 360f
            angles.add(currentAngle + sweepAngle / 2)
            currentAngle += sweepAngle
        }

        Canvas(
            modifier = Modifier.size(160.dp)
        ) {
            val strokeWidth = 35.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val topLeft = Offset(
                (size.width - radius * 2) / 2,
                (size.height - radius * 2) / 2
            )

            var startAngle = -90f

            percentuali.forEachIndexed { index, percentuale ->
                if (percentuale > 0f) {
                    val sweepAngle = (percentuale / total) * 360f
                    drawArc(
                        color = colori[index],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = Size(radius * 2, radius * 2),
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweepAngle
                }
            }
        }

        val labelRadius = 115.dp

        percentuali.forEachIndexed { index, percentuale ->
            if (percentuale > 0f) {
                val angleRad = Math.toRadians(angles[index].toDouble())
                val xOffset = (labelRadius.value * kotlin.math.cos(angleRad)).dp
                val yOffset = (labelRadius.value * kotlin.math.sin(angleRad)).dp

                Text(
                    text = "${percentuale.toInt()}%\n${nomi[index]}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = coloriTesto[index],
                    textAlign = TextAlign.Center,
                    lineHeight = 14.sp,
                    modifier = Modifier.offset(x = xOffset, y = yOffset)
                )
            }
        }
    }
}

@Composable
fun BadgeContent(badges: List<com.example.aromabox.data.model.Badge>) {
    if (badges.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Non hai ancora badge",
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Hai ${badges.size} badge",
                fontWeight = FontWeight.Medium
            )
        }
    }
}