package com.fast.batteryx.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.fast.batteryx.ui.theme.GlassBorder
import com.fast.batteryx.ui.theme.GlassDark
import com.fast.batteryx.ui.theme.GlassWhite

/**
 * Glassmorphism-styled card with translucent background and gradient border.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    isDark: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val bgColor = if (isDark) GlassWhite else GlassDark.copy(alpha = 0.04f)
    val borderColor = if (isDark) GlassBorder else Color.White.copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(bgColor)
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    listOf(
                        borderColor,
                        borderColor.copy(alpha = 0.1f),
                        borderColor.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(cornerRadius)
            )
            .padding(16.dp)
    ) {
        content()
    }
}
