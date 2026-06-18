package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.entity.ChargingSession
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalTime
import javax.inject.Inject

data class HabitItem(
    val emoji: String,
    val title: String,
    val description: String,
    val severity: HabitSeverity
)

enum class HabitSeverity { GOOD, WARNING, BAD }

data class HabitsUiState(
    val habitScore: Float = 80f,
    val habits: List<HabitItem> = emptyList(),
    val recommendations: List<String> = emptyList(),
    val overnightChargingCount: Int = 0,
    val fullChargeCount: Int = 0,
    val highTempChargingCount: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class HabitsViewModel @Inject constructor(
    private val sessionRepository: ChargingSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HabitsUiState())
    val uiState: StateFlow<HabitsUiState> = _uiState.asStateFlow()

    init { analyzeHabits() }

    private fun analyzeHabits() {
        viewModelScope.launch {
            sessionRepository.getRecentCompletedSessions(50).collect { sessions ->
                val overnight = sessions.count { isOvernightCharge(it) }
                val fullCharge = sessions.count { (it.endPercentage ?: 0) >= 95 }
                val highTemp = sessions.count { it.peakTemperature > 42f }

                val habits = mutableListOf<HabitItem>()

                if (overnight > 3) {
                    habits.add(HabitItem("🌙", "Overnight Charging", "Detected $overnight overnight charges recently. This degrades battery health.", HabitSeverity.BAD))
                } else if (overnight > 0) {
                    habits.add(HabitItem("🌙", "Occasional Overnight", "$overnight overnight charge(s). Try to avoid leaving on charger all night.", HabitSeverity.WARNING))
                } else {
                    habits.add(HabitItem("🌙", "No Overnight Charging", "Great! You avoid overnight charging.", HabitSeverity.GOOD))
                }

                if (fullCharge > 5) {
                    habits.add(HabitItem("🔋", "Frequent Full Charges", "$fullCharge sessions charged above 95%. Keep below 80% for longevity.", HabitSeverity.BAD))
                } else if (fullCharge > 0) {
                    habits.add(HabitItem("🔋", "Occasional Full Charges", "$fullCharge full charges. Try to keep below 80%.", HabitSeverity.WARNING))
                } else {
                    habits.add(HabitItem("🔋", "Optimal Charging", "You avoid full charges. Excellent habit!", HabitSeverity.GOOD))
                }

                if (highTemp > 2) {
                    habits.add(HabitItem("🌡️", "High Temp Charging", "$highTemp sessions with high temperature. Avoid charging while using heavy apps.", HabitSeverity.BAD))
                } else {
                    habits.add(HabitItem("🌡️", "Cool Charging", "You charge at safe temperatures. Well done!", HabitSeverity.GOOD))
                }

                val score = calculateScore(overnight, fullCharge, highTemp, sessions.size)
                val recs = generateRecommendations(overnight, fullCharge, highTemp)

                _uiState.value = HabitsUiState(
                    habitScore = score,
                    habits = habits,
                    recommendations = recs,
                    overnightChargingCount = overnight,
                    fullChargeCount = fullCharge,
                    highTempChargingCount = highTemp,
                    isLoading = false
                )
            }
        }
    }

    private fun isOvernightCharge(session: ChargingSession): Boolean {
        val startHour = session.startTime.hour
        val endTime = session.endTime ?: return false
        val duration = Duration.between(session.startTime, endTime).toHours()
        return startHour >= 22 || startHour <= 5 && duration > 4
    }

    private fun calculateScore(overnight: Int, fullCharge: Int, highTemp: Int, total: Int): Float {
        if (total == 0) return 80f
        var score = 100f
        score -= overnight * 5f
        score -= fullCharge * 3f
        score -= highTemp * 4f
        return score.coerceIn(0f, 100f)
    }

    private fun generateRecommendations(overnight: Int, fullCharge: Int, highTemp: Int): List<String> {
        val recs = mutableListOf<String>()
        if (overnight > 0) recs.add("Set a charge alarm at 80% to avoid overnight charging.")
        if (fullCharge > 0) recs.add("Unplug at 80% to maximize battery lifespan.")
        if (highTemp > 0) recs.add("Avoid charging while gaming or using heavy apps.")
        recs.add("Keep your battery between 20%-80% for optimal health.")
        recs.add("Use the original charger or a certified one.")
        return recs
    }
}
