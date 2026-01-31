package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import com.example.aromabox.ui.viewmodels.CatalogViewModel

private val PageBackground = Color(0xFFF2F2F2)
private val PrimaryColor = Color(0xFF8378BF)
private val NeutralColor = Color(0xFF737083)
private val TextColor = Color(0xFF374151)

// Lista marche fisse dal Figma
val brandOptions = listOf(
    "Dior",
    "Chanel",
    "Yves Saint Laurent",
    "Giardini di Toscana",
    "Prada",
    "Chloé",
    "Carolina Herrera",
    "Valentino",
    "Jo Malone London",
    "Tom Ford"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBrandScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
) {
    val currentSelected by catalogViewModel.selectedBrands.collectAsState()
    var selectedBrands by remember { mutableStateOf(currentSelected) }

    LaunchedEffect(currentSelected) {
        selectedBrands = currentSelected
    }

    Scaffold(containerColor = PageBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSubHeader(
                title = "MARCA",
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack(Screen.Catalog.route, inclusive = false) }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 24.dp)
            ) {
                items(brandOptions) { brand ->
                    CheckboxOptionRow(
                        text = brand,
                        isSelected = selectedBrands.contains(brand),
                        onClick = {
                            selectedBrands = if (selectedBrands.contains(brand)) {
                                selectedBrands - brand
                            } else {
                                selectedBrands + brand
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = {
                        catalogViewModel.setSelectedBrands(selectedBrands)
                        navController.popBackStack(Screen.Catalog.route, inclusive = false)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeutralColor)
                ) {
                    Text("SALVA", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                }
            }

            BottomNavigationBar(selectedScreen = Screen.Catalog, navController = navController)
        }
    }
}

@Composable
fun CheckboxOptionRow(
    text: String,
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
        // Checkbox custom come nel Figma
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (isSelected) PrimaryColor else PageBackground)
                .border(1.dp, PrimaryColor, RoundedCornerShape(3.dp)),
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

        Text(
            text = text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = TextColor
        )
    }
}