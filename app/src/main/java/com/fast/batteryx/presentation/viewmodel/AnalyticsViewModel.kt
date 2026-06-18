package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.entity.BatterySample
import com.fast.batteryx.domain.repository.BatterySampleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

enum class AnalyticsPeriod(val label: String, val hours: Long) {
    DAY("24h", 24),
    WEEK("7 Days", 168),
    MONTH("30 Days", 720)
}

data class AnalyticsUiState(
    val selectedPeriod: AnalyticsPeriod = AnalyticsPeriod.DAY,
    val samples: List<BatterySample> = emptyList(),
    val avgDrainPerHour: Float = 0f,
    val screenUsagePercent: Float = 40f,
    val standbyUsagePercent: Float = 45f,
    val chargingUsagePercent: Float = 15f,
    val isLoading: Boolean = true
)

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val sampleRepository: BatterySampleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalyticsUiState())
    val uiState: StateFlow<AnalyticsUiState> = _uiState.asStateFlow()

    init {
        loadData(AnalyticsPeriod.DAY)
    }

    fun selectPeriod(period: AnalyticsPeriod) {
        _uiState.value = _uiState.value.copy(selectedPeriod = period, isLoading = true)
        loadData(period)
    }

    private fun loadData(period: AnalyticsPeriod) {
        viewModelScope.launch {
            val since = LocalDateTime.now().minusHours(period.hours)
            sampleRepository.getSamplesSince(since).collect { samples ->
                val drainSamples = samples.filter { !it.isCharging && it.current < 0 }
                val avgDrain = if (drainSamples.isNotEmpty()) {
                    drainSamples.map { kotlin.math.abs(it.current) }.average().toFloat()
                } else 0f

                // Calculate usage breakdown from sample data
                val total = samples.size.toFloat().coerceAtLeast(1f)
                val screenOn = samples.count { it.screenOn }.toFloat()
                val charging = samples.count { it.isCharging }.toFloat()
                val standby = total - screenOn - charging

                _uiState.value = _uiState.value.copy(
                    samples = samples,
                    avgDrainPerHour = avgDrain,
                    screenUsagePercent = (screenOn / total * 100f).coerceIn(0f, 100f),
                    standbyUsagePercent = (standby / total * 100f).coerceIn(0f, 100f),
                    chargingUsagePercent = (charging / total * 100f).coerceIn(0f, 100f),
                    isLoading = false
                )
            }
        }
    }
}
