package com.example.aromabox.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DrawerBackground = Color(0xFFCDC4FC)

/**
 * Menu laterale che si apre da destra
 * Voci: Info, Contatti, Disconnessione
 */
@Composable
fun AppDrawerContent(
    onCloseClick: () -> Unit,
    onInfoClick: () -> Unit,
    onContattiClick: () -> Unit,
    onDisconnessioneClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(DrawerBackground)
            .padding(top = 88.dp) // Spazio dall'alto come nel Figma
    ) {
        // Pulsante X per chiudere - posizionato in alto a destra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 45.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Chiudi menu",
                    tint = Color(0xFF222222),
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(33.dp))

        DrawerMenuItem(
            text = "Info",
            onClick = onInfoClick
        )

        Spacer(modifier = Modifier.height(33.dp))

        DrawerMenuItem(
            text = "Contatti",
            onClick = onContattiClick
        )

        Spacer(modifier = Modifier.height(33.dp))

        DrawerMenuItem(
            text = "Disconnessione",
            onClick = onDisconnessioneClick
        )
    }
}

@Composable
private fun DrawerMenuItem(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold, // 600 in Figma
        color = Color(0xFF222222),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 44.dp, vertical = 12.dp)
    )
}