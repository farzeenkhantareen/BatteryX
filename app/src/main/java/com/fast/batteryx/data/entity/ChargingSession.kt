package com.fast.batteryx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entity representing a complete charging session.
 * Tracks from when charging starts until it stops.
 */
@Entity(tableName = "charging_sessions")
data class ChargingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Session timing
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,

    // Charging metrics
    val startPercentage: Int,            // Battery % when charging started
    val endPercentage: Int? = null,      // Battery % when charging ended
    val capacityAdded: Double = 0.0,     // Charge added (mAh)
    val averageCurrent: Double = 0.0,    // Average charging current (mA)
    val peakCurrent: Double = 0.0,       // Peak charging current (mA)
    val averageVoltage: Int = 0,         // Average charging voltage (mV)
    val averageTemperature: Float = 0f,  // Average temperature during charge (°C)
    val peakTemperature: Float = 0f,     // Peak temperature during charge (°C)

    // Charger info
    val chargerType: Int = 0,            // BatteryManager.BATTERY_PLUGGED_* constant
    val isComplete: Boolean = false
)
