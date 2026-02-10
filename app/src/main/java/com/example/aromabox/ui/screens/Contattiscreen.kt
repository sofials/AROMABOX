package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.components.LogoutConfirmationOverlay
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.ReportViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

private val PageBackground = Color(0xFFF2F2F2)
private val CardBackground = Color(0xFFFAFAFA)
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)
private val AccentColor = Color(0xFF605882)
private val TextPrimary = Color(0xFF222222)
private val TextSecondary = Color(0xFF737083)
private val ButtonTextColor = Color(0xFFF2F2F2)
private val ErrorRed = Color(0xFFE53935)

private data class ReportType(
    val key: String,
    val label: String
)

private val reportTypes = listOf(
    ReportType("bug", "Bug"),
    ReportType("suggestion", "Suggerimento"),
    ReportType("other", "Altro")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContattiScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    reportViewModel: ReportViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var showLogoutConfirmation by remember { mutableStateOf(false) }

    val selectedType by reportViewModel.selectedType.collectAsState()
    val message by reportViewModel.message.collectAsState()
    val isLoading by reportViewModel.isLoading.collectAsState()
    val sendResult by reportViewModel.sendResult.collectAsState()
    val errorMessage by reportViewModel.errorMessage.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                                        navController.navigate(Screen.Info.route)
                                    }
                                },
                                onContattiClick = {
                                    scope.launch { drawerState.close() }
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
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onLogoClick = {
                                    navController.navigate(Screen.About.route)
                                }
                            )
                        },
                        bottomBar = {
                            BottomNavigationBar(
                                selectedScreen = null,
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
                            Text(
                                text = "Contatti",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                letterSpacing = 0.5.sp,
                                lineHeight = 32.5.sp,
                                modifier = Modifier.padding(start = 46.dp, top = 24.dp, bottom = 24.dp)
                            )

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 18.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBackground),
                                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(25.dp)
                                ) {
                                    Text(
                                        text = "Qualche problema?",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = TextPrimary,
                                        letterSpacing = 0.5.sp,
                                        lineHeight = 23.sp
                                    )

                                    Text(
                                        text = "Inviaci una segnalazione e ti risponderemo il prima possibile.",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Normal,
                                        color = TextSecondary,
                                        lineHeight = 18.sp,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                                    )

                                    Text(
                                        text = "Tipo di segnalazione",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        reportTypes.forEach { type ->
                                            val isSelected = selectedType == type.key
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(44.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(
                                                        if (isSelected) PrimaryColor
                                                        else Color.White
                                                    )
                                                    .border(
                                                        width = if (isSelected) 0.dp else 1.dp,
                                                        color = if (isSelected) Color.Transparent else SecondaryColor,
                                                        shape = RoundedCornerShape(10.dp)
                                                    )
                                                    .clickable {
                                                        reportViewModel.onTypeSelected(type.key)
                                                    },
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = type.label,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isSelected) Color.White else AccentColor
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(18.dp))

                                    Text(
                                        text = "Descrivi il problema",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = TextSecondary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    OutlinedTextField(
                                        value = message,
                                        onValueChange = { reportViewModel.onMessageChanged(it) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(140.dp),
                                        placeholder = {
                                            Text(
                                                text = "Descrivi la tua segnalazione in dettaglio...",
                                                fontSize = 14.sp,
                                                color = TextSecondary.copy(alpha = 0.6f)
                                            )
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = PrimaryColor,
                                            unfocusedBorderColor = SecondaryColor.copy(alpha = 0.5f),
                                            cursorColor = PrimaryColor,
                                            focusedContainerColor = Color.White,
                                            unfocusedContainerColor = Color.White
                                        ),
                                        textStyle = LocalTextStyle.current.copy(
                                            fontSize = 14.sp,
                                            color = TextPrimary
                                        ),
                                        maxLines = 6,
                                        enabled = !isLoading
                                    )

                                    Text(
                                        text = "${message.length} / 500",
                                        fontSize = 11.sp,
                                        color = if (message.length > 500) ErrorRed else TextSecondary,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp),
                                        textAlign = TextAlign.End
                                    )

                                    if (errorMessage != null) {
                                        Text(
                                            text = errorMessage!!,
                                            fontSize = 12.sp,
                                            color = ErrorRed,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(top = 4.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = { reportViewModel.sendReport() },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = PrimaryColor,
                                            disabledContainerColor = PrimaryColor.copy(alpha = 0.5f)
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 4.dp
                                        ),
                                        enabled = !isLoading && message.length <= 500
                                    ) {
                                        if (isLoading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = Color.White,
                                                strokeWidth = 2.5.dp
                                            )
                                        } else {
                                            Text(
                                                text = "INVIA SEGNALAZIONE",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = ButtonTextColor,
                                                textAlign = TextAlign.Center,
                                                lineHeight = 18.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
        }

        if (sendResult != null) {
            ReportResultOverlay(
                isSuccess = sendResult == true,
                onDismiss = { reportViewModel.clearResult() }
            )
        }

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
private fun ReportResultOverlay(
    isSuccess: Boolean,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .width(320.dp)
                .clickable(enabled = false, onClick = {}),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Cerchio con check/X nei colori dell'app
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(if (isSuccess) SecondaryColor else ErrorRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSuccess) "✓" else "✕",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuccess) AccentColor else ErrorRed
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isSuccess) "Segnalazione inviata!" else "Errore nell'invio",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isSuccess)
                        "Grazie per il tuo feedback! Ti risponderemo il prima possibile."
                    else
                        "Si è verificato un errore. Riprova più tardi.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSuccess) PrimaryColor else ErrorRed
                    )
                ) {
                    Text(
                        text = if (isSuccess) "CHIUDI" else "RIPROVA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}