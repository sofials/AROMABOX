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
        answer = "AromaBox è un'app collegata a un omonimo distributore di profumi. Include un sistema di pagamento tramite smartphone sicuro, facile e conveniente. Con AromaBox ti basta scegliere una fragranza e questa verrà erogata dopo il pagamento. Scansiona il QR code, entro pochi secondi il tuo credito sarà trasferito e visualizzato sul distributore."
    ),
    FaqItem(
        question = "Cosa devo fare?",
        answer = "Una volta scaricata l'applicazione AromaBox, potrai erogare il tuo profumo preferito caricando il tuo borsellino elettronico. Bastano pochi secondi per goderti una coccola profumata!"
    ),
    FaqItem(
        question = "Come avvengono gli acquisti?",
        answer = "Gli acquisti avvengono tramite il credito presente sul tuo borsellino virtuale. Posizionati di fronte al distributore automatico abilitato, scansiona il QR ed effettua l'acquisto tramite app!"
    ),
    FaqItem(
        question = "Come posso ricaricare il borsellino?",
        answer = "Puoi ricaricare il tuo borsellino elettronico come più preferisci, utilizzando Paypal, Satispay o inserendo direttamente la tua carta di credito. Una volta acquistato, il sistema ti addebiterà automaticamente l'importo effettivo dell'erogazione"
    ),
    FaqItem(
        question = "Quanto costa usare AromaBox?",
        answer = "Pagare con AromaBox è totalmente GRATUITO. Ti saranno addebitati solo gli importi dei tuoi acquisti, senza alcuna commissione aggiuntiva."
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
                                selectedScreen = Screen.Home,
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