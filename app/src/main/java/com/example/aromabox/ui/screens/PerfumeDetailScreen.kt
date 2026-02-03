package com.example.aromabox.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.data.model.Distributor
import com.example.aromabox.data.model.Perfume
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.DistributorViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import java.util.Locale

// Colori dal Figma
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val BackgroundColor = Color(0xFFF2F2F2)
private val TextPrimary = Color(0xFF1E293B)
private val TextSecondary = Color(0xFF6B7280)
private val TextGray = Color(0xFF374151)
private val NeutralColor = Color(0xFF737083)
private val SuccessGreen = Color(0xFF22C55E)
private val ErrorRed = Color(0xFFEF4444)

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
    catalogViewModel: CatalogViewModel,
    distributorViewModel: DistributorViewModel
) {
    val context = LocalContext.current
    val allPerfumes by catalogViewModel.perfumes.collectAsState()
    val currentUser by userViewModel.currentUser.collectAsState()
    val distributors by distributorViewModel.distributors.collectAsState()

    val perfume = allPerfumes.find { it.id == perfumeId }
    val isFavorite = currentUser?.preferiti?.contains(perfumeId) ?: false

    // Stato per la selezione del distributore
    var selectedDistributorId by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessOverlay by remember { mutableStateOf(false) }
    var generatedPin by remember { mutableStateOf("") }

    // Distributori attivi che hanno questo profumo disponibile
    val availableDistributors = distributors.filter { distributor ->
        distributor.attivo && distributor.getDisponibilita(perfumeId) > 0
    }

    // Trova il distributore selezionato
    val selectedDistributor = distributors.find { it.id == selectedDistributorId }

    if (perfume == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = PrimaryColor)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                DetailTopBar(
                    onBackClick = { navController.popBackStack() },
                    onMenuClick = { /* TODO: Menu */ }
                )
            },
            bottomBar = {
                DetailBottomSection(
                    navController = navController,
                    isEnabled = selectedDistributorId != null && !isProcessing,
                    isProcessing = isProcessing,
                    onAcquistaClick = {
                        val distributorId = selectedDistributorId
                        val distributor = selectedDistributor
                        if (distributorId != null && distributor != null) {
                            val userWallet = currentUser?.wallet ?: 0.0
                            if (userWallet >= perfume.prezzo) {
                                isProcessing = true

                                // 1. Crea l'ordine nel database
                                userViewModel.createOrder(
                                    perfume = perfume,
                                    distributorId = distributorId,
                                    distributorName = distributor.nome,
                                    onSuccess = { pin ->
                                        generatedPin = pin

                                        // 2. Scala l'inventario
                                        distributorViewModel.decrementInventory(
                                            distributorId = distributorId,
                                            perfumeId = perfumeId,
                                            onSuccess = {
                                                // 3. Scala il wallet
                                                userViewModel.rechargeWallet(
                                                    amount = -perfume.prezzo,
                                                    onSuccess = {
                                                        isProcessing = false
                                                        showSuccessOverlay = true
                                                    },
                                                    onError = { error ->
                                                        isProcessing = false
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                            },
                                            onError = { error ->
                                                isProcessing = false
                                                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    },
                                    onError = { error ->
                                        isProcessing = false
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Saldo insufficiente. Ricarica il wallet.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
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

                    // SEZIONE: Selezione distributore
                    Text(
                        text = "SELEZIONA DISTRIBUTORE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (availableDistributors.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(ErrorRed.copy(alpha = 0.1f))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Nessun distributore disponibile per questo prodotto",
                                fontSize = 14.sp,
                                color = ErrorRed,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        availableDistributors.forEach { distributor ->
                            DistributorSelectionRow(
                                distributor = distributor,
                                disponibilita = distributor.getDisponibilita(perfumeId),
                                isSelected = selectedDistributorId == distributor.id,
                                onClick = {
                                    selectedDistributorId = if (selectedDistributorId == distributor.id) {
                                        null
                                    } else {
                                        distributor.id
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

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

                    PiramideNotaRow(
                        label = "Note di testa",
                        note = perfume.noteOlfattive.noteDiTesta.joinToString(", ") {
                            it.replaceFirstChar { c -> c.uppercase() }
                        },
                        backgroundColor = NotaTestaColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PiramideNotaRow(
                        label = "Note di cuore",
                        note = perfume.noteOlfattive.noteDiCuore.joinToString(", ") {
                            it.replaceFirstChar { c -> c.uppercase() }
                        },
                        backgroundColor = NotaCuoreColor
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PiramideNotaRow(
                        label = "Note di fondo",
                        note = perfume.noteOlfattive.noteDiFondo.joinToString(", ") {
                            it.replaceFirstChar { c -> c.uppercase() }
                        },
                        backgroundColor = NotaFondoColor
                    )

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Overlay successo acquisto
        PurchaseSuccessOverlay(
            visible = showSuccessOverlay,
            perfumeName = perfume.nome,
            pin = generatedPin,
            onDismiss = {
                showSuccessOverlay = false
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun DistributorSelectionRow(
    distributor: Distributor,
    disponibilita: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SecondaryColor.copy(alpha = 0.2f) else Color.White)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) PrimaryColor else Color(0xFFE0E0E0),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Radio button
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isSelected) PrimaryColor else Color.Transparent)
                .border(2.dp, if (isSelected) PrimaryColor else NeutralColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selezionato",
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Info distributore
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = distributor.nome,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = distributor.getIndirizzoCompleto(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = TextSecondary
            )
        }

        // Disponibilità
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$disponibilita",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = if (disponibilita > 2) SuccessGreen else if (disponibilita > 0) Color(0xFFF59E0B) else ErrorRed
            )
            Text(
                text = "disponibili",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun PurchaseSuccessOverlay(
    visible: Boolean,
    perfumeName: String,
    pin: String,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(4000) // Più tempo per vedere il PIN
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Cerchio con check
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(SuccessGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Completato",
                        tint = SuccessGreen,
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Acquisto completato!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = perfumeName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // PIN
                Text(
                    text = "Il tuo PIN:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryColor.copy(alpha = 0.1f))
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = pin,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryColor,
                        letterSpacing = 4.sp
                    )
                }
            }
        }
    }
}

// TopBar con freccia indietro
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
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = NeutralColor,
                    modifier = Modifier.size(24.dp)
                )
            }

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
        Image(
            painter = painterResource(id = perfume.getImageResource()),
            contentDescription = perfume.nome,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentScale = ContentScale.Fit
        )

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
    isEnabled: Boolean,
    isProcessing: Boolean,
    onAcquistaClick: () -> Unit
) {
    Column {
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
                        containerColor = PrimaryColor,
                        disabledContainerColor = PrimaryColor.copy(alpha = 0.4f)
                    ),
                    enabled = isEnabled
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Acquista",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        BottomNavigationBar(
            selectedScreen = Screen.Catalog,
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