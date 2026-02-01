package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel

// Colori dal Figma
private val PageBackground = Color(0xFFF2F2F2)
private val HeaderBackground = Color(0xFFC4B9FF).copy(alpha = 0.40f)
private val NeutralColor = Color(0xFF737083)
private val TextColor = Color(0xFF1E1E1E)
private val DividerColor = Color(0xFF737083)
private val ActiveFilterColor = Color(0xFF8378BF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
) {
    val productCount = catalogViewModel.getFilteredPerfumes().size
    val selectedDistributor by catalogViewModel.selectedDistributor.collectAsState()

    Scaffold(
        containerColor = PageBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            FilterHeader(
                onClearFilters = { catalogViewModel.clearFilters() },
                onClose = { navController.popBackStack() }
            )

            // Lista filtri
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // ✅ NUOVO: Filtro Distributore (primo nella lista)
                FilterOptionRow(
                    title = "DISTRIBUTORE",
                    subtitle = selectedDistributor?.nome,
                    onClick = { navController.navigate(Screen.FilterDistributor.route) }
                )

                FilterOptionRow(
                    title = "ORDINA PER",
                    onClick = { navController.navigate(Screen.FilterSort.route) }
                )

                FilterOptionRow(
                    title = "PREZZO",
                    onClick = { navController.navigate(Screen.FilterPrice.route) }
                )

                FilterOptionRow(
                    title = "MARCA",
                    onClick = { navController.navigate(Screen.FilterBrand.route) }
                )

                FilterOptionRow(
                    title = "GENERE",
                    onClick = { navController.navigate(Screen.FilterGender.route) }
                )

                FilterOptionRow(
                    title = "FAMIGLIA OLFATTIVA",
                    onClick = { navController.navigate(Screen.FilterFamily.route) }
                )

                FilterOptionRow(
                    title = "NOTE AROMATICHE",
                    onClick = { navController.navigate(Screen.FilterNotes.route) }
                )
            }

            // Bottone "Mostra X prodotti"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeutralColor)
                ) {
                    Text(
                        text = "MOSTRA $productCount PRODOTTI",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            BottomNavigationBar(
                selectedScreen = Screen.Catalog,
                navController = navController
            )
        }
    }
}

@Composable
fun FilterHeader(
    onClearFilters: () -> Unit,
    onClose: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = HeaderBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = NeutralColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancella filtri",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextColor,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable { onClearFilters() }
                )

                Text(
                    text = "Filtri",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeutralColor
                )
            }
        }
    }
}

@Composable
fun FilterOptionRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            color = PageBackground
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 35.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeutralColor
                    )

                    // ✅ Mostra il valore selezionato se presente
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ActiveFilterColor
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Apri",
                    tint = if (subtitle != null) ActiveFilterColor else NeutralColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = DividerColor)
    }
}