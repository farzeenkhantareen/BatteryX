package com.fast.batteryx.domain.repository

import com.fast.batteryx.data.entity.ChargingSession
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for charging session data.
 */
interface ChargingSessionRepository {

    /**
     * Insert a new charging session
     */
    suspend fun insertSession(session: ChargingSession): Long

    /**
     * Update an existing charging session
     */
    suspend fun updateSession(session: ChargingSession)

    /**
     * Get the latest charging session
     */
    fun getLatestSession(): Flow<ChargingSession?>

    /**
     * Get currently active charging session (if any)
     */
    fun getActiveSession(): Flow<ChargingSession?>

    /**
     * Get completed sessions within a date range
     */
    fun getCompletedSessionsByRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<ChargingSession>>

    /**
     * Get recent completed sessions
     */
    fun getRecentCompletedSessions(limit: Int = 20): Flow<List<ChargingSession>>

    /**
     * Get count of completed sessions
     */
    fun getCompletedSessionCount(): Flow<Int>

    /**
     * Get average capacity added per session
     */
    fun getAverageCapacityAdded(): Flow<Float>

    /**
     * Get average charging current
     */
    fun getAverageChargingCurrent(): Flow<Float>

    /**
     * Get total capacity added across all sessions
     */
    fun getTotalCapacityAdded(): Flow<Double?>

    /**
     * End an active session
     */
    suspend fun updateSessionEnd(
        sessionId: Long,
        endTime: LocalDateTime,
        endPercent: Int,
        chargeAddedMah: Double,
        averageCurrentMa: Double
    )

    /**
     * Delete old completed sessions
     */
    suspend fun deleteOldCompletedSessions(oldestDate: LocalDateTime)
}

