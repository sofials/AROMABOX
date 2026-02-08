package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.BadgeGridContent
import com.example.aromabox.ui.components.BadgeDetailDialog
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.data.model.Badge
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.*
import com.example.aromabox.ui.viewmodels.UserViewModel
import com.example.aromabox.ui.viewmodels.AuthViewModel
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.utils.getImageResForNota
import kotlinx.coroutines.launch
import java.util.Locale

private val TabActiveBg = Color(0xFFC4B9FF)
private val TabInactiveBg = Color(0xFFEDEDED)
private val SecondaryColor = Color(0xFFC4B9FF)
private val NeutralColor = Color(0xFF737083)
private val EditIconBg = Color(0xFF4D2161)

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

    val userBadges by userViewModel.userBadges.collectAsState()

    var selectedTab by remember { mutableStateOf(ProfileTab.PROFILO_OLFATTIVO) }
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

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
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.White,
                            shadowElevation = 4.dp,
                            shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp, bottom = 16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier.size(80.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(32.dp))
                                                .background(Color(0xFFC4B9FF).copy(alpha = 0.14f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .offset(y = (-10).dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFC4B9FF))
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(60.dp)
                                                    .offset(y = 25.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFFC4B9FF).copy(alpha = 0.80f))
                                            )
                                        }

                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .offset(x = 4.dp, y = 4.dp)
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(EditIconBg)
                                                .clickable { },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Modifica",
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    val displayName = when {
                                        user.nome.isNotBlank() || user.cognome.isNotBlank() ->
                                            "${user.nome} ${user.cognome}".trim()
                                        user.email.isNotBlank() -> user.email.substringBefore("@")
                                        else -> "Utente"
                                    }
                                    Text(
                                        text = displayName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFF1F2024),
                                        letterSpacing = 0.08.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    val nickname = "@${user.email.substringBefore("@").lowercase()}"
                                    Text(
                                        text = nickname,
                                        fontSize = 12.sp,
                                        color = Color(0xFF71727A),
                                        letterSpacing = 0.12.sp,
                                        lineHeight = 16.sp
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp)
                                        .padding(bottom = 16.dp)
                                        .height(29.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(TabInactiveBg)
                                        .padding(4.dp)
                                ) {
                                    ProfileTab.entries.forEach { tab ->
                                        val isSelected = selectedTab == tab
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) TabActiveBg else Color.Transparent)
                                                .clickable { selectedTab = tab },
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
                                                color = if (isSelected) Color.Black else Color(0xFF484C52),
                                                lineHeight = 13.2.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

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
                                            navController.navigate(Screen.PerfumeDetail.createRoute(perfumeId))
                                        },
                                        onRemoveFavorite = { perfumeId ->
                                            userViewModel.toggleFavorite(perfumeId)
                                        }
                                    )
                                }
                                ProfileTab.PROFILO_OLFATTIVO -> {
                                    ProfiloOlfattivoContent(
                                        profilo = user.profiloOlfattivo,
                                        onRifaiQuiz = { navController.navigate(Screen.Quiz.route) },
                                        onNotePreferiteClick = { navController.navigate(Screen.NotePreferite.route) }
                                    )
                                }
                                ProfileTab.BADGE -> {
                                    BadgeGridContent(
                                        badges = userBadges,
                                        onBadgeClick = { badge -> selectedBadge = badge }
                                    )
                                }
                            }
                        }
                    }

                    selectedBadge?.let { badge ->
                        BadgeDetailDialog(
                            badge = badge,
                            onDismiss = { selectedBadge = null }
                        )
                    }
                }
            }
        }
    }
}

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
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = Color(0xFFE0E0E0),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Non hai ancora profumi preferiti",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Esplora il catalogo e salva i tuoi preferiti!",
                    color = Color.LightGray,
                    fontSize = 12.sp,
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
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
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

@Composable
fun FavoritePerfumeCard(
    perfume: Perfume,
    onClick: () -> Unit,
    onRemoveFavorite: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(Color(0xFFF2F2F2)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = perfume.getImageResource()),
                        contentDescription = perfume.nome,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = perfume.marca.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF222222),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 14.sp
                    )

                    Text(
                        text = perfume.nome,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFF1E1E1E),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = String.format(Locale.ITALIAN, "%.2f €", perfume.prezzo),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = NeutralColor,
                        lineHeight = 20.sp
                    )
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp, 22.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(SecondaryColor)
                    .clickable(onClick = onRemoveFavorite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Rimuovi dai preferiti",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
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
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Non hai ancora completato il quiz olfattivo",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRifaiQuiz,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) {
                Text("Fai il quiz")
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                PieChartCompact(profilo = profilo)
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNotePreferiteClick() },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Note preferite",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "›",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val tutteLeNote = profilo.getTutteLeNote()
                        tutteLeNote.take(3).forEach { nota ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
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
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = nota,
                                    fontSize = 9.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRifaiQuiz,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF737083)),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                Text("RIFAI IL TEST", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
@Composable
fun PieChartCompact(profilo: ProfiloOlfattivo) {
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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        // ✅ Usa esplicitamente le proprietà dello scope
        val width = this.maxWidth
        val height = this.maxHeight

        val availableSize = minOf(width, height, 280.dp)
        val boxSize = availableSize * 0.85f

        val canvasSize = boxSize * 0.545f
        val labelRadius = boxSize * 0.409f

        val fontSizeValue = (boxSize.value * 0.041f).coerceIn(8f, 11f)
        val fontSize = fontSizeValue.sp

        val total = percentuali.sum()

        if (total <= 0f) {
            Text(
                text = "Nessun dato disponibile",
                color = Color.Gray,
                fontSize = fontSize
            )
            return@BoxWithConstraints
        }

        val angles = mutableListOf<Float>()
        var currentAngle = -90f

        percentuali.forEach { percentuale ->
            val sweepAngle = (percentuale / total) * 360f
            angles.add(currentAngle + sweepAngle / 2)
            currentAngle += sweepAngle
        }

        Canvas(modifier = Modifier.size(canvasSize)) {
            val strokeWidth = size.minDimension * 0.233f
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

        percentuali.forEachIndexed { index, percentuale ->
            if (percentuale > 0f) {
                val angleRad = Math.toRadians(angles[index].toDouble())
                val xOffset = (labelRadius.value * kotlin.math.cos(angleRad)).dp
                val yOffset = (labelRadius.value * kotlin.math.sin(angleRad)).dp

                Text(
                    text = "${percentuale.toInt()}%\n${nomi[index]}",
                    fontSize = fontSize,
                    fontWeight = FontWeight.Medium,
                    color = coloriTesto[index],
                    textAlign = TextAlign.Center,
                    lineHeight = fontSize * 1.22f,
                    modifier = Modifier.offset(x = xOffset, y = yOffset)
                )
            }
        }
    }
}

@Composable
fun PieChart(profilo: ProfiloOlfattivo) {
    PieChartCompact(profilo)
}

@Composable
fun BadgeContent(badges: List<Badge>) {
    BadgeGridContent(
        badges = badges,
        onBadgeClick = { }
    )
}