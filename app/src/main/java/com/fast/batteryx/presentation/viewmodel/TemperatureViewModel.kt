package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.entity.TemperatureHistory
import com.fast.batteryx.data.source.BatteryBroadcastListener
import com.fast.batteryx.domain.repository.TemperatureHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class TempPeriod(val label: String, val hours: Long) {
    HOURLY("Hourly", 6),
    DAILY("Daily", 24),
    WEEKLY("Weekly", 168)
}

data class TemperatureUiState(
    val currentTemp: Float = 25f,
    val averageTemp: Float = 30f,
    val peakTemp: Float = 35f,
    val isCharging: Boolean = false,
    val selectedPeriod: TempPeriod = TempPeriod.HOURLY,
    val history: List<TemperatureHistory> = emptyList()
)

@HiltViewModel
class TemperatureViewModel @Inject constructor(
    private val broadcastListener: BatteryBroadcastListener,
    private val tempRepository: TemperatureHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TemperatureUiState())
    val uiState: StateFlow<TemperatureUiState> = _uiState.asStateFlow()

    init {
        observeTemp()
        loadHistory(TempPeriod.HOURLY)
        loadAverages()
    }

    private fun observeTemp() {
        viewModelScope.launch {
            broadcastListener.observeBatteryChanges().collect { info ->
                _uiState.value = _uiState.value.copy(
                    currentTemp = info.temperature / 10f,
                    isCharging = info.isCharging
                )
            }
        }
    }

    private fun loadAverages() {
        viewModelScope.launch {
            tempRepository.getOverallAverageTemp().collect { avg ->
                _uiState.value = _uiState.value.copy(averageTemp = avg)
            }
        }
        viewModelScope.launch {
            tempRepository.getPeakChargingTemp().collect { peak ->
                _uiState.value = _uiState.value.copy(peakTemp = peak.toFloat())
            }
        }
    }

    fun selectPeriod(period: TempPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period)
        loadHistory(period)
    }

    private fun loadHistory(period: TempPeriod) {
        viewModelScope.launch {
            val since = LocalDateTime.now().minusHours(period.hours)
            tempRepository.getTemperatureByRange(since, LocalDateTime.now()).collect { history ->
                _uiState.value = _uiState.value.copy(history = history)
            }
        }
    }
}
