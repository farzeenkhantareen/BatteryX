package com.fast.batteryx.data.dao

import androidx.room.*
import com.fast.batteryx.data.entity.TemperatureHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for TemperatureHistory entity.
 * Manages temperature tracking and thermal analysis.
 */
@Dao
interface TemperatureHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: TemperatureHistory)

    @Query("SELECT * FROM temperature_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestTemperature(): Flow<TemperatureHistory?>

    @Query("""
        SELECT * FROM temperature_history 
        WHERE timestamp >= :startTime AND timestamp <= :endTime
        ORDER BY timestamp DESC
    """)
    fun getTemperatureByRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<TemperatureHistory>>

    @Query("SELECT AVG(temperature) FROM temperature_history WHERE isCharging = 1")
    fun getAverageChargingTemp(): Flow<Float>

    @Query("SELECT MAX(temperature) FROM temperature_history WHERE isCharging = 1")
    fun getPeakChargingTemp(): Flow<Int>

    @Query("SELECT AVG(temperature) FROM temperature_history")
    fun getOverallAverageTemp(): Flow<Float>

    @Delete
    suspend fun delete(history: TemperatureHistory)

    @Query("DELETE FROM temperature_history WHERE timestamp < :oldestDate")
    suspend fun deleteOlderThan(oldestDate: LocalDateTime)
}

