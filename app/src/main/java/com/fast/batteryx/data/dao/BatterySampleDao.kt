package com.fast.batteryx.data.dao

import androidx.room.*
import com.fast.batteryx.data.entity.BatterySample
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for BatterySample entity.
 * Provides CRUD operations and query methods for battery sample data.
 */
@Dao
interface BatterySampleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sample: BatterySample)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(samples: List<BatterySample>)

    @Query("SELECT * FROM battery_samples ORDER BY timestamp DESC LIMIT 1")
    fun getLatestSample(): Flow<BatterySample?>

    @Query("""
        SELECT * FROM battery_samples 
        WHERE timestamp >= :startTime AND timestamp <= :endTime
        ORDER BY timestamp DESC
    """)
    fun getSamplesByTimeRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<BatterySample>>

    @Query("""
        SELECT * FROM battery_samples 
        WHERE DATE(timestamp) = DATE(:targetDate)
        ORDER BY timestamp DESC
    """)
    fun getSamplesByDate(targetDate: LocalDateTime): Flow<List<BatterySample>>

    @Query("SELECT AVG(temperature) FROM battery_samples WHERE isCharging = 1")
    fun getAverageChargingTemperature(): Flow<Float>

    @Query("SELECT MAX(temperature) FROM battery_samples WHERE isCharging = 1")
    fun getPeakChargingTemperature(): Flow<Int>

    @Query("SELECT COUNT(*) FROM battery_samples")
    fun getTotalSampleCount(): Flow<Int>

    @Query("DELETE FROM battery_samples WHERE timestamp < :oldestDate")
    suspend fun deleteOlderThan(oldestDate: LocalDateTime)

    @Delete
    suspend fun delete(sample: BatterySample)

    @Query("DELETE FROM battery_samples")
    suspend fun clearAll()
}

