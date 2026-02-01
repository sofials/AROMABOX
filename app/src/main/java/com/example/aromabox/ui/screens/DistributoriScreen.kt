package com.example.aromabox.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.aromabox.ui.components.CommonTopBar
import com.example.aromabox.ui.navigation.Screen

private val PageBackground = Color(0xFFF2F2F2)
private val MapPlaceholderBg = Color(0xFFF9F9F9)
private val CardBorderColor = Color(0xFF818181)
private val TextGray = Color(0xFF6B7280)
private val NeutralColor = Color(0xFF737083)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributoriScreen(
    navController: NavController
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                onMenuClick = { /* TODO: Menu */ }
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

            // Placeholder Mappa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MapPlaceholderBg)
                    .border(1.dp, Color(0xFFBBB4B5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🗺️",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Mappa in arrivo...",
                        fontSize = 16.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Medium
                    )
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
                // Titolo sezione
                Text(
                    text = "TUTTI I DISTRIBUTORI AROMABOX (1)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = CardBorderColor,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Card distributore placeholder
                DistributoreCard(
                    nome = "Nome Distributore",
                    indirizzo = "Via Placeholder 123, 00000 Città (XX)"
                )
            }
        }
    }
}

@Composable
fun DistributoreCard(
    nome: String,
    indirizzo: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(CardBorderColor)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cerchio radio button (non selezionato)
            Box(
                modifier = Modifier
                    .size(17.dp)
                    .clip(CircleShape)
                    .background(PageBackground)
                    .border(0.5.dp, NeutralColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Testo
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = TextGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(nome)
                        }
                        withStyle(
                            style = SpanStyle(
                                color = TextGray,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append(" - $indirizzo")
                        }
                    },
                    lineHeight = 18.sp
                )
            }
        }
    }
}