package com.example.aromabox.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.aromabox.R
import com.example.aromabox.data.model.Order
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.Primary
import com.example.aromabox.ui.theme.Secondary
import com.example.aromabox.ui.viewmodels.StoricoTab
import com.example.aromabox.ui.viewmodels.StoricoViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

private val BackgroundColor = Color(0xFFF2F2F2)
private val CardBackgroundColor = Color(0xFFF7F6FA)
private val TextPrimary = Color(0xFF1E1E1E)
private val TextSecondary = Color(0xFF737083)
private val TextBrand = Color(0xFF222222)
private val PrimaryColor = Color(0xFF8378BF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoricoScreen(
    navController: NavController,
    userViewModel: UserViewModel = viewModel(),
    storicoViewModel: StoricoViewModel = viewModel()
) {
    val orders by storicoViewModel.orders.collectAsState()
    val isLoading by storicoViewModel.isLoading.collectAsState()
    val selectedTab by storicoViewModel.selectedTab.collectAsState()

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val filteredOrders = remember(orders, selectedTab) {
        when (selectedTab) {
            StoricoTab.DA_RITIRARE -> orders.filter { !it.ritirato }
            StoricoTab.RITIRATI -> orders.filter { it.ritirato }
        }
    }

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
                            onInfoClick = { scope.launch { drawerState.close() } },
                            onContattiClick = { scope.launch { drawerState.close() } },
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
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            selectedScreen = Screen.Storico,
                            navController = navController
                        )
                    },
                    containerColor = BackgroundColor
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        Text(
                            text = "Ecco lo storico dei tuoi acquisti!",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )

                        TabPills(
                            selectedTab = selectedTab,
                            countDaRitirare = orders.count { !it.ritirato },
                            countRitirati = orders.count { it.ritirato },
                            onTabSelected = { storicoViewModel.selectTab(it) },
                            modifier = Modifier.padding(horizontal = 17.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isLoading) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Primary)
                            }
                        } else if (filteredOrders.isEmpty()) {
                            EmptyStateMessage(selectedTab = selectedTab)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(horizontal = 17.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(
                                    items = filteredOrders,
                                    key = { it.orderId }
                                ) { order ->
                                    OrderCard(
                                        order = order,
                                        showPin = selectedTab == StoricoTab.DA_RITIRARE,
                                        onClick = {
                                            navController.navigate(Screen.PerfumeDetail.createRoute(order.perfumeId))
                                        }
                                    )
                                }

                                item { Spacer(modifier = Modifier.height(16.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TabPills(
    selectedTab: StoricoTab,
    countDaRitirare: Int,
    countRitirati: Int,
    onTabSelected: (StoricoTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TabPill(
            text = "Da ritirare",
            count = countDaRitirare,
            isSelected = selectedTab == StoricoTab.DA_RITIRARE,
            onClick = { onTabSelected(StoricoTab.DA_RITIRARE) },
            modifier = Modifier.weight(1f)
        )
        TabPill(
            text = "Ritirati",
            count = countRitirati,
            isSelected = selectedTab == StoricoTab.RITIRATI,
            onClick = { onTabSelected(StoricoTab.RITIRATI) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabPill(
    text: String,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) Primary else Color.White
    val textColor = if (isSelected) Color.White else TextSecondary
    val borderColor = if (isSelected) Primary else Color(0xFFE0E0E0)

    Surface(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(22.dp))
            .border(1.dp, borderColor, RoundedCornerShape(22.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = textColor
            )

            if (count > 0) {
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White.copy(alpha = 0.3f) else Secondary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Primary
                    )
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: Order,
    showPin: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(CardBackgroundColor)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(CardBackgroundColor)
                    .border(0.2.dp, TextSecondary)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(getDrawableResource(order.perfumeImageUrl))
                        .crossfade(true)
                        .build(),
                    contentDescription = order.perfumeName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = order.perfumeBrand.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextBrand,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.perfumeName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Column {
                    Text(
                        text = order.getFormattedDate(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = order.getFormattedPrice(),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
            }
        }

        if (showPin && order.pin.isNotEmpty()) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 12.dp),
                thickness = 0.5.dp,
                color = TextSecondary.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "PIN:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryColor.copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = order.pin,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor,
                            letterSpacing = 2.sp
                        )
                    }
                }

                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(order.pin))
                        Toast.makeText(context, "PIN copiato!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ContentCopy,
                        contentDescription = "Copia PIN",
                        tint = PrimaryColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(selectedTab: StoricoTab) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = if (selectedTab == StoricoTab.DA_RITIRARE)
                    Icons.Outlined.ShoppingBag else Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (selectedTab == StoricoTab.DA_RITIRARE)
                    "Nessun ordine da ritirare" else "Nessun ordine ritirato",
                fontSize = 16.sp,
                color = TextSecondary
            )
            if (selectedTab == StoricoTab.DA_RITIRARE) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Esplora il catalogo per fare il tuo primo acquisto!",
                    fontSize = 14.sp,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }
        }
    }
}

private fun getDrawableResource(imageUrl: String): Int {
    return when (imageUrl.lowercase().trim()) {
        "perfume_chanel_no5" -> R.drawable.perfume_chanel_no5
        "perfume_dior_sauvage" -> R.drawable.perfume_dior_sauvage
        "perfume_armani_si" -> R.drawable.perfume_armani_si
        else -> R.drawable.logo
    }
}