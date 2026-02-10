package com.example.aromabox.ui.screens

import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.AppDrawerContent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.aromabox.R
import com.example.aromabox.data.model.ProfiloOlfattivo
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Colori Quiz
private val QuizProgressBg = Color(0xFFEEEEEE)
private val QuizProgressFill = Color(0xFF8378BF)
private val QuizAccent = Color(0xFF9A91C9)
private val SecondaryColor = Color(0xFFC4B9FF)

data class NotaOlfattiva(
    val nome: String,
    val imageRes: Int
)

val noteFloreali = listOf(
    NotaOlfattiva("Tuberosa", R.drawable.tuberosa),
    NotaOlfattiva("Gelsomino", R.drawable.gelsomino),
    NotaOlfattiva("Rosa rossa", R.drawable.rosa_rossa),
    NotaOlfattiva("Giglio", R.drawable.giglio),
    NotaOlfattiva("Narciso", R.drawable.narciso),
    NotaOlfattiva("Magnolia", R.drawable.magnolia),
    NotaOlfattiva("Gardenia", R.drawable.gardenia),
    NotaOlfattiva("Fiore d'arancio", R.drawable.fiore_arancio),
    NotaOlfattiva("Peonia", R.drawable.peonia),
    NotaOlfattiva("Viola", R.drawable.viola),
    NotaOlfattiva("Ylang-ylang", R.drawable.ylang_ylang),
    NotaOlfattiva("Rosa Bianca", R.drawable.rosa_bianca),
    NotaOlfattiva("Geranio", R.drawable.geranio),
    NotaOlfattiva("Iris", R.drawable.iris),
    NotaOlfattiva("Loto", R.drawable.loto),
    NotaOlfattiva("Osmanto", R.drawable.osmanto),
    NotaOlfattiva("Neroli", R.drawable.neroli),
    NotaOlfattiva("Fresia", R.drawable.fresia),
    NotaOlfattiva("Labdano", R.drawable.labdano),
    NotaOlfattiva("Orchidea", R.drawable.orchidea)
)

val noteFruttate = listOf(
    NotaOlfattiva("Pesca", R.drawable.pesca),
    NotaOlfattiva("Mela verde", R.drawable.mela_verde),
    NotaOlfattiva("Mela rossa", R.drawable.mela_rossa),
    NotaOlfattiva("Albicocca", R.drawable.albicocca),
    NotaOlfattiva("Mora", R.drawable.mora),
    NotaOlfattiva("Ciliegia", R.drawable.ciliegia),
    NotaOlfattiva("Litchi", R.drawable.litchi),
    NotaOlfattiva("Prugna", R.drawable.prugna),
    NotaOlfattiva("Ananas", R.drawable.ananas),
    NotaOlfattiva("Ribes", R.drawable.ribes),
    NotaOlfattiva("Fico", R.drawable.fico),
    NotaOlfattiva("Pera", R.drawable.pera),
    NotaOlfattiva("Fragola", R.drawable.fragola),
    NotaOlfattiva("Lampone", R.drawable.lampone),
    NotaOlfattiva("Melograno", R.drawable.melograno),
    NotaOlfattiva("Mango", R.drawable.mango),
    NotaOlfattiva("Frutto della passione", R.drawable.frutto_della_passione)
)

val noteSpeziate = listOf(
    NotaOlfattiva("Pepe rosa", R.drawable.pepe_rosa),
    NotaOlfattiva("Pepe nero", R.drawable.pepe_nero),
    NotaOlfattiva("Cannella", R.drawable.cannella),
    NotaOlfattiva("Cardamomo", R.drawable.cardamomo),
    NotaOlfattiva("Caffè", R.drawable.caffe),
    NotaOlfattiva("Curry", R.drawable.curry),
    NotaOlfattiva("Anice", R.drawable.anice),
    NotaOlfattiva("Zenzero", R.drawable.zenzero),
    NotaOlfattiva("Zafferano", R.drawable.zafferano)
)

