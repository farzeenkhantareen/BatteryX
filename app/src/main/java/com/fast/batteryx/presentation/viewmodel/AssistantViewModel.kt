package com.fast.batteryx.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fast.batteryx.data.source.BatteryBroadcastListener
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AssistantMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

data class AssistantUiState(
    val messages: List<AssistantMessage> = listOf(
        AssistantMessage("Hi! I'm your Battery Assistant. Ask me anything about your battery health.", false)
    ),
    val suggestedQueries: List<String> = listOf(
        "Why is my battery draining fast?",
        "Is my charger healthy?",
        "Why is charging slow?",
        "How can I improve battery life?",
        "What's my battery health?"
    )
)

@HiltViewModel
class AssistantViewModel @Inject constructor(
    private val broadcastListener: BatteryBroadcastListener,
    private val batteryCollector: BatteryDataCollector,
    private val sessionRepository: ChargingSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    fun sendMessage(query: String) {
        val msgs = _uiState.value.messages.toMutableList()
        msgs.add(AssistantMessage(query, true))
        _uiState.value = _uiState.value.copy(messages = msgs)

        viewModelScope.launch {
            val response = generateResponse(query)
            val updated = _uiState.value.messages.toMutableList()
            updated.add(AssistantMessage(response, false))
            _uiState.value = _uiState.value.copy(messages = updated)
        }
    }

    private suspend fun generateResponse(query: String): String {
        val info = broadcastListener.getLatestBatteryInfo()
        val currentMa = batteryCollector.getCurrentMilliamps()
        val designCap = batteryCollector.getDesignCapacityMilliampereHours()
        val fullCap = batteryCollector.getFullChargeCapacityMilliampereHours()
        val healthPct = if (designCap > 0 && fullCap > 0) (fullCap.toFloat() / designCap * 100f) else 100f
        val tempC = info.temperature / 10f
        val absCurrent = kotlin.math.abs(currentMa)

        val lower = query.lowercase()

        return when {
            lower.contains("drain") || lower.contains("draining") -> {
                buildString {
                    append("Your battery is currently at ${info.percentage}%. ")
                    if (!info.isCharging) {
                        append("Current drain: ${absCurrent}mA. ")
                        if (absCurrent > 500) append("This is higher than normal. Background apps or screen brightness might be causing excessive drain. ")
                        else append("Drain rate looks normal. ")
                    } else append("Your device is currently charging, so no drain right now. ")
                    if (tempC > 38) append("Also, temperature is ${tempC.toInt()}°C which is a bit high — heat increases drain.")
                }
            }
            lower.contains("charger") || lower.contains("cable") -> {
                buildString {
                    if (info.isCharging) {
                        append("Charging at ${absCurrent}mA via ${info.getChargerType()}. ")
                        if (absCurrent > 2000) append("Your charger seems healthy — delivering fast charging speeds! ")
                        else if (absCurrent > 800) append("Charging at normal speeds. Your charger is working fine. ")
                        else append("Charging is slower than expected. Try a different cable or clean the port. ")
                    } else {
                        append("You're not currently charging. Plug in your device to test the charger. ")
                    }
                }
            }
            lower.contains("slow") && lower.contains("charg") -> {
                buildString {
                    if (info.isCharging) {
                        append("Current charging speed: ${absCurrent}mA. ")
                        if (absCurrent < 500) append("This is very slow. Possible causes: bad cable, dirty port, background usage, or low-power USB port.")
                        else if (absCurrent < 1000) append("This is slower than optimal. Try using the original charger and cable.")
                        else append("Charging speed looks normal actually!")
                    } else append("Plug in your charger first, then ask me again.")
                    if (tempC > 40) append(" High temperature (${tempC.toInt()}°C) also throttles charging speed.")
                }
            }
            lower.contains("health") -> {
                buildString {
                    append("Battery health: ${healthPct.toInt()}%. ")
                    if (designCap > 0) append("Design capacity: ${designCap}mAh, Current capacity: ${fullCap}mAh. ")
                    when {
                        healthPct >= 85 -> append("Your battery is in excellent condition!")
                        healthPct >= 70 -> append("Good condition, but some wear is present.")
                        healthPct >= 50 -> append("Fair condition. Consider being careful with charging habits.")
                        else -> append("Your battery has significant wear. Consider replacement.")
                    }
                }
            }
            lower.contains("improve") || lower.contains("tips") || lower.contains("extend") -> {
                "Here are tips to extend your battery lifespan:\n\n" +
                "• Keep charge between 20%-80%\n" +
                "• Avoid overnight charging\n" +
                "• Don't use the phone while charging\n" +
                "• Keep the phone cool — avoid direct sunlight\n" +
                "• Use original or certified chargers\n" +
                "• Reduce screen brightness and timeout\n" +
                "• Turn off features you don't use (Bluetooth, Location)"
            }
            lower.contains("temperature") || lower.contains("temp") || lower.contains("hot") -> {
                buildString {
                    append("Current temperature: ${tempC.toInt()}°C. ")
                    when {
                        tempC < 25 -> append("Nice and cool — optimal for battery health.")
                        tempC < 35 -> append("Normal range. Nothing to worry about.")
                        tempC < 42 -> append("Getting warm. Avoid heavy usage while charging.")
                        else -> append("⚠️ Too hot! Stop using the phone and let it cool down. High heat damages the battery.")
                    }
                }
            }
            else -> {
                "Battery: ${info.percentage}% | Health: ${healthPct.toInt()}% | Temp: ${tempC.toInt()}°C | " +
                if (info.isCharging) "Charging at ${absCurrent}mA" else "Discharging at ${absCurrent}mA" +
                "\n\nTry asking about: battery drain, charger health, charging speed, temperature, or tips to improve battery life."
            }
        }
    }
}
