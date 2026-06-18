package com.fast.batteryx.presentation.viewmodel

import android.os.BatteryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.source.BatteryBroadcastInfo
import com.fast.batteryx.data.source.BatteryBroadcastListener
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.BatterySampleRepository
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val batteryPercent: Int = 0,
    val isCharging: Boolean = false,
    val temperature: Float = 0f,       // °C
    val voltage: Float = 0f,           // V
    val currentMa: Int = 0,            // mA (negative = discharging)
    val health: Int = BatteryManager.BATTERY_HEALTH_UNKNOWN,
    val healthPercent: Float = 100f,
    val chargerType: String = "Unknown",
    val chargingSpeed: String = "—",
    val screenTimeRemaining: String = "—",
    val cycleCount: Int = 0,
    val wearPercent: Float = 0f,
    val designCapacityMah: Int = 0,
    val currentCapacityMah: Int = 0,
    val insights: List<InsightItem> = emptyList()
)

data class InsightItem(
    val emoji: String,
    val title: String,
    val description: String
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val broadcastListener: BatteryBroadcastListener,
    private val batteryCollector: BatteryDataCollector,
    private val sampleRepository: BatterySampleRepository,
    private val sessionRepository: ChargingSessionRepository,
    private val healthRepository: HealthHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeBattery()
        loadStats()
    }

    private fun observeBattery() {
        viewModelScope.launch {
            broadcastListener.observeBatteryChanges().collect { info ->
                val currentMa = batteryCollector.getCurrentMilliamps()
                val designCap = batteryCollector.getDesignCapacityMilliampereHours()
                val fullCap = batteryCollector.getFullChargeCapacityMilliampereHours()

                val healthPct = if (designCap > 0 && fullCap > 0) {
                    (fullCap.toFloat() / designCap * 100f).coerceIn(0f, 100f)
                } else 100f

                val wearPct = (100f - healthPct).coerceAtLeast(0f)

                _uiState.value = _uiState.value.copy(
                    batteryPercent = info.percentage,
                    isCharging = info.isCharging,
                    temperature = info.temperature / 10f,
                    voltage = info.voltage / 1000f,
                    currentMa = currentMa,
                    health = info.health,
                    healthPercent = healthPct,
                    chargerType = if (info.isCharging) info.getChargerType() else "Not Charging",
                    designCapacityMah = designCap,
                    currentCapacityMah = fullCap,
                    wearPercent = wearPct,
                    chargingSpeed = classifyChargingSpeed(currentMa, info.isCharging),
                    screenTimeRemaining = estimateScreenTime(info.percentage, currentMa, info.isCharging),
                    insights = generateInsights(info, currentMa, healthPct, wearPct)
                )
            }
        }
    }

    private fun loadStats() {
        viewModelScope.launch {
            sessionRepository.getCompletedSessionCount().collect { count ->
                _uiState.value = _uiState.value.copy(cycleCount = count)
            }
        }
    }

    private fun classifyChargingSpeed(currentMa: Int, isCharging: Boolean): String {
        if (!isCharging) return "—"
        val absCurrent = kotlin.math.abs(currentMa)
        return when {
            absCurrent > 3000 -> "⚡ Super Fast"
            absCurrent > 2000 -> "⚡ Fast"
            absCurrent > 1000 -> "Normal"
            absCurrent > 500  -> "Slow"
            else              -> "Trickle"
        }
    }

    private fun estimateScreenTime(percent: Int, currentMa: Int, isCharging: Boolean): String {
        if (isCharging || currentMa >= 0) return "Charging"
        val drainRate = kotlin.math.abs(currentMa)
        if (drainRate < 50) return "Calculating..."
        // rough estimate: assume ~3500 mAh battery
        val remainingMah = percent / 100f * 3500f
        val hoursLeft = remainingMah / drainRate
        val hours = hoursLeft.toInt()
        val minutes = ((hoursLeft - hours) * 60).toInt()
        return when {
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    private fun generateInsights(
        info: BatteryBroadcastInfo,
        currentMa: Int,
        healthPct: Float,
        wearPct: Float
    ): List<InsightItem> {
        val insights = mutableListOf<InsightItem>()
        val tempC = info.temperature / 10f

        if (tempC > 40f) {
            insights.add(InsightItem(
                "🌡️", "High Temperature",
                "Battery is at ${tempC.toInt()}°C. Consider stopping heavy usage."
            ))
        }

        if (info.isCharging && kotlin.math.abs(currentMa) < 500) {
            insights.add(InsightItem(
                "🐌", "Slow Charging",
                "Charging speed is lower than normal. Try a different cable."
            ))
        }

        if (wearPct > 15f) {
            insights.add(InsightItem(
                "⚠️", "Battery Wear",
                "Your battery has ${wearPct.toInt()}% wear. Avoid charging to 100%."
            ))
        }

        if (info.isCharging && info.percentage > 80) {
            insights.add(InsightItem(
                "💡", "Unplug Soon",
                "Battery above 80%. Unplugging extends battery lifespan."
            ))
        }

        if (!info.isCharging && info.percentage < 20) {
            insights.add(InsightItem(
                "🔋", "Low Battery",
                "Consider charging soon to avoid deep discharge."
            ))
        }

        if (insights.isEmpty()) {
            insights.add(InsightItem(
                "✅", "All Good",
                "Battery is healthy and performing normally."
            ))
        }

        return insights
    }
}
