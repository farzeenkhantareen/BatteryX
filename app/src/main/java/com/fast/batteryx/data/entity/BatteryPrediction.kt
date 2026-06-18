package com.fast.batteryx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entity storing battery health predictions.
 * ML-inspired predictions of future health status.
 */
@Entity(tableName = "battery_predictions")
data class BatteryPrediction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Prediction data
    val predictionDate: LocalDate, // Date this prediction was made
    val targetDate: LocalDate, // Date this prediction targets
    val predictedHealthPercentage: Float, // Predicted health %
    val confidence: Float, // Confidence level (0-1)
    val daysUntilReplacement: Int? = null, // Days until battery should be replaced

    // Metadata
    val predictionType: String = "LINEAR" // LINEAR, ML, EXPONENTIAL, etc.
)

