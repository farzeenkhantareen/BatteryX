package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.entity.ChargingSession
import com.fast.batteryx.data.source.BatteryBroadcastListener
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import com.fast.batteryx.domain.repository.UserSettingsRepository
import com.fast.batteryx.data.entity.UserSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ChargingUiState(
    val isCharging: Boolean = false,
    val currentMa: Int = 0,
    val powerWatts: Float = 0f,
    val voltage: Float = 0f,
    val temperature: Float = 0f,
    val chargerType: String = "—",
    val batteryPercent: Int = 0,
    val chargingReadings: List<ChargingReading> = emptyList(),
    val recentSessions: List<ChargingSession> = emptyList(),
    val chargeAlarmPercent: Int = 80,
    val chargeAlarmEnabled: Boolean = true
)

data class ChargingReading(
    val timestamp: Long,
    val currentMa: Int,
    val voltage: Float
)

@HiltViewModel
class ChargingViewModel @Inject constructor(
    private val broadcastListener: BatteryBroadcastListener,
    private val batteryCollector: BatteryDataCollector,
    private val sessionRepository: ChargingSessionRepository,
    private val settingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChargingUiState())
    val uiState: StateFlow<ChargingUiState> = _uiState.asStateFlow()

    init {
        observeCharging()
        loadSessions()
        loadAlarmSettings()
    }

    private fun observeCharging() {
        viewModelScope.launch {
            broadcastListener.observeBatteryChanges().collect { info ->
                val currentMa = batteryCollector.getCurrentMilliamps()
                val voltage = info.voltage / 1000f
                val power = kotlin.math.abs(currentMa) / 1000f * voltage

                val readings = _uiState.value.chargingReadings.toMutableList()
                readings.add(ChargingReading(System.currentTimeMillis(), currentMa, voltage))
                if (readings.size > 60) readings.removeFirst()

                _uiState.value = _uiState.value.copy(
                    isCharging = info.isCharging,
                    currentMa = currentMa,
                    powerWatts = power,
                    voltage = voltage,
                    temperature = info.temperature / 10f,
                    chargerType = if (info.isCharging) info.getChargerType() else "Not Charging",
                    batteryPercent = info.percentage,
                    chargingReadings = readings
                )
            }
        }
    }

    private fun loadSessions() {
        viewModelScope.launch {
            sessionRepository.getRecentCompletedSessions(20).collect { sessions ->
                _uiState.value = _uiState.value.copy(recentSessions = sessions)
            }
        }
    }

    private fun loadAlarmSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collect { settings ->
                _uiState.value = _uiState.value.copy(
                    chargeAlarmPercent = settings?.chargeAlertPercentage ?: 80,
                    chargeAlarmEnabled = settings?.enableChargeAlert ?: true
                )
            }
        }
    }

    fun setChargeAlarmPercent(percent: Int) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings().first() ?: UserSettings()
            settingsRepository.updateSettings(current.copy(chargeAlertPercentage = percent))
        }
    }

    fun toggleChargeAlarm(enabled: Boolean) {
        viewModelScope.launch {
            val current = settingsRepository.getSettings().first() ?: UserSettings()
            settingsRepository.updateSettings(current.copy(enableChargeAlert = enabled))
        }
    }
}
