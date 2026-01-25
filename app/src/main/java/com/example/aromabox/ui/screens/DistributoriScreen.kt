package com.example.aromabox.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController  // ✅ NavController invece di NavHostController
import androidx.navigation.NavHostController
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.theme.Primary
import com.example.aromabox.ui.theme.Surface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributoriScreen(navController: NavController) {  // ✅ NavController
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Distributori", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = Screen.Distributori,  // ✅ Nome corretto
                navController = navController as NavHostController
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mappa Distributori", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Coming soon...", fontSize = 14.sp)
        }
    }
}