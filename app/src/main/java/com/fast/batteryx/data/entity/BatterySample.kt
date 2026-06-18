package com.fast.batteryx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

/**
 * Entity representing a single battery sample snapshot.
 * Collected periodically (every 5-10 minutes) to track battery metrics over time.
 */
@Entity(tableName = "battery_samples")
data class BatterySample(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Battery metrics
    val percentage: Int, // Battery percentage (0-100)
    val temperature: Int, // Temperature in Celsius
    val voltage: Int, // Voltage in millivolts
    val current: Int, // Current in milliamps (negative = discharging, positive = charging)
    val health: Int, // Battery health percentage
    val status: Int, // Battery status (charging, discharging, etc.)
    val plugged: Int, // Charger type (AC, USB, Wireless)
    val capacity: Int, // Current capacity in mAh

    // Metadata
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isCharging: Boolean = false,
    val screenOn: Boolean = false
)

