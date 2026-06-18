package com.fast.batteryx.domain.model

import java.time.LocalDate

/**
 * Domain model for battery health information.
 */
data class BatteryHealth(
    val estimatedCapacity: Int, // mAh
    val designCapacity: Int, // mAh
    val healthPercentage: Float, // 0-100
    val wearPercentage: Float, // 0-100
    val chargeCyclesEstimate: Float,
    val lastUpdated: LocalDate
) {
    val capacityLost: Int get() = designCapacity - estimatedCapacity
    val degradationStatus: String get() = when {
        healthPercentage >= 90 -> "Excellent"
        healthPercentage >= 80 -> "Good"
        healthPercentage >= 70 -> "Fair"
        healthPercentage >= 50 -> "Poor"
        else -> "Critical"
    }
}

