package com.fast.batteryx.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fast.batteryx.presentation.screen.AnalyticsScreen
import com.fast.batteryx.presentation.screen.AssistantScreen
import com.fast.batteryx.presentation.screen.ChargingScreen
import com.fast.batteryx.presentation.screen.DashboardScreen
import com.fast.batteryx.presentation.screen.HabitsScreen
import com.fast.batteryx.presentation.screen.HealthScreen
import com.fast.batteryx.presentation.screen.MoreScreen
import com.fast.batteryx.presentation.screen.PredictorScreen
import com.fast.batteryx.presentation.screen.SettingsScreen
import com.fast.batteryx.presentation.screen.TemperatureScreen

@Composable
fun BatteryXNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Health.route) {
            HealthScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Charging.route) {
            ChargingScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(viewModel = hiltViewModel())
        }
        composable(Screen.More.route) {
            MoreScreen(onNavigate = { screen ->
                navController.navigate(screen.route)
            })
        }
        composable(Screen.Temperature.route) {
            TemperatureScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Habits.route) {
            HabitsScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Predictor.route) {
            PredictorScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Assistant.route) {
            AssistantScreen(viewModel = hiltViewModel())
        }
        composable(Screen.Settings.route) {
            SettingsScreen(viewModel = hiltViewModel())
        }
    }
}
