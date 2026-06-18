package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.entity.HealthHistory
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import com.fast.batteryx.domain.repository.BatteryPredictionRepository
import com.fast.batteryx.data.entity.BatteryPrediction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class HealthUiState(
    val healthPercent: Float = 100f,
    val healthLabel: String = "Excellent",
    val designCapacityMah: Int = 0,
    val estimatedCapacityMah: Int = 0,
    val capacityLostMah: Int = 0,
    val wearPercent: Float = 0f,
    val healthHistory: List<HealthHistory> = emptyList(),
    val predictions: List<PredictionItem> = emptyList(),
    val isLoading: Boolean = true
)

data class PredictionItem(
    val label: String,
    val predictedHealth: Float,
    val confidence: Float
)

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val batteryCollector: BatteryDataCollector,
    private val healthRepository: HealthHistoryRepository,
    private val predictionRepository: BatteryPredictionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthUiState())
    val uiState: StateFlow<HealthUiState> = _uiState.asStateFlow()

    init {
        loadHealthData()
        loadHistory()
        generatePredictions()
    }

    private fun loadHealthData() {
        viewModelScope.launch {
            val designCap = batteryCollector.getDesignCapacityMilliampereHours()
            val fullCap = batteryCollector.getFullChargeCapacityMilliampereHours()

            val healthPct = if (designCap > 0 && fullCap > 0) {
                (fullCap.toFloat() / designCap * 100f).coerceIn(0f, 100f)
            } else 100f

            val wearPct = (100f - healthPct).coerceAtLeast(0f)
            val lost = if (designCap > 0 && fullCap > 0) designCap - fullCap else 0

            _uiState.value = _uiState.value.copy(
                healthPercent = healthPct,
                healthLabel = when {
                    healthPct >= 85f -> "Excellent"
                    healthPct >= 70f -> "Good"
                    healthPct >= 50f -> "Fair"
                    healthPct >= 30f -> "Poor"
                    else -> "Critical"
                },
                designCapacityMah = designCap,
                estimatedCapacityMah = fullCap,
                capacityLostMah = lost,
                wearPercent = wearPct,
                isLoading = false
            )
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            healthRepository.getRecentHealth(90).collect { history ->
                _uiState.value = _uiState.value.copy(healthHistory = history)
            }
        }
    }

    private fun generatePredictions() {
        viewModelScope.launch {
            val designCap = batteryCollector.getDesignCapacityMilliampereHours()
            val fullCap = batteryCollector.getFullChargeCapacityMilliampereHours()
            val currentHealth = if (designCap > 0 && fullCap > 0) {
                (fullCap.toFloat() / designCap * 100f).coerceIn(0f, 100f)
            } else 100f

            // Simple linear degradation model: ~0.5% per month average
            val degradePerMonth = 0.5f
            val predictions = listOf(
                PredictionItem("3 Months", (currentHealth - degradePerMonth * 3).coerceAtLeast(0f), 0.85f),
                PredictionItem("6 Months", (currentHealth - degradePerMonth * 6).coerceAtLeast(0f), 0.7f),
                PredictionItem("1 Year",   (currentHealth - degradePerMonth * 12).coerceAtLeast(0f), 0.55f),
            )

            _uiState.value = _uiState.value.copy(predictions = predictions)
        }
    }
}
