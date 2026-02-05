package com.example.aromabox.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

private val PageBackground = Color(0xFFF2F2F2)
private val CardHeaderBg = Color(0xFF737083)
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val CardBg = Color.White
private val TextSecondary = Color(0xFFFFFFFF).copy(alpha = 0.72f)
private val CheckBgColor = Color(0xFFEDE9FF)
private val CheckIconColor = Color(0xFF8378BF)

// Opzioni importo
val rechargeAmounts = listOf(5.0, 10.0, 20.0)

// Opzioni metodo di pagamento
enum class PaymentMethod(val displayName: String) {
    PAYPAL("PayPal"),
    GOOGLE_PAY("Google Pay"),
    APPLE_PAY("Apple Pay"),
    CARD("Carta")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()
    val scope = rememberCoroutineScope()

    // Stato del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var selectedAmount by remember { mutableStateOf<Double?>(null) }
    var selectedPayment by remember { mutableStateOf<PaymentMethod?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var showSuccessOverlay by remember { mutableStateOf(false) }

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
                                        // TODO: Naviga a Info
                                    }
                                },
                                onContattiClick = {
                                    scope.launch {
                                        drawerState.close()
                                        // TODO: Naviga a Contatti
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
                                .padding(horizontal = 17.dp)
                        ) {
                            // Freccia indietro
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Indietro",
                                    tint = Color.Black
                                )
                            }

                            // Card superiore (grigia) - Saldo + Selezione importo
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                                colors = CardDefaults.cardColors(containerColor = CardHeaderBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 36.dp, horizontal = 31.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Saldo disponibile
                                    Text(
                                        text = "SALDO DISPONIBILE",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = TextSecondary,
                                        letterSpacing = 1.sp
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = String.format(Locale.ITALIAN, "%.2f €", currentUser?.wallet ?: 0.0),
                                        fontSize = 33.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )

                                    Spacer(modifier = Modifier.height(32.dp))

                                    // Seleziona importo
                                    Text(
                                        text = "Seleziona l'importo",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = PageBackground
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Bottoni importo
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceEvenly
                                    ) {
                                        rechargeAmounts.forEach { amount ->
                                            AmountButton(
                                                amount = amount,
                                                isSelected = selectedAmount == amount,
                                                onClick = { selectedAmount = amount }
                                            )
                                        }
                                    }
                                }
                            }

                            // Card inferiore (bianca) - Metodi di pagamento
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = 20.dp, bottomEnd = 20.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 31.dp, horizontal = 41.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Ricarica con",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = CardHeaderBg
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Prima riga: PayPal e Google Pay
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        PaymentMethodButton(
                                            method = PaymentMethod.PAYPAL,
                                            isSelected = selectedPayment == PaymentMethod.PAYPAL,
                                            onClick = { selectedPayment = PaymentMethod.PAYPAL },
                                            modifier = Modifier.weight(1f)
                                        )
                                        PaymentMethodButton(
                                            method = PaymentMethod.GOOGLE_PAY,
                                            isSelected = selectedPayment == PaymentMethod.GOOGLE_PAY,
                                            onClick = { selectedPayment = PaymentMethod.GOOGLE_PAY },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Apple Pay
                                    PaymentMethodButton(
                                        method = PaymentMethod.APPLE_PAY,
                                        isSelected = selectedPayment == PaymentMethod.APPLE_PAY,
                                        onClick = { selectedPayment = PaymentMethod.APPLE_PAY },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    // Carta
                                    PaymentMethodButton(
                                        method = PaymentMethod.CARD,
                                        isSelected = selectedPayment == PaymentMethod.CARD,
                                        onClick = { selectedPayment = PaymentMethod.CARD },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Spacer(modifier = Modifier.height(40.dp))

                                    // Bottone Prosegui
                                    Button(
                                        onClick = {
                                            if (selectedAmount != null && selectedPayment != null) {
                                                isProcessing = true
                                                userViewModel.rechargeWallet(
                                                    amount = selectedAmount!!,
                                                    onSuccess = {
                                                        isProcessing = false
                                                        showSuccessOverlay = true
                                                    },
                                                    onError = { error ->
                                                        isProcessing = false
                                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                                    }
                                                )
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Seleziona importo e metodo di pagamento",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        },
                                        modifier = Modifier
                                            .width(144.dp)
                                            .height(37.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryColor,
                                            disabledContainerColor = PrimaryColor.copy(alpha = 0.5f)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                                        enabled = !isProcessing
                                    ) {
                                        if (isProcessing) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = "Prosegui",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }

        // Overlay successo
        RechargeSuccessOverlay(
            visible = showSuccessOverlay,
            onDismiss = {
                showSuccessOverlay = false
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Home.route) { inclusive = true }
                }
            }
        )
    }
}

@Composable
fun AmountButton(
    amount: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .then(
                if (isSelected) {
                    Modifier.background(SecondaryColor)
                } else {
                    Modifier.border(1.5.dp, SecondaryColor, RoundedCornerShape(4.dp))
                }
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${amount.toInt()} €",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) Color.White else SecondaryColor
        )
    }
}

@Composable
fun PaymentMethodButton(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(59.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFF4F4F4))
            .then(
                if (isSelected) {
                    Modifier.border(1.5.dp, PrimaryColor, RoundedCornerShape(8.dp))
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = method.displayName,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) PrimaryColor else Color.Gray
        )
    }
}

@Composable
fun RechargeSuccessOverlay(
    visible: Boolean,
    onDismiss: () -> Unit
) {
    LaunchedEffect(visible) {
        if (visible) {
            delay(2000)
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
                // Cerchio esterno con bordo tratteggiato (simulato)
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .border(
                            width = 2.dp,
                            color = SecondaryColor,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Cerchio interno con check
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(CheckBgColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Completato",
                            tint = CheckIconColor,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Pagamento effettuato!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
    }
}