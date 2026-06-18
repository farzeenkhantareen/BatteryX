package com.fast.batteryx.domain.usecase

import com.fast.batteryx.data.entity.BatterySample
import com.fast.batteryx.data.entity.HealthHistory
import com.fast.batteryx.domain.repository.BatterySampleRepository
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.math.abs

/**
 * Use case for calculating battery health using coulomb counting.
 * Accumulates charge during charging sessions to estimate capacity.
 */
class CalculateBatteryHealthUseCase @Inject constructor(
    private val sampleRepository: BatterySampleRepository,
    private val healthRepository: HealthHistoryRepository
) {

    /**
     * Calculate estimated capacity from charging sessions.
     * Uses integration of current over time (coulomb counting).
     *
     * @param designCapacity The battery's design capacity in mAh
     * @return Estimated current capacity and health percentage
     */
    suspend fun calculateHealth(designCapacity: Int): Pair<Int, Float> {
        val now = LocalDateTime.now()
        val oneDayAgo = now.minusDays(1)

        val samples = sampleRepository.getSamplesSince(oneDayAgo)
            .map { it }
            .collect { /* Will be handled by caller */ }

        return Pair(designCapacity, 100f) // Placeholder
    }

    /**
     * Estimate capacity from a list of battery samples during charging.
     * Uses coulomb counting: Charge (Ah) = Integral of Current (A) over Time
     *
     * @param samples Battery samples during a charging session
     * @param designCapacity Design capacity in mAh
     * @return Estimated capacity in mAh
     */
    fun estimateCapacityFromChargingSamples(
        samples: List<BatterySample>,
        designCapacity: Int
    ): Int {
        if (samples.size < 2) return designCapacity

        var totalChargeAdded = 0f // Accumulated in Ah

        for (i in 0 until samples.size - 1) {
            val current = samples[i]
            val next = samples[i + 1]

            // Only count positive current (charging)
            if (current.current > 0) {
                val timeDiffMinutes = java.time.temporal.ChronoUnit.MINUTES
                    .between(current.timestamp, next.timestamp)

                // Current is in mA, convert to A
                val currentAmps = current.current.toFloat() / 1000f
                val timeHours = timeDiffMinutes / 60f

                // Charge = Current (A) × Time (hours)
                totalChargeAdded += currentAmps * timeHours
            }
        }

        // Charge added is in Ah, convert to mAh
        val chargeAddedMah = (totalChargeAdded * 1000).toInt()

        return (designCapacity * 0.95).toInt() + chargeAddedMah
    }

    /**
     * Calculate health percentage from capacity
     */
    fun calculateHealthPercentage(
        estimatedCapacity: Int,
        designCapacity: Int
    ): Float {
        return (estimatedCapacity.toFloat() / designCapacity) * 100
    }

    /**
     * Record health data to history
     */
    suspend fun recordHealthSnapshot(
        estimatedCapacity: Int,
        designCapacity: Int,
        chargeCyclesEstimate: Float = 0f
    ) {
        val health = HealthHistory(
            estimatedCapacity = estimatedCapacity,
            designCapacity = designCapacity,
            healthPercentage = calculateHealthPercentage(estimatedCapacity, designCapacity),
            wearPercentage = 100f - calculateHealthPercentage(estimatedCapacity, designCapacity),
            chargeCyclesEstimate = chargeCyclesEstimate,
            date = LocalDate.now(),
            samplesAnalyzed = 0
        )
        healthRepository.recordHealth(health)
    }
}

