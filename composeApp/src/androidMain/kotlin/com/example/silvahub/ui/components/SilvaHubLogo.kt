package com.example.silvahub.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.silvahub.R

private val LogoNavy = Color(0xFF0B2030)

@Composable
fun SilvaHubLogo(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    circular: Boolean = false,
) {
    val shape = if (circular) CircleShape else RoundedCornerShape(8.dp)
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(LogoNavy),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.silvahub_launcher_foreground),
            contentDescription = "SilvaHub",
            modifier = Modifier.size(size * 0.92f),
            contentScale = ContentScale.Fit,
        )
    }
}
