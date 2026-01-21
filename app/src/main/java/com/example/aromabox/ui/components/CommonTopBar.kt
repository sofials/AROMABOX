package com.example.aromabox.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.aromabox.R
import com.example.aromabox.ui.theme.Primary

private val HeaderBg = Color(0xFFC4B9FF).copy(alpha = 0.80f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(
    onMenuClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            // Logo dentro cerchio bianco
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "AromaBox Logo",
                    modifier = Modifier.size(32.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = onMenuClick,
                modifier = Modifier.padding(end = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Primary,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = HeaderBg
        )
    )
}