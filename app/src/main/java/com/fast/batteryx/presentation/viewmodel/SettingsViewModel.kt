package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.entity.UserSettings
import com.fast.batteryx.domain.repository.UserSettingsRepository
import com.fast.batteryx.ui.theme.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val dynamicColors: Boolean = true,
    val chargeAlertPercent: Int = 80,
    val tempAlertThreshold: Int = 45,
    val enableChargeAlert: Boolean = true,
    val enableTempAlert: Boolean = true,
    val enableWearAlert: Boolean = true,
    val enableSound: Boolean = true,
    val enableVibration: Boolean = true,
    val samplingInterval: Int = 10
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init { loadSettings() }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                if (settings != null) {
                    _uiState.value = SettingsUiState(
                        themePreference = when {
                            settings.isDarkMode -> ThemePreference.DARK
                            else -> ThemePreference.LIGHT
                        },
                        dynamicColors = settings.useDynamicColor,
                        chargeAlertPercent = settings.chargeAlertPercentage,
                        tempAlertThreshold = settings.temperatureAlertThreshold,
                        enableChargeAlert = settings.enableChargeAlert,
                        enableTempAlert = settings.enableTemperatureAlert,
                        enableWearAlert = settings.enableWearAlert,
                        enableSound = settings.enableSoundNotification,
                        enableVibration = settings.enableVibration,
                        samplingInterval = settings.samplingIntervalMinutes
                    )
                }
            }
        }
    }

    fun setTheme(pref: ThemePreference) {
        _uiState.value = _uiState.value.copy(themePreference = pref)
        updateSettings { it.copy(isDarkMode = pref == ThemePreference.DARK) }
    }

    fun setDynamicColors(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(dynamicColors = enabled)
        updateSettings { it.copy(useDynamicColor = enabled) }
    }

    fun setChargeAlert(percent: Int) {
        _uiState.value = _uiState.value.copy(chargeAlertPercent = percent)
        updateSettings { it.copy(chargeAlertPercentage = percent) }
    }

    fun toggleChargeAlert(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableChargeAlert = enabled)
        updateSettings { it.copy(enableChargeAlert = enabled) }
    }

    fun toggleTempAlert(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableTempAlert = enabled)
        updateSettings { it.copy(enableTemperatureAlert = enabled) }
    }

    fun toggleWearAlert(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(enableWearAlert = enabled)
        updateSettings { it.copy(enableWearAlert = enabled) }
    }

    private fun updateSettings(transform: (UserSettings) -> UserSettings) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings().first() ?: UserSettings()
            settingsRepository.updateSettings(transform(current))
        }
    }
}
