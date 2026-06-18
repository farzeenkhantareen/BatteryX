package com.fast.batteryx.data.dao

import androidx.room.*
import com.fast.batteryx.data.entity.HealthHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for HealthHistory entity.
 * Manages historical health data and trend analysis.
 */
@Dao
interface HealthHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: HealthHistory)

    @Query("SELECT * FROM health_history ORDER BY date DESC LIMIT 1")
    fun getLatestHealth(): Flow<HealthHistory?>

    @Query("""
        SELECT * FROM health_history 
        WHERE date >= :startDate AND date <= :endDate
        ORDER BY date DESC
    """)
    fun getHealthByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthHistory>>

    @Query("""
        SELECT * FROM health_history 
        ORDER BY date DESC
        LIMIT :limit
    """)
    fun getRecentHealth(limit: Int = 30): Flow<List<HealthHistory>>

    @Query("SELECT AVG(healthPercentage) FROM health_history")
    fun getAverageHealth(): Flow<Float>

    @Query("SELECT AVG(wearPercentage) FROM health_history")
    fun getAverageWear(): Flow<Float>

    @Query("SELECT COUNT(*) FROM health_history")
    fun getTotalHistoryCount(): Flow<Int>

    @Delete
    suspend fun delete(history: HealthHistory)

    @Query("DELETE FROM health_history WHERE date < :oldestDate")
    suspend fun deleteOlderThan(oldestDate: LocalDate)
}

