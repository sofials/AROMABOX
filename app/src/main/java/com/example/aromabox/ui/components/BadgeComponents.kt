package com.example.aromabox.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.aromabox.R
import com.example.aromabox.data.model.Badge
import com.example.aromabox.data.model.BadgeCategory
import com.example.aromabox.data.model.BadgeDefinitions
import com.example.aromabox.ui.theme.Primary

// Colori dal Figma
private val BadgeCardBg = Color(0xFFF7F6FA)
private val BadgeIconBg = Color(0xFFDAD3FF)
private val BadgeCompletedColor = Color(0xFF90B87C)
private val BadgeLockedColor = Color(0xFFC4C4C4).copy(alpha = 0.45f)
private val BadgeTitleColor = Color(0xFF2A282F)
private val BadgeDescColor = Color(0xFF625F68)
private val CloseButtonColor = Color(0xFF8378BF)

/**
 * Contenuto principale dei badge organizzato per categoria
 * Layout: Titolo categoria + riga di badge
 */
@Composable
fun BadgeGridContent(
    badges: List<Badge>,
    onBadgeClick: (Badge) -> Unit,
    modifier: Modifier = Modifier
) {
    // Se la lista è vuota, usa i badge di default
    val badgesToShow = if (badges.isEmpty()) {
        BadgeDefinitions.allBadges.map { definition ->
            Badge.fromDefinition(definition, isUnlocked = false)
        }
    } else {
        badges
    }

    // Raggruppa i badge per categoria
    val badgesByCategory = badgesToShow.groupBy { it.category }

    // Ordine delle categorie (senza AMBASSADOR)
    val categoryOrder = listOf(
        BadgeCategory.APPRENDISTA.name,
        BadgeCategory.TESTER.name,
        BadgeCategory.ESPLORATORE.name
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(categoryOrder) { categoryName ->
            val categoryBadges = badgesByCategory[categoryName] ?: emptyList()
            if (categoryBadges.isNotEmpty()) {
                BadgeCategorySection(
                    categoryName = categoryName,
                    badges = categoryBadges.sortedBy { it.livello },
                    onBadgeClick = onBadgeClick
                )
            }
        }
    }
}

/**
 * Sezione singola categoria con titolo e riga di badge
 */
@Composable
fun BadgeCategorySection(
    categoryName: String,
    badges: List<Badge>,
    onBadgeClick: (Badge) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Titolo categoria
        Text(
            text = categoryName.uppercase(),
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF222222),
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Riga di badge
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            badges.forEach { badge ->
                BadgeItem(
                    badge = badge,
                    onClick = { onBadgeClick(badge) }
                )
            }
        }
    }
}

/**
 * Singolo badge con icona 60x60
 * Badge bloccati hanno sfondo circolare grigio e immagine in scala di grigi
 */
@Composable
fun BadgeItem(
    badge: Badge,
    onClick: () -> Unit
) {
    // ColorMatrix per scala di grigi (badge bloccati)
    val grayscaleMatrix = ColorMatrix().apply { setToSaturation(0f) }

    Box(
        modifier = Modifier
            .size(60.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (badge.isUnlocked) {
            // Badge sbloccato - immagine a colori senza sfondo
            Image(
                painter = painterResource(id = R.drawable.beautyicon),
                contentDescription = badge.nome,
                modifier = Modifier.size(55.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            // Badge bloccato - sfondo circolare grigio con immagine in scala di grigi
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8E8E8)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.beautyicon),
                    contentDescription = badge.nome,
                    modifier = Modifier
                        .size(45.dp)
                        .alpha(0.6f),
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.colorMatrix(grayscaleMatrix)
                )
            }
        }
    }
}

/**
 * Dialog dettaglio badge (quando si clicca su un badge)
 * L'icona sporge sopra la card senza coprire il titolo
 */
@Composable
fun BadgeDetailDialog(
    badge: Badge,
    onDismiss: () -> Unit
) {
    val grayscaleMatrix = ColorMatrix().apply { setToSaturation(0f) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            // Card principale - con padding top per lasciare spazio all'icona
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp), // Spazio per metà icona che sporge
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BadgeCardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp, bottom = 24.dp, start = 16.dp, end = 16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Titolo badge
                        Text(
                            text = badge.nome,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BadgeTitleColor,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Descrizione con icona check/locked
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            // Icona stato completamento
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (badge.isUnlocked) BadgeCompletedColor
                                        else BadgeLockedColor
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (badge.isUnlocked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Completato",
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Testo descrizione
                            Text(
                                text = badge.descrizione,
                                fontSize = 14.sp,
                                color = BadgeDescColor,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Data ottenimento se sbloccato
                        if (badge.isUnlocked && badge.dataOttenimento > 0) {
                            Spacer(modifier = Modifier.height(16.dp))

                            val dateString = java.text.SimpleDateFormat(
                                "dd/MM/yyyy",
                                java.util.Locale.ITALIAN
                            ).format(java.util.Date(badge.dataOttenimento))

                            Text(
                                text = "Ottenuto il $dateString",
                                fontSize = 12.sp,
                                color = BadgeCompletedColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Pulsante X per chiudere
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(CloseButtonColor)
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Chiudi",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Icona badge che sporge sopra la card
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BadgeIconBg),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.beautyicon),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp),
                    contentScale = ContentScale.Fit,
                    colorFilter = if (!badge.isUnlocked) {
                        ColorFilter.colorMatrix(grayscaleMatrix)
                    } else null
                )
            }
        }
    }
}

/**
 * Snackbar/Toast per notificare nuovo badge sbloccato
 */
@Composable
fun NewBadgeUnlockedSnackbar(
    badge: Badge,
    onDismiss: () -> Unit
) {
    Snackbar(
        modifier = Modifier.padding(16.dp),
        action = {
            TextButton(onClick = onDismiss) {
                Text("OK", color = Color.White)
            }
        },
        containerColor = Primary,
        contentColor = Color.White
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.beautyicon),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Nuovo Badge Sbloccato!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = badge.nome,
                    fontSize = 12.sp
                )
            }
        }
    }
}

/**
 * Componente per mostrare il progresso verso un badge
 */
@Composable
fun BadgeProgressIndicator(
    currentValue: Int,
    targetValue: Int,
    badgeName: String,
    modifier: Modifier = Modifier
) {
    val progress = (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f)

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = badgeName,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = "$currentValue/$targetValue",
                fontSize = 12.sp,
                color = Primary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = Primary,
            trackColor = Color(0xFFE0E0E0)
        )
    }
}