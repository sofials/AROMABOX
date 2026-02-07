package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.Alignment
import com.example.aromabox.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.aromabox.data.model.Distributor
import com.example.aromabox.ui.components.AppDrawerContent
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.DistributorViewModel
import com.example.aromabox.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch
// MapLibre imports - versione 11.x
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

private val PageBackground = Color(0xFFF2F2F2)
private val CardBorderColor = Color(0xFF818181)
private val TextGray = Color(0xFF6B7280)
private val NeutralColor = Color(0xFF737083)
private val PrimaryColor = Color(0xFF8378BF)
private val SecondaryColor = Color(0xFFC4B9FF)

// ⚠️ INSERISCI LA TUA API KEY QUI
private const val MAPTILER_API_KEY = "gzv73kTZ1aADn58Y8tsE"

// URL della tua mappa personalizzata MapTiler
private const val MAPTILER_STYLE_URL = "https://api.maptiler.com/maps/019c2b56-0d0d-7e29-9021-e57fb956cb20/style.json?key=$MAPTILER_API_KEY"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributoriScreen(
    navController: NavController,
    distributorViewModel: DistributorViewModel,
    catalogViewModel: CatalogViewModel,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val distributors by distributorViewModel.distributors.collectAsState()
    val isLoading by distributorViewModel.isLoading.collectAsState()
    val scope = rememberCoroutineScope()

    // Stato del drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // ✅ FIX: Inizializza MapLibre SINCRONICAMENTE prima di tutto
    val isMapLibreInitialized = remember {
        try {
            MapLibre.getInstance(context)
            true
        } catch (e: Exception) {
            false
        }
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
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Screen.Contatti.route)
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
                                navController.navigate(Screen.About.route)
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            selectedScreen = Screen.Distributori,
                            navController = navController
                        )
                    },
                    containerColor = PageBackground
                ) { paddingValues ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        // Titolo
                        Text(
                            text = "Trova gli AromaBox vicino a te!",
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 16.dp)
                        )

                        // Mappa MapTiler - solo se MapLibre è inizializzato
                        if (isMapLibreInitialized) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, SecondaryColor, RoundedCornerShape(12.dp))
                            ) {
                                val activeDistributors = distributors.filter { it.attivo }

                                MapTilerMapView(
                                    modifier = Modifier.fillMaxSize(),
                                    distributors = activeDistributors
                                )
                            }
                        } else {
                            // Fallback se MapLibre non è inizializzato
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp)
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.LightGray),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Mappa non disponibile", color = Color.Gray)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Sezione lista distributori
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .verticalScroll(rememberScrollState())
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = "TUTTI I DISTRIBUTORI AROMABOX (${distributors.size})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = CardBorderColor,
                                letterSpacing = 0.5.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            if (isLoading) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = PrimaryColor)
                                }
                            } else {
                                distributors.forEach { distributor ->
                                    DistributoreCard(
                                        distributor = distributor,
                                        onClick = {
                                            if (distributor.attivo) {
                                                catalogViewModel.setSelectedDistributor(distributor)
                                                navController.navigate(Screen.Catalog.route) {
                                                    popUpTo(Screen.Home.route) { saveState = true }
                                                    launchSingleTop = true
                                                }
                                            }
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MapTilerMapView(
    modifier: Modifier = Modifier,
    distributors: List<Distributor>
) {
    val context = LocalContext.current
    val defaultPosition = LatLng(45.0628, 7.6627)

    // ✅ Stato per tenere riferimento alla mappa
    var mapInstance by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }

    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                MapView(ctx).apply {
                    getMapAsync { map ->
                        mapInstance = map  // ✅ Salva il riferimento

                        map.setStyle(Style.Builder().fromUri(MAPTILER_STYLE_URL)) { style ->
                            // Imposta posizione camera
                            map.cameraPosition = CameraPosition.Builder()
                                .target(defaultPosition)
                                .zoom(15.0)
                                .build()

                            // Crea icona custom con colore Primary #8378BF
                            val iconFactory = org.maplibre.android.annotations.IconFactory.getInstance(ctx)
                            val bitmap = BitmapFactory.decodeResource(ctx.resources, R.drawable.ic_marker_purple)
                            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 80, 80, false)
                            val customIcon = iconFactory.fromBitmap(scaledBitmap)

                            // Aggiungi marker per ogni distributore attivo
                            distributors.forEach { distributor ->
                                @Suppress("DEPRECATION")
                                map.addMarker(
                                    org.maplibre.android.annotations.MarkerOptions()
                                        .position(LatLng(distributor.latitudine, distributor.longitudine))
                                        .title(distributor.nome)
                                        .snippet(distributor.getIndirizzoCompleto())
                                        .icon(customIcon)
                                )
                            }

                            // Se non ci sono distributori, aggiungi comunque un marker sul Politecnico
                            if (distributors.isEmpty()) {
                                @Suppress("DEPRECATION")
                                map.addMarker(
                                    org.maplibre.android.annotations.MarkerOptions()
                                        .position(defaultPosition)
                                        .title("Politecnico di Torino")
                                        .snippet("AromaBox disponibile qui!")
                                        .icon(customIcon)
                                )
                            }
                        }

                        // Abilita controlli
                        map.uiSettings.isZoomGesturesEnabled = true
                        map.uiSettings.isScrollGesturesEnabled = true
                        map.uiSettings.isRotateGesturesEnabled = false
                        map.uiSettings.isTiltGesturesEnabled = false
                    }
                }
            },
            onRelease = { view ->
                view.onDestroy()
            }
        )

        // ✅ FAB per ricentrare la mappa sul Politecnico
        FloatingActionButton(
            onClick = {
                mapInstance?.animateCamera(
                    org.maplibre.android.camera.CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(defaultPosition)
                            .zoom(15.0)
                            .build()
                    ),
                    1000  // Durata animazione in millisecondi
                )
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = PrimaryColor,
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = "Centra mappa sul Politecnico"
            )
        }
    }
}

@Composable
fun DistributoreCard(
    distributor: Distributor,
    onClick: () -> Unit
) {
    val isClickable = distributor.attivo

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .then(
                if (isClickable) Modifier.clickable(onClick = onClick) else Modifier
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isClickable) Color.White else Color.White.copy(alpha = 0.7f)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(
                if (isClickable) SecondaryColor else CardBorderColor.copy(alpha = 0.5f)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(if (isClickable) SecondaryColor.copy(alpha = 0.3f) else PageBackground)
                    .border(
                        width = if (isClickable) 2.dp else 0.5.dp,
                        color = if (isClickable) PrimaryColor else NeutralColor.copy(alpha = 0.5f),
                        shape = CircleShape
                    )
            ) {
                if (isClickable) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(PrimaryColor)
                            .align(Alignment.Center)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = if (isClickable) Color.Black else TextGray.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(distributor.nome)
                        }
                        withStyle(
                            style = SpanStyle(
                                color = if (isClickable) TextGray else TextGray.copy(alpha = 0.5f),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(" - ${distributor.getIndirizzoCompleto()}")
                        }
                    },
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isClickable) "Tocca per vedere i prodotti disponibili →" else "Prossimamente disponibile",
                    fontSize = 11.sp,
                    color = if (isClickable) PrimaryColor else TextGray.copy(alpha = 0.5f),
                    fontWeight = if (isClickable) FontWeight.Medium else FontWeight.Normal
                )
            }
        }
    }
}