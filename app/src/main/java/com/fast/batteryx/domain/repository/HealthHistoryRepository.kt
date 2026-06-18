package com.fast.batteryx.domain.repository

import com.fast.batteryx.data.entity.HealthHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for battery health history data.
 */
interface HealthHistoryRepository {

    /**
     * Record a health history entry
     */
    suspend fun recordHealth(health: HealthHistory)

    /**
     * Get the latest health record
     */
    fun getLatestHealth(): Flow<HealthHistory?>

    /**
     * Get health history within a date range
     */
    fun getHealthByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthHistory>>

    /**
     * Get recent health records
     */
    fun getRecentHealth(limit: Int = 30): Flow<List<HealthHistory>>

    /**
     * Get average health percentage
     */
    fun getAverageHealth(): Flow<Float>

    /**
     * Get average wear percentage
     */
    fun getAverageWear(): Flow<Float>

    /**
     * Get total count of health records
     */
    fun getTotalHistoryCount(): Flow<Int>

    /**
     * Delete old health records
     */
    suspend fun deleteOlderThan(oldestDate: LocalDate)
}

