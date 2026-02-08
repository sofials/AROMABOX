package com.example.aromabox.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.LogoutConfirmationOverlay
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

private val PageBackground = Color(0xFFF2F2F2)
private val TextPrimary = Color(0xFF222222)
private val TextSecondary = Color(0xFF737083)
private val DividerColor = Color(0xFF737083)
private val AnswerBackground = Color.White
private val AnswerBorderColor = Color(0xFFC2C1C8)

// Data class per le FAQ
data class FaqItem(
    val question: String,
    val answer: String
)

// Lista delle FAQ
val faqList = listOf(
    FaqItem(
        question = "Che cos'è AromaBox?",
        answer = "AromaBox è un’app collegata a una rete di distributori automatici di profumi.\n" +
                "Ti permette di acquistare campioncini direttamente dal tuo smartphone in modo semplice, sicuro e veloce. Con AromaBox puoi scegliere la fragranza che preferisci, effettuare il pagamento tramite app e ritirare il prodotto quando vuoi presso un distributore abilitato."
    ),
    FaqItem(
        question = "Cosa devo fare?",
        answer = "Dopo aver scaricato l’app AromaBox, puoi acquistare il tuo profumo preferito caricando il tuo borsellino virtuale.\n" +
                "Una volta completato l’acquisto, riceverai un codice PIN personale che ti servirà per ritirare il prodotto."
    ),
    FaqItem(
        question = "Come avvengono gli acquisti?",
        answer = "Gli acquisti avvengono tramite il credito presente sul tuo borsellino virtuale.\n" +
                "Puoi acquistare il prodotto ovunque ti trovi, anche senza essere davanti al distributore.\n" +
                "Quando sei davanti a un distributore AromaBox, inserisci il PIN nell’app:\n" +
                "al riconoscimento del codice, il tuo telefono emetterà un suono e il prodotto verrà erogato automaticamente."
    ),
    FaqItem(
        question = "Come posso ricaricare il borsellino?",
        answer = "Puoi ricaricare il tuo borsellino virtuale utilizzando PayPal, Satispay o carta di credito.\n" +
                "Il credito verrà aggiornato in tempo reale e potrai usarlo subito per i tuoi acquisti."
    ),
    FaqItem(
        question = "Quanto costa usare AromaBox?",
        answer = "Usare AromaBox è totalmente gratuito.\n" +
                "Pagherai solo il costo dei prodotti acquistati, senza commissioni aggiuntive."
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // Stato per overlay conferma logout
    var showLogoutConfirmation by remember { mutableStateOf(false) }

    // Stato per tracciare quale FAQ è espansa (-1 = nessuna)
    var expandedFaqIndex by remember { mutableStateOf(-1) }

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
                                    scope.launch { drawerState.close() }
                                    // Già su Info
                                },
                                onContattiClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Screen.Contatti.route)
                                    }
                                },
                                onDisconnessioneClick = {
                                    scope.launch { drawerState.close() }
                                    showLogoutConfirmation = true
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
                                onMenuClick = {
                                    scope.launch { drawerState.open() }
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(
                                selectedScreen = null,  // ✅ null perché è schermata secondaria
                                navController = navController
                            )
                        },
                        containerColor = PageBackground
                    ) { paddingValues ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues)
                                .verticalScroll(rememberScrollState())
                        ) {
                            // Titolo
                            Text(
                                text = "Info e domande frequenti",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 0.5.sp,
                                lineHeight = 32.5.sp,
                                modifier = Modifier.padding(start = 32.dp, top = 24.dp, bottom = 16.dp)
                            )

                            // Lista FAQ
                            faqList.forEachIndexed { index, faq ->
                                FaqItemRow(
                                    faq = faq,
                                    isExpanded = expandedFaqIndex == index,
                                    onClick = {
                                        expandedFaqIndex = if (expandedFaqIndex == index) -1 else index
                                    }
                                )
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }

        // Overlay conferma logout
        LogoutConfirmationOverlay(
            visible = showLogoutConfirmation,
            onConfirm = {
                showLogoutConfirmation = false
                userViewModel.logout()
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = {
                showLogoutConfirmation = false
            }
        )
    }
}

@Composable
private fun FaqItemRow(
    faq: FaqItem,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Riga domanda
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .background(PageBackground)
                .clickable(onClick = onClick)
                .padding(horizontal = 29.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = faq.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = TextPrimary,
                letterSpacing = 0.5.sp,
                lineHeight = 23.sp,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Chiudi" else "Espandi",
                tint = TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }

        // Divider
        HorizontalDivider(
            thickness = 0.5.dp,
            color = DividerColor
        )

        // Risposta (visibile solo se espansa)
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AnswerBackground)
                        .padding(horizontal = 32.dp, vertical = 11.dp)
                ) {
                    Text(
                        text = faq.answer,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary,
                        letterSpacing = 0.5.sp,
                        lineHeight = 23.sp
                    )
                }

                // Divider sotto la risposta
                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = AnswerBorderColor
                )
            }
        }
    }
}