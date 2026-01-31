package com.example.aromabox.ui.screens

import androidx.compose.foundation.layout.*
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

// Opzioni genere dal Figma
val genderOptions = listOf("Uomo", "Donna", "Unisex")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterGenderScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
) {
    val currentSelected by catalogViewModel.selectedGenders.collectAsState()
    var selectedGenders by remember { mutableStateOf(currentSelected) }

    LaunchedEffect(currentSelected) {
        selectedGenders = currentSelected
    }

    Scaffold(containerColor = PageBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSubHeader(
                title = "GENERE",
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack(Screen.Catalog.route, inclusive = false) }
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 24.dp)
            ) {
                genderOptions.forEach { gender ->
                    CheckboxOptionRow(
                        text = gender,
                        isSelected = selectedGenders.contains(gender),
                        onClick = {
                            selectedGenders = if (selectedGenders.contains(gender)) {
                                selectedGenders - gender
                            } else {
                                selectedGenders + gender
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
                        catalogViewModel.setSelectedGenders(selectedGenders)
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