val noteGourmand = listOf(
    NotaOlfattiva("Caramello", R.drawable.caramello),
    NotaOlfattiva("Vaniglia", R.drawable.vaniglia),
    NotaOlfattiva("Crema", R.drawable.crema),
    NotaOlfattiva("Miele", R.drawable.miele),
    NotaOlfattiva("Biscotto", R.drawable.biscotto),
    NotaOlfattiva("Cocco", R.drawable.cocco),
    NotaOlfattiva("Pralina", R.drawable.pralina),
    NotaOlfattiva("Cioccolato", R.drawable.cioccolato),
    NotaOlfattiva("Mandorla", R.drawable.mandorla),
    NotaOlfattiva("Pistacchio", R.drawable.pistacchio),
    NotaOlfattiva("Nocciola", R.drawable.nocciola),
    NotaOlfattiva("Castagna", R.drawable.castagna),
    NotaOlfattiva("Cacao", R.drawable.cacao),
    NotaOlfattiva("Marshmallow", R.drawable.marshmallow),
    NotaOlfattiva("Latte", R.drawable.latte),
    NotaOlfattiva("Gelato", R.drawable.gelato),
    NotaOlfattiva("Zucchero di canna", R.drawable.zucchero_canna),
    NotaOlfattiva("Rum", R.drawable.rum),
    NotaOlfattiva("Cognac", R.drawable.cognac)
)

val noteLegnose = listOf(
    NotaOlfattiva("Oud", R.drawable.oud),
    NotaOlfattiva("Cedro", R.drawable.cedro),
    NotaOlfattiva("Sandalo", R.drawable.sandalo),
    NotaOlfattiva("Patchouli", R.drawable.patchouli),
    NotaOlfattiva("Vetiver", R.drawable.vetiver),
    NotaOlfattiva("Muschio", R.drawable.muschio),
    NotaOlfattiva("Pelle", R.drawable.pelle),
    NotaOlfattiva("Guaiaco", R.drawable.guaiaco)
)

data class QuizStep(
    val titolo: String,
    val note: List<NotaOlfattiva>
)

