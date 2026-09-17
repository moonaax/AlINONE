package com.allinone.music.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LandingScreen(
    onOpenMusic: () -> Unit,
    onOpenUIKit: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = "ALLINONE",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 4.sp,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "All in one place",
                fontSize = 14.sp,
                color = Color(0xFF888898),
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(56.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                LandingCard(
                    title = "Music",
                    subtitle = "聚合音乐平台",
                    emoji = "🎵",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFF6C3CE1), Color(0xFF9B6DFF))
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenMusic,
                )
                LandingCard(
                    title = "UI Kit",
                    subtitle = "现代美学组件库",
                    emoji = "🎨",
                    gradient = Brush.linearGradient(
                        colors = listOf(Color(0xFFE13C68), Color(0xFFFF6D8F))
                    ),
                    modifier = Modifier.weight(1f),
                    onClick = onOpenUIKit,
                )
            }
        }
    }
}

@Composable
private fun LandingCard(
    title: String,
    subtitle: String,
    emoji: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(24.dp))
            .background(gradient)
            .clickable(onClick = onClick)
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = emoji,
                fontSize = 40.sp,
            )
            Column {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.75f),
                )
            }
        }
    }
}
