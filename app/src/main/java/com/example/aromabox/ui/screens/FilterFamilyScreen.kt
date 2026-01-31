package com.example.aromabox.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel

private val PageBackground = Color(0xFFF2F2F2)
private val NeutralColor = Color(0xFF737083)

// Opzioni famiglia olfattiva dal Figma
val familyOptions = listOf(
    "Aromatica",
    "Floreale",
    "Dolce",
    "Fruttata",
    "Fresca",
    "Polverosa",
    "Orientale",
    "Speziata"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterFamilyScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
) {
    val currentSelected by catalogViewModel.selectedFamilies.collectAsState()
    var selectedFamilies by remember { mutableStateOf(currentSelected) }

    LaunchedEffect(currentSelected) {
        selectedFamilies = currentSelected
    }

    Scaffold(containerColor = PageBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSubHeader(
                title = "FAMIGLIA OLFATTIVA",
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack(Screen.Catalog.route, inclusive = false) }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 24.dp)
            ) {
                items(familyOptions) { family ->
                    CheckboxOptionRow(
                        text = family,
                        isSelected = selectedFamilies.contains(family),
                        onClick = {
                            selectedFamilies = if (selectedFamilies.contains(family)) {
                                selectedFamilies - family
                            } else {
                                selectedFamilies + family
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
                        catalogViewModel.setSelectedFamilies(selectedFamilies)
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