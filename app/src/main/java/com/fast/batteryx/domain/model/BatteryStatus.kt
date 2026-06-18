package com.fast.batteryx.domain.model

import java.time.LocalDateTime

/**
 * Domain model for current battery status.
 * This is the main model used throughout the app for battery state.
 */
data class BatteryStatus(
    val percentage: Int, // 0-100
    val health: Int, // 0-100
    val temperature: Int, // Celsius
    val voltage: Int, // Millivolts
    val current: Int, // Milliamps (negative = discharging)
    val isCharging: Boolean,
    val chargerType: String, // AC, USB, Wireless, None
    val lastUpdated: LocalDateTime,
    val capacity: Int = 0, // Current capacity in mAh
    val designCapacity: Int = 0 // Design capacity in mAh
) {
    val isDischarging: Boolean get() = !isCharging && current < 0

    /**
     * Estimated screen-on time remaining in minutes
     */
    fun estimatedTimeRemaining(): Int {
        if (current >= 0) return -1 // Charging
        val absorbedPower = (current.toFloat() / 1000f) // Convert to Amps
        return if (absorbedPower > 0) {
            (percentage / 100f * capacity / absorbedPower / 60).toInt()
        } else {
            -1
        }
    }
}

