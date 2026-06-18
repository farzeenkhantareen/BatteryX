package com.fast.batteryx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entity tracking temperature readings over time.
 * Used for thermal analysis and anomaly detection.
 */
@Entity(tableName = "temperature_history")
data class TemperatureHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Temperature data
    val temperature: Int, // Temperature in Celsius
    val isCharging: Boolean, // Was device charging when sampled
    val batteryPercentage: Int, // Battery level when sampled

    // Metadata
    val timestamp: LocalDateTime = LocalDateTime.now()
)

