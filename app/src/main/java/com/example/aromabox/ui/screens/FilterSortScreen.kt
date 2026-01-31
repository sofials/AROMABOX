package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.navigation.Screen

// Colori dal Figma
private val PageBackground = Color(0xFFF2F2F2)
private val PrimaryColor = Color(0xFF8378BF)
private val NeutralColor = Color(0xFF737083)
private val TextColor = Color(0xFF374151)
private val DividerColor = Color(0xFF737083)

enum class SortOption(val displayName: String) {
    PREZZO_CRESCENTE("Prezzo crescente"),
    PREZZO_DECRESCENTE("Prezzo decrescente")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSortScreen(
    navController: NavController,
    currentSort: SortOption? = null,
    onSortSelected: (SortOption) -> Unit = {}
) {
    var selectedSort by remember { mutableStateOf(currentSort) }

    Scaffold(
        containerColor = PageBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            SortHeader(
                onBackClick = { navController.popBackStack() },
                onCloseClick = {
                    // Torna al catalogo (chiude tutti i filtri)
                    navController.popBackStack(Screen.Catalog.route, inclusive = false)
                }
            )

            // Opzioni di ordinamento
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 32.dp)
            ) {
                SortOption.entries.forEach { option ->
                    SortOptionRow(
                        option = option,
                        isSelected = selectedSort == option,
                        onClick = { selectedSort = option }
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // Bottone "Salva"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = {
                        selectedSort?.let { onSortSelected(it) }
                        navController.popBackStack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeutralColor
                    )
                ) {
                    Text(
                        text = "SALVA",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }

            // Bottom Navigation Bar
            BottomNavigationBar(
                selectedScreen = Screen.Catalog,
                navController = navController
            )
        }
    }
}

@Composable
fun SortHeader(
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PageBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
        ) {
            // Riga con freccia indietro, titolo, X
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Freccia indietro
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Indietro",
                        tint = NeutralColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Titolo centrato
                Text(
                    text = "ORDINA PER",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = NeutralColor
                )

                // X per chiudere
                IconButton(onClick = onCloseClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = NeutralColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Divider
            HorizontalDivider(
                thickness = 0.5.dp,
                color = DividerColor
            )
        }
    }
}

@Composable
fun SortOptionRow(
    option: SortOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 33.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Radio button custom
        Box(
            modifier = Modifier
                .size(17.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) PrimaryColor else Color.Transparent
                )
                .then(
                    if (!isSelected) {
                        Modifier.background(Color.Transparent)
                    } else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                // Cerchio interno bianco
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
            } else {
                // Bordo quando non selezionato
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ) {
                    // Disegna solo il bordo
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        shape = CircleShape,
                        color = Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryColor)
                    ) {}
                }
            }
        }

        // Testo opzione
        Text(
            text = option.displayName,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = TextColor
        )
    }
}