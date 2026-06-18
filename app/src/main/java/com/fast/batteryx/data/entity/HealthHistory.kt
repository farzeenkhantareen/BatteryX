package com.fast.batteryx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entity tracking battery health estimates over time.
 * Stored daily/weekly to create historical trend data for predictions.
 */
@Entity(tableName = "health_history")
data class HealthHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Health metrics
    val estimatedCapacity: Int, // Estimated current capacity (mAh)
    val designCapacity: Int, // Design capacity (mAh)
    val healthPercentage: Float, // Health % (0-100)
    val wearPercentage: Float, // Wear % (100 - healthPercentage)

    // Historical data
    val date: LocalDate = LocalDate.now(),
    val chargeCyclesEstimate: Float = 0f, // Estimated charge cycles completed
    val samplesAnalyzed: Int = 0 // Number of battery samples used in calculation
)

