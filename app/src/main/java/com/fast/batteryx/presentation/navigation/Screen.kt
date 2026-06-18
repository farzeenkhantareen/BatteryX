package com.fast.batteryx.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Filled.BatteryChargingFull)
    object Health    : Screen("health",    "Health",    Icons.Filled.Favorite)
    object Charging  : Screen("charging",  "Charging",  Icons.Filled.BatteryChargingFull)
    object Analytics : Screen("analytics", "Analytics", Icons.Filled.Analytics)
    object More      : Screen("more",      "More",      Icons.Filled.MoreHoriz)

    // Sub-screens (no bottom nav)
    object Temperature : Screen("temperature", "Temperature", Icons.Filled.AutoAwesome)
    object Habits      : Screen("habits",      "Habits",      Icons.Filled.AutoAwesome)
    object Predictor   : Screen("predictor",   "Predictor",   Icons.Filled.AutoAwesome)
    object Assistant   : Screen("assistant",   "Assistant",   Icons.Filled.AutoAwesome)
    object Settings    : Screen("settings",    "Settings",    Icons.Filled.AutoAwesome)
}

val bottomNavItems = listOf(
    Screen.Dashboard,
    Screen.Health,
    Screen.Charging,
    Screen.Analytics,
    Screen.More
)
