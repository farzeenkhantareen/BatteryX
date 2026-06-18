package com.fast.batteryx.ui.theme

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ─── Animation Constants ──────────────────────────────────────────────────────
object AnimationTokens {
    const val FAST = 150
    const val NORMAL = 300
    const val SLOW = 500
    const val VERY_SLOW = 800

    fun <T> springSnappy(): AnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    fun <T> springSmooth(): AnimationSpec<T> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    fun <T> tweenNormal(): AnimationSpec<T> = tween(NORMAL)
    fun <T> tweenSlow(): AnimationSpec<T> = tween(SLOW)
}

// ─── Gradient Brushes ─────────────────────────────────────────────────────────
object GradientTokens {
    val electricBrush = Brush.linearGradient(GradientElectric)
    val healthBrush = Brush.linearGradient(GradientHealth)
    val warnBrush = Brush.linearGradient(GradientWarn)
    val chargeBrush = Brush.linearGradient(GradientCharge)
    val nightBrush = Brush.verticalGradient(GradientNight)

    fun batteryLevelBrush(percent: Int): Brush {
        val colors = when {
            percent > 60 -> listOf(BatteryHigh, Color(0xFF00BCD4))
            percent > 30 -> listOf(BatteryLow, AmberWarning)
            else -> listOf(BatteryCritical, CoralRed)
        }
        return Brush.verticalGradient(colors)
    }

    fun healthBrush(healthPercent: Float): Brush {
        val colors = when {
            healthPercent >= 85f -> listOf(HealthExcellent, Color(0xFF00BCD4))
            healthPercent >= 70f -> listOf(HealthGood, Color(0xFFCCFF90))
            healthPercent >= 50f -> listOf(HealthFair, AmberWarning)
            healthPercent >= 30f -> listOf(HealthPoor, AmberWarning)
            else -> listOf(HealthCritical, CoralRed)
        }
        return Brush.linearGradient(colors)
    }

    fun temperatureBrush(tempCelsius: Float): Brush {
        val colors = when {
            tempCelsius < 25f -> listOf(TempCool, TempNormal)
            tempCelsius < 35f -> listOf(TempNormal, TempWarm)
            tempCelsius < 45f -> listOf(TempWarm, TempHot)
            else -> listOf(TempHot, TempDangerous)
        }
        return Brush.verticalGradient(colors)
    }
}

// ─── Health Color Helpers ─────────────────────────────────────────────────────
fun healthColor(percent: Float): Color = when {
    percent >= 85f -> HealthExcellent
    percent >= 70f -> HealthGood
    percent >= 50f -> HealthFair
    percent >= 30f -> HealthPoor
    else -> HealthCritical
}

fun healthLabel(percent: Float): String = when {
    percent >= 85f -> "Excellent"
    percent >= 70f -> "Good"
    percent >= 50f -> "Fair"
    percent >= 30f -> "Poor"
    else -> "Critical"
}

fun batteryColor(percent: Int): Color = when {
    percent > 60 -> BatteryHigh
    percent > 30 -> BatteryLow
    else -> BatteryCritical
}

fun temperatureColor(celsius: Float): Color = when {
    celsius < 25f -> TempCool
    celsius < 35f -> TempNormal
    celsius < 45f -> TempWarm
    celsius < 50f -> TempHot
    else -> TempDangerous
}

// ─── Glassmorphism Modifier ───────────────────────────────────────────────────
@Composable
fun Modifier.glassCard(
    cornerRadius: Dp = 20.dp,
    borderWidth: Dp = 1.dp,
    isDark: Boolean = isSystemInDarkTheme()
): Modifier {
    val bgColor = if (isDark) GlassWhite else GlassDark.copy(alpha = 0.04f)
    val borderColor = if (isDark) GlassBorder else Color.White.copy(alpha = 0.6f)
    return this
        .clip(RoundedCornerShape(cornerRadius))
        .background(bgColor)
        .border(
            width = borderWidth,
            brush = Brush.linearGradient(
                listOf(
                    borderColor,
                    borderColor.copy(alpha = 0.1f),
                    borderColor.copy(alpha = 0.3f)
                )
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

// ─── Gradient Background Modifier ────────────────────────────────────────────
fun Modifier.gradientBackground(
    colors: List<Color>,
    angle: Float = 135f
): Modifier = this.background(
    brush = Brush.linearGradient(
        colors = colors,
        start = Offset(0f, 0f),
        end = Offset(
            kotlin.math.cos(Math.toRadians(angle.toDouble())).toFloat() * 1000f,
            kotlin.math.sin(Math.toRadians(angle.toDouble())).toFloat() * 1000f
        )
    )
)

// ─── Glow Effect ─────────────────────────────────────────────────────────────
fun Modifier.glowEffect(
    glowColor: Color,
    radius: Dp = 20.dp
): Modifier = this.drawBehind {
    drawCircle(
        color = glowColor.copy(alpha = 0.15f),
        radius = size.maxDimension / 2 + radius.toPx()
    )
}

// ─── Elevation Tokens ─────────────────────────────────────────────────────────
object ElevationTokens {
    val None = 0.dp
    val Low = 2.dp
    val Medium = 4.dp
    val High = 8.dp
    val Overlay = 16.dp
}
