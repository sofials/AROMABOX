package com.example.aromabox.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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

// Note aromatiche dal Figma
val noteOptions = listOf(
    "Tuberosa",
    "Gelsomino",
    "Giglio",
    "Narciso",
    "Gardenia",
    "Ylang-ylang",
    "Viola",
    "Geranio",
    "Iris",
    "Neroli"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterNotesScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
) {
    val currentSelected by catalogViewModel.selectedNotes.collectAsState()
    var selectedNotes by remember { mutableStateOf(currentSelected) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(currentSelected) {
        selectedNotes = currentSelected
    }

    // Filtra note in base alla ricerca
    val filteredNotes = if (searchQuery.isEmpty()) {
        noteOptions
    } else {
        noteOptions.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Scaffold(containerColor = PageBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSubHeader(
                title = "NOTE AROMATICHE",
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack(Screen.Catalog.route, inclusive = false) }
            )

            // Barra di ricerca
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                placeholder = {
                    Text(
                        text = "cerca...",
                        color = Color.Black.copy(alpha = 0.57f),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Cerca",
                        tint = Color(0xFF6B7280)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFFD1D5DB),
                    unfocusedBorderColor = Color(0xFFD1D5DB)
                ),
                shape = RoundedCornerShape(63.dp),
                singleLine = true
            )

            // Lista note
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp)
            ) {
                items(filteredNotes) { note ->
                    CheckboxOptionRow(
                        text = note,
                        isSelected = selectedNotes.contains(note),
                        onClick = {
                            selectedNotes = if (selectedNotes.contains(note)) {
                                selectedNotes - note
                            } else {
                                selectedNotes + note
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
                        catalogViewModel.setSelectedNotes(selectedNotes)
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