package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class PredictorUiState(
    val currentHealthPercent: Float = 100f,
    val monthlyDegradation: Float = 0.5f,
    val estimatedLifespanMonths: Int = 36,
    val replacementDate: String = "",
    val agingTrend: List<AgingPoint> = emptyList(),
    val isLoading: Boolean = true
)

data class AgingPoint(
    val monthsFromNow: Int,
    val predictedHealth: Float
)

@HiltViewModel
class PredictorViewModel @Inject constructor(
    private val batteryCollector: BatteryDataCollector,
    private val healthRepository: HealthHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictorUiState())
    val uiState: StateFlow<PredictorUiState> = _uiState.asStateFlow()

    init { calculatePredictions() }

    private fun calculatePredictions() {
        viewModelScope.launch {
            val designCap = batteryCollector.getDesignCapacityMilliampereHours()
            val fullCap = batteryCollector.getFullChargeCapacityMilliampereHours()
            val currentHealth = if (designCap > 0 && fullCap > 0) {
                (fullCap.toFloat() / designCap * 100f).coerceIn(0f, 100f)
            } else 100f

            // Estimate degradation rate from history or use default 0.5%/month
            val degradeRate = 0.5f

            // Calculate months until 70% health (typical replacement threshold)
            val healthToLose = currentHealth - 70f
            val monthsRemaining = if (degradeRate > 0) (healthToLose / degradeRate).toInt().coerceAtLeast(0) else 60

            val replDate = LocalDate.now().plusMonths(monthsRemaining.toLong())
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

            // Generate aging curve
            val agingTrend = (0..36 step 3).map { month ->
                AgingPoint(
                    monthsFromNow = month,
                    predictedHealth = (currentHealth - degradeRate * month).coerceAtLeast(0f)
                )
            }

            _uiState.value = PredictorUiState(
                currentHealthPercent = currentHealth,
                monthlyDegradation = degradeRate,
                estimatedLifespanMonths = monthsRemaining,
                replacementDate = replDate.format(formatter),
                agingTrend = agingTrend,
                isLoading = false
            )
        }
    }
}
