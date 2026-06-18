package com.fast.batteryx.data.dao

import androidx.room.*
import com.fast.batteryx.data.entity.ChargingSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Data Access Object for ChargingSession entity.
 * Provides operations for tracking and querying charging sessions.
 */
@Dao
interface ChargingSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: ChargingSession): Long

    @Update
    suspend fun update(session: ChargingSession)

    @Query("SELECT * FROM charging_sessions ORDER BY startTime DESC LIMIT 1")
    fun getLatestSession(): Flow<ChargingSession?>

    @Query("SELECT * FROM charging_sessions WHERE isComplete = 0 LIMIT 1")
    fun getActiveSession(): Flow<ChargingSession?>

    @Query("""
        SELECT * FROM charging_sessions 
        WHERE startTime >= :startTime AND startTime <= :endTime
        AND isComplete = 1
        ORDER BY startTime DESC
    """)
    fun getCompletedSessionsByRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<ChargingSession>>

    @Query("""
        SELECT * FROM charging_sessions
        WHERE isComplete = 1
        ORDER BY startTime DESC
        LIMIT :limit
    """)
    fun getRecentCompletedSessions(limit: Int = 20): Flow<List<ChargingSession>>

    @Query("SELECT COUNT(*) FROM charging_sessions WHERE isComplete = 1")
    fun getCompletedSessionCount(): Flow<Int>

    @Query("SELECT AVG(capacityAdded) FROM charging_sessions WHERE isComplete = 1")
    fun getAverageCapacityAdded(): Flow<Float>

    @Query("SELECT AVG(averageCurrent) FROM charging_sessions WHERE isComplete = 1")
    fun getAverageChargingCurrent(): Flow<Float>

    @Query("""
        SELECT SUM(capacityAdded) FROM charging_sessions 
        WHERE isComplete = 1
    """)
    fun getTotalCapacityAdded(): Flow<Double?>

    @Query("""
        UPDATE charging_sessions
        SET endTime = :endTime,
            endPercentage = :endPercent,
            capacityAdded = :chargeAddedMah,
            averageCurrent = :averageCurrentMa,
            isComplete = 1
        WHERE id = :sessionId
    """)
    suspend fun updateSessionEnd(
        sessionId: Long,
        endTime: java.time.LocalDateTime,
        endPercent: Int,
        chargeAddedMah: Double,
        averageCurrentMa: Double
    )

    @Delete
    suspend fun delete(session: ChargingSession)

    @Query("DELETE FROM charging_sessions WHERE isComplete = 1 AND endTime < :oldestDate")
    suspend fun deleteOldCompletedSessions(oldestDate: LocalDateTime)
}

