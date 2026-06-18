package com.fast.batteryx.domain.usecase

import com.fast.batteryx.data.entity.BatteryPrediction
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min

/**
 * Use case for predicting future battery health.
 * Uses linear regression and degradation trends to forecast health.
 */
class PredictBatteryHealthUseCase @Inject constructor(
    private val healthRepository: HealthHistoryRepository
) {

    /**
     * Generate health predictions for the next 12 months.
     * Uses linear regression based on historical data.
     */
    suspend fun predictHealthTrend(
        designCapacity: Int,
        replacementThreshold: Float = 80f // Replace when health drops below this %
    ): List<BatteryPrediction> {
        val today = LocalDate.now()
        val predictions = mutableListOf<BatteryPrediction>()

        // Get last 30 days of health data
        val thirtyDaysAgo = today.minusDays(30)
        val recentHealth = healthRepository.getHealthByDateRange(thirtyDaysAgo, today)
            .first()

        if (recentHealth.isEmpty()) {
            // No data yet, return neutral predictions
            return generateNeutralPredictions(today)
        }

        // Calculate degradation rate (health % per day)
        val healthValues = recentHealth.sortedBy { it.date }
            .map { it.healthPercentage }

        val degradationRate = if (healthValues.size >= 2) {
            val firstHealth = healthValues.first()
            val lastHealth = healthValues.last()
            val daysSpan = healthValues.size - 1
            (firstHealth - lastHealth) / daysSpan
        } else {
            0.1f // Default: 0.1% per day
        }

        // Current health
        val currentHealth = recentHealth.lastOrNull()?.healthPercentage ?: 100f

        // Predict 3, 6, and 12 months
        val predictionPoints = listOf(90, 180, 365) // days

        for (daysAhead in predictionPoints) {
            val targetDate = today.plusDays(daysAhead.toLong())
            val predictedHealth = max(0f, currentHealth - (degradationRate * daysAhead))

            // Calculate confidence based on data freshness
            val confidence = min(0.95f, 0.5f + (recentHealth.size / 100f))

            // Calculate days until replacement
            val daysUntilReplacement = if (degradationRate > 0) {
                ((currentHealth - replacementThreshold) / degradationRate).toInt()
            } else {
                null
            }

            predictions.add(
                BatteryPrediction(
                    predictionDate = today,
                    targetDate = targetDate,
                    predictedHealthPercentage = predictedHealth,
                    confidence = confidence,
                    daysUntilReplacement = daysUntilReplacement,
                    predictionType = "LINEAR"
                )
            )
        }

        return predictions
    }

    /**
     * Calculate charge cycles from capacity history.
     * Estimates based on cumulative capacity loss.
     */
    suspend fun estimateChargeCycles(
        designCapacity: Int,
        currentCapacity: Int
    ): Float {
        // Assumes ~0.5mAh loss per cycle on average
        val capacityLost = designCapacity - currentCapacity
        return capacityLost / 0.5f
    }

    /**
     * Estimate remaining lifespan in months
     */
    suspend fun estimateRemainingLifespan(
        currentHealth: Float,
        degradationRatePerDay: Float,
        replacementThreshold: Float = 80f
    ): Int {
        if (degradationRatePerDay <= 0) return 120 // Assume 10 years

        val daysRemaining = (currentHealth - replacementThreshold) / degradationRatePerDay
        return (daysRemaining / 30).toInt().coerceAtLeast(0)
    }

    /**
     * Generate neutral predictions when no data is available
     */
    private fun generateNeutralPredictions(today: LocalDate): List<BatteryPrediction> {
        return listOf(
            BatteryPrediction(
                predictionDate = today,
                targetDate = today.plusDays(90),
                predictedHealthPercentage = 99f,
                confidence = 0.1f,
                daysUntilReplacement = null,
                predictionType = "NEUTRAL"
            ),
            BatteryPrediction(
                predictionDate = today,
                targetDate = today.plusDays(180),
                predictedHealthPercentage = 98f,
                confidence = 0.1f,
                daysUntilReplacement = null,
                predictionType = "NEUTRAL"
            ),
            BatteryPrediction(
                predictionDate = today,
                targetDate = today.plusDays(365),
                predictedHealthPercentage = 97f,
                confidence = 0.1f,
                daysUntilReplacement = null,
                predictionType = "NEUTRAL"
            )
        )
    }
}