val quizSteps = listOf(
    QuizStep("Scegli le note floreali che ti piacciono di più", noteFloreali),
    QuizStep("Scegli le note fruttate che ti piacciono di più", noteFruttate),
    QuizStep("Scegli le note speziate che ti piacciono di più", noteSpeziate),
    QuizStep("Scegli le note gourmand che ti piacciono di più", noteGourmand),
    QuizStep("Scegli le note legnose che ti piacciono di più", noteLegnose)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current

    var currentStep by remember { mutableIntStateOf(0) }
    var selectedNotes by remember { mutableStateOf(mapOf<Int, List<String>>()) }

    // Stato per l'overlay di completamento
    var showCompletedOverlay by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    LaunchedEffect(Unit) {
        userViewModel.loadCurrentUser()
    }

    val currentQuizStep = quizSteps[currentStep]
    val progress = (currentStep + 1) / quizSteps.size.toFloat()

    // Box che contiene tutto + overlay
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
                                    scope.launch { drawerState.close() }
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
                                    navController.navigate(Screen.About.route)
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(
                                selectedScreen = Screen.Quiz,
                                navController = navController
                            )
                        },
                        floatingActionButton = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Freccia indietro - A SINISTRA
                                if (currentStep > 0) {
                                    FloatingActionButton(
                                        onClick = { currentStep-- },
                                        containerColor = Color.Gray.copy(alpha = 0.7f),
                                        contentColor = Color.White,
                                        shape = CircleShape,
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                            contentDescription = "Indietro",
                                            tint = Color.White,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(56.dp))
                                }

                                // Freccia avanti / Check - A DESTRA
                                FloatingActionButton(
                                    onClick = {
                                        val currentSelections = selectedNotes[currentStep] ?: emptyList()
                                        if (currentSelections.isNotEmpty()) {
                                            if (currentStep < quizSteps.size - 1) {
                                                currentStep++
                                            } else {
                                                // Quiz completato!
                                                val profilo = ProfiloOlfattivo(
                                                    noteFloreali = selectedNotes[0] ?: emptyList(),
                                                    noteFruttate = selectedNotes[1] ?: emptyList(),
                                                    noteSpeziate = selectedNotes[2] ?: emptyList(),
                                                    noteGourmand = selectedNotes[3] ?: emptyList(),
                                                    noteLegnose = selectedNotes[4] ?: emptyList()
                                                )
                                                userViewModel.updateProfiloOlfattivo(profilo)
                                                showCompletedOverlay = true
                                            }
                                        } else {
                                            Toast.makeText(context, "Seleziona almeno una nota", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    },
                                    containerColor = QuizAccent.copy(alpha = 0.85f),
                                    contentColor = Color.White,
                                    shape = CircleShape,
                                    modifier = Modifier.size(56.dp)
                                ) {
                                    Icon(
                                        imageVector = if (currentStep == quizSteps.size - 1)
                                            Icons.Default.Check
                                        else
                                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                        contentDescription = if (currentStep == quizSteps.size - 1) "Completa" else "Avanti",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        },
                        floatingActionButtonPosition = FabPosition.Center
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .background(Color.White)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp)
                            ) {
                                Text(
                                    text = currentQuizStep.titolo,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Progress bar
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(QuizProgressBg)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(progress)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(QuizProgressFill)
                                    )
                                }

                                // Contatore selezioni
                                val currentSelections = selectedNotes[currentStep] ?: emptyList()
                                Text(
                                    text = "${currentSelections.size} selezionate",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                                )

                                // Griglia note
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(3),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(18.dp),
                                    contentPadding = PaddingValues(bottom = 80.dp),
                                    modifier = Modifier.fillMaxSize()
                                ) {
                                    items(currentQuizStep.note) { nota ->
                                        val currentList = selectedNotes[currentStep] ?: emptyList()
                                        val isSelected = currentList.contains(nota.nome)

                                        NotaItem(
                                            nota = nota,
                                            isSelected = isSelected,
                                            onClick = {
                                                val updatedList = if (isSelected) {
                                                    currentList - nota.nome
                                                } else {
                                                    currentList + nota.nome
                                                }
                                                selectedNotes = selectedNotes.toMutableMap().apply {
                                                    put(currentStep, updatedList)
                                                }
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

        // Overlay Quiz Completato
        QuizCompletedOverlay(
            visible = showCompletedOverlay,
            onDismiss = {
                showCompletedOverlay = false
                navController.navigate(Screen.Recommended.route) {
                    popUpTo(Screen.Quiz.route) { inclusive = true }
                }
            }
        )
    }
}

/**
 * Overlay compatto per quiz completato
 * Stile card come gli altri overlay
 * Scompare automaticamente dopo 2 secondi
 */
@Composable
fun QuizCompletedOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    // Auto-dismiss dopo 2 secondi
    LaunchedEffect(visible) {
        if (visible) {
            delay(2000)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(200)) + scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(200)
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(200)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            // Card compatta stile badge
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
                    // Cerchio con check
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SecondaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completato",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Quiz completato!",
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
fun NotaItem(
    nota: NotaOlfattiva,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .then(
                        if (isSelected) {
                            Modifier.border(
                                width = 3.dp,
                                color = QuizAccent,
                                shape = CircleShape
                            )
                        } else {
                            Modifier
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = nota.imageRes),
                    contentDescription = nota.nome,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(QuizAccent)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selezionato",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = nota.nome,
            fontSize = 12.sp,
            color = if (isSelected) QuizAccent else Color.Black,
            fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 14.sp
        )
    }
}