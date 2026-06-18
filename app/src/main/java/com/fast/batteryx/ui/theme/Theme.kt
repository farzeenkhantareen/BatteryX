package com.fast.batteryx.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Theme Preference ─────────────────────────────────────────────────────────
enum class ThemePreference { DARK, LIGHT, SYSTEM }

val LocalThemePreference = staticCompositionLocalOf { ThemePreference.SYSTEM }

// ─── Dark Color Scheme ────────────────────────────────────────────────────────
private val BatteryXDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = Color(0xFF003459),
    onPrimaryContainer = Color(0xFFB3E5FC),
    secondary = DarkSecondary,
    onSecondary = Color(0xFF1A0080),
    secondaryContainer = Color(0xFF4527A0),
    onSecondaryContainer = Color(0xFFEDE7F6),
    tertiary = NeonGreen,
    onTertiary = Color(0xFF003D14),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkBorder,
    error = CoralRed,
    onError = Color(0xFF370000),
    surfaceContainer = DarkCard,
    surfaceContainerHigh = DarkCardElevated,
    surfaceContainerHighest = Color(0xFF232334),
)

// ─── Light Color Scheme ───────────────────────────────────────────────────────
private val BatteryXLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = Color(0xFFE1F5FE),
    onPrimaryContainer = Color(0xFF003459),
    secondary = LightSecondary,
    onSecondary = LightOnPrimary,
    secondaryContainer = Color(0xFFEDE7F6),
    onSecondaryContainer = Color(0xFF1A0080),
    tertiary = Color(0xFF00897B),
    onTertiary = Color(0xFFFFFFFF),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    outlineVariant = LightBorder,
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    surfaceContainer = LightCard,
    surfaceContainerHigh = LightCardElevated,
    surfaceContainerHighest = Color(0xFFECEEFF),
)

// ─── Theme Composable ─────────────────────────────────────────────────────────
@Composable
fun BatteryXTheme(
    themePreference: ThemePreference = ThemePreference.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = when (themePreference) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> systemDark
    }

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (useDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDark -> BatteryXDarkColorScheme
        else -> BatteryXLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !useDark
                isAppearanceLightNavigationBars = !useDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = BatteryXTypography,
        shapes = BatteryXShapes,
        content = content
    )
}

// ─── Helper extension ─────────────────────────────────────────────────────────
@Composable
fun isDarkTheme(themePreference: ThemePreference = ThemePreference.SYSTEM): Boolean {
    return when (themePreference) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM -> isSystemInDarkTheme()
    }
}