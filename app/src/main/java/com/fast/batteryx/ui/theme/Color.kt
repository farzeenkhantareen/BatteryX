package com.fast.batteryx.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Core Brand ──────────────────────────────────────────────────────────────
val ElectricBlue = Color(0xFF4FC3F7)
val ElectricBlueDeep = Color(0xFF0288D1)
val NeonGreen = Color(0xFF00E676)
val CyberPurple = Color(0xFF7C4DFF)
val AmberWarning = Color(0xFFFFAB40)
val CoralRed = Color(0xFFFF5252)

// ─── Dark Theme (AMOLED Premium) ─────────────────────────────────────────────
val DarkBackground = Color(0xFF050508)
val DarkSurface = Color(0xFF0D0D14)
val DarkSurfaceVariant = Color(0xFF13131E)
val DarkCard = Color(0xFF181825)
val DarkCardElevated = Color(0xFF1E1E2E)
val DarkBorder = Color(0xFF2A2A3E)
val DarkBorderGlow = Color(0xFF4FC3F740)

val DarkPrimary = ElectricBlue
val DarkOnPrimary = Color(0xFF003459)
val DarkSecondary = CyberPurple
val DarkOnSurface = Color(0xFFE8EAED)
val DarkOnSurfaceVariant = Color(0xFF9AA0B4)
val DarkOutline = Color(0xFF44475A)

// ─── Light Theme (Clean Modern) ──────────────────────────────────────────────
val LightBackground = Color(0xFFF8F9FF)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF0F2FF)
val LightCard = Color(0xFFFFFFFF)
val LightCardElevated = Color(0xFFF5F7FF)
val LightBorder = Color(0xFFE0E4F0)
val LightBorderGlow = Color(0xFF4FC3F730)

val LightPrimary = Color(0xFF0277BD)
val LightOnPrimary = Color(0xFFFFFFFF)
val LightSecondary = Color(0xFF5E35B1)
val LightOnSurface = Color(0xFF1A1C2E)
val LightOnSurfaceVariant = Color(0xFF5A607A)
val LightOutline = Color(0xFFBCC0D0)

// ─── Health Status Colors ─────────────────────────────────────────────────────
val HealthExcellent = Color(0xFF00E676)   // 85-100%
val HealthGood = Color(0xFF76FF03)        // 70-84%
val HealthFair = Color(0xFFFFD740)        // 50-69%
val HealthPoor = Color(0xFFFF6D00)        // 30-49%
val HealthCritical = Color(0xFFFF1744)    // 0-29%

// ─── Battery Level Colors ─────────────────────────────────────────────────────
val BatteryHigh = Color(0xFF00E676)
val BatteryMedium = Color(0xFF76FF03)
val BatteryLow = Color(0xFFFFAB40)
val BatteryCritical = Color(0xFFFF5252)

// ─── Temperature Colors ───────────────────────────────────────────────────────
val TempCool = Color(0xFF40C4FF)
val TempNormal = Color(0xFF00E676)
val TempWarm = Color(0xFFFFD740)
val TempHot = Color(0xFFFF6D00)
val TempDangerous = Color(0xFFFF1744)

// ─── Gradient Definitions ────────────────────────────────────────────────────
val GradientElectric = listOf(Color(0xFF4FC3F7), Color(0xFF7C4DFF))
val GradientHealth = listOf(Color(0xFF00E676), Color(0xFF00BCD4))
val GradientWarn = listOf(Color(0xFFFFAB40), Color(0xFFFF5252))
val GradientCharge = listOf(Color(0xFF4FC3F7), Color(0xFF00E676))
val GradientNight = listOf(Color(0xFF050508), Color(0xFF0D0D25))

// ─── Glassmorphism ───────────────────────────────────────────────────────────
val GlassWhite = Color(0x14FFFFFF)
val GlassWhiteStrong = Color(0x22FFFFFF)
val GlassBorder = Color(0x33FFFFFF)
val GlassDark = Color(0x1A000000)

// ─── Chart Colors ────────────────────────────────────────────────────────────
val ChartPrimary = ElectricBlue
val ChartSecondary = CyberPurple
val ChartTertiary = NeonGreen
val ChartFill = Color(0x264FC3F7)