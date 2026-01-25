package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.*
import com.example.aromabox.ui.viewmodels.UserViewModel
import com.example.aromabox.ui.viewmodels.AuthViewModel
import com.example.aromabox.utils.getImageResForNota

private val TabActiveBg = Color(0xFFCFC5FF)
private val TabInactiveBg = Color(0xFFF2F2F2)

enum class ProfileTab {
    PREFERITI, PROFILO_OLFATTIVO, BADGE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            // Profile Header - NON scorre
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
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
                        Text(
                            text = currentUser?.nome?.firstOrNull()?.uppercase() ?: "?",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    // Edit button
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Primary)
                            .clickable { /* Edit profile */ },
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

                // Nome
                Text(
                    text = "${currentUser?.nome ?: ""} ${currentUser?.cognome ?: ""}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )

                // Email
                Text(
                    text = currentUser?.email ?: "",
                    fontSize = 13.sp,
                    color = Color(0xFF777777)
                )
            }

            // Tabs - NON scorre
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TabInactiveBg)
                    .padding(4.dp)
            ) {
                ProfileTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) TabActiveBg else Color.Transparent)
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
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                            color = if (isSelected) Color.Black else Color(0xFF777777)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contenuto tab - QUESTA PARTE scorre se necessario
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (selectedTab) {
                        ProfileTab.PREFERITI -> PreferitiContent(
                            currentUser?.preferiti ?: emptyList()
                        )
                        ProfileTab.PROFILO_OLFATTIVO -> ProfiloOlfattivoContent(
                            profilo = currentUser?.profiloOlfattivo,
                            onRifaiQuiz = {
                                navController.navigate(Screen.Quiz.route)
                            },
                            onNotePreferiteClick = {
                                navController.navigate(Screen.NotePreferite.route)
                            }
                        )
                        ProfileTab.BADGE -> BadgeContent(
                            currentUser?.badges ?: emptyList()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Logout button - Sempre visibile in fondo
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

@Composable
fun ProfiloOlfattivoContent(
    profilo: ProfiloOlfattivo?,
    onRifaiQuiz: () -> Unit,
    onNotePreferiteClick: () -> Unit
) {
    if (profilo == null) {
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
            // Grafico a torta
            PieChart(profilo = profilo)

            Spacer(modifier = Modifier.height(16.dp))

            // Card note preferite - cliccabile
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

                    // ✅ Mostra le prime 3 note usando getTutteLeNote() dal model
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
        }
    }
}

@Composable
fun PieChart(profilo: ProfiloOlfattivo) {
    // ✅ Usa i metodi del model per ottenere le percentuali
    val percentuali = listOf(
        profilo.getPercentualeFloreale(),
        profilo.getPercentualeFruttata(),
        profilo.getPercentualeSpeziata(),
        profilo.getPercentualeGourmand(),
        profilo.getPercentualeLegnosa()
    )

    val colori = listOf(
        Color(0xFFCCB4E1),  // floreale
        Color(0xFFDCCDC5),  // fruttato
        Color(0xFFB8D2D6),  // speziato
        Color(0xFFD8EFFF),  // gourmand
        Color(0xFFC5D2BE)   // legnoso
    )

    val coloriTesto = listOf(
        Color(0xFF9A7BB8),  // floreale
        Color(0xFFA89A92),  // fruttato
        Color(0xFF7A9EA3),  // speziato
        Color(0xFF6BA3C7),  // gourmand
        Color(0xFF8A9E7D)   // legnoso
    )

    val nomi = listOf("floreale", "fruttato", "speziato", "gourmand", "legnoso")

    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        val total = percentuali.sum()
        val angles = mutableListOf<Float>()
        var currentAngle = -90f

        percentuali.forEach { percentuale ->
            val sweepAngle = if (total > 0f) (percentuale / total) * 360f else 0f
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

            if (total > 0f) {
                percentuali.forEachIndexed { index, percentuale ->
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
fun PreferitiContent(preferiti: List<String>) {
    if (preferiti.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Non hai ancora profumi preferiti",
                color = Color.Gray
            )
        }
    } else {
        Text(
            text = "Hai ${preferiti.size} profumi preferiti",
            modifier = Modifier.padding(16.dp)
        )
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
                color = Color.Gray
            )
        }
    } else {
        Text(
            text = "Hai ${badges.size} badge",
            modifier = Modifier.padding(16.dp)
        )
    }
}