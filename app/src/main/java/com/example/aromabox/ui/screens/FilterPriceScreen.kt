package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
private val SliderTrackColor = Color(0xFFE3E8EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPriceScreen(
    navController: NavController,
    catalogViewModel: CatalogViewModel
) {
    val minPrice by catalogViewModel.minPriceFilter.collectAsState()
    val maxPrice by catalogViewModel.maxPriceFilter.collectAsState()

    var sliderPosition by remember { mutableStateOf(minPrice..maxPrice) }
    val priceRange = 1f..4f

    LaunchedEffect(minPrice, maxPrice) {
        sliderPosition = minPrice..maxPrice
    }

    Scaffold(containerColor = PageBackground) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            FilterSubHeader(
                title = "PREZZO",
                onBackClick = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack(Screen.Catalog.route, inclusive = false) }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input boxes per min e max
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 41.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PriceInputBox(value = sliderPosition.start, modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .width(29.dp)
                        .height(0.5.dp)
                        .background(NeutralColor)
                )

                PriceInputBox(value = sliderPosition.endInclusive, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Range Slider
            RangeSlider(
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = priceRange,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = PrimaryColor,
                    activeTrackColor = PrimaryColor,
                    inactiveTrackColor = SliderTrackColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 41.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp)
            ) {
                Button(
                    onClick = {
                        catalogViewModel.setPriceRange(sliderPosition.start, sliderPosition.endInclusive)
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
fun PriceInputBox(value: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(48.dp)
            .border(0.5.dp, NeutralColor, RoundedCornerShape(0.dp))
            .background(PageBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "€",
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = TextColor,
                lineHeight = 12.sp
            )
            Text(
                text = String.format("%.0f", value),
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = TextColor,
                lineHeight = 16.sp
            )
        }
    }
}