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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.aromabox.data.model.Distributor
import com.example.aromabox.ui.navigation.Screen
import com.example.aromabox.ui.viewmodels.CatalogViewModel
import com.example.aromabox.ui.viewmodels.DistributorViewModel

private val PageBackground = Color(0xFFF2F2F2)
private val HeaderBackground = Color(0xFFC4B9FF).copy(alpha = 0.40f)
private val PrimaryColor = Color(0xFF8378BF)
private val NeutralColor = Color(0xFF737083)
private val TextGray = Color(0xFF6B7280)
private val SuccessGreen = Color(0xFF22C55E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDistributorScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel,
    distributorViewModel: DistributorViewModel
) {
    val distributors by distributorViewModel.distributors.collectAsState()
    val selectedDistributor by catalogViewModel.selectedDistributor.collectAsState()

    // Solo distributori attivi
    val activeDistributors = distributors.filter { it.attivo }

    Scaffold(
        topBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = HeaderBackground
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = NeutralColor
                        )
                    }

                    Text(
                        text = "Seleziona Distributore",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = Screen.Catalog,
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
                .padding(16.dp)
        ) {
            // Opzione "Tutti i distributori" (nessun filtro)
            DistributorFilterOption(
                title = "Tutti i distributori",
                subtitle = "Mostra profumi da tutti i distributori",
                isSelected = selectedDistributor == null,
                onClick = {
                    catalogViewModel.setSelectedDistributor(null)
                    navController.popBackStack()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Titolo sezione
            Text(
                text = "DISTRIBUTORI ATTIVI",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeutralColor,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Lista distributori attivi
            if (activeDistributors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nessun distributore attivo al momento",
                        fontSize = 14.sp,
                        color = TextGray
                    )
                }
            } else {
                activeDistributors.forEach { distributor ->
                    DistributorFilterOption(
                        title = distributor.nome,
                        subtitle = distributor.getIndirizzoCompleto(),
                        productCount = distributor.inventario.filter { it.value > 0 }.size,
                        isSelected = selectedDistributor?.id == distributor.id,
                        onClick = {
                            catalogViewModel.setSelectedDistributor(distributor)
                            navController.popBackStack()
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sezione distributori non attivi (solo info)
            val inactiveDistributors = distributors.filter { !it.attivo }
            if (inactiveDistributors.isNotEmpty()) {
                Text(
                    text = "PROSSIMAMENTE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralColor.copy(alpha = 0.6f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                inactiveDistributors.forEach { distributor ->
                    DistributorFilterOption(
                        title = distributor.nome,
                        subtitle = distributor.getIndirizzoCompleto(),
                        isSelected = false,
                        isEnabled = false,
                        onClick = { }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun DistributorFilterOption(
    title: String,
    subtitle: String,
    productCount: Int? = null,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isEnabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) Color.White else Color.White.copy(alpha = 0.5f)
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                brush = androidx.compose.ui.graphics.SolidColor(PrimaryColor),
                width = 2.dp
            )
        } else {
            null
        },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio/Check indicator
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) PrimaryColor
                        else if (isEnabled) Color.Transparent
                        else Color.Gray.copy(alpha = 0.2f)
                    )
                    .border(
                        width = 2.dp,
                        color = if (isSelected) PrimaryColor
                        else if (isEnabled) NeutralColor
                        else Color.Gray.copy(alpha = 0.4f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selezionato",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEnabled) Color.Black else Color.Gray
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isEnabled) TextGray else Color.Gray.copy(alpha = 0.6f)
                )
            }

            // Contatore prodotti (se presente)
            if (productCount != null && isEnabled) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$productCount",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(
                        text = "prodotti",
                        fontSize = 10.sp,
                        color = TextGray
                    )
                }
            }

            // Badge "Prossimamente" per distributori non attivi
            if (!isEnabled) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Gray.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Presto",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}