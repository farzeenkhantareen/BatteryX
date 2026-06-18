package com.fast.batteryx.data.repository

import com.fast.batteryx.data.dao.ChargingSessionDao
import com.fast.batteryx.data.entity.ChargingSession
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Implementation of ChargingSessionRepository.
 */
class ChargingSessionRepositoryImpl @Inject constructor(
    private val dao: ChargingSessionDao
) : ChargingSessionRepository {

    override suspend fun insertSession(session: ChargingSession): Long {
        return dao.insert(session)
    }

    override suspend fun updateSession(session: ChargingSession) {
        dao.update(session)
    }

    override fun getLatestSession(): Flow<ChargingSession?> {
        return dao.getLatestSession()
    }

    override fun getActiveSession(): Flow<ChargingSession?> {
        return dao.getActiveSession()
    }

    override fun getCompletedSessionsByRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<ChargingSession>> {
        return dao.getCompletedSessionsByRange(startTime, endTime)
    }

    override fun getRecentCompletedSessions(limit: Int): Flow<List<ChargingSession>> {
        return dao.getRecentCompletedSessions(limit)
    }

    override fun getCompletedSessionCount(): Flow<Int> {
        return dao.getCompletedSessionCount()
    }

    override fun getAverageCapacityAdded(): Flow<Float> {
        return dao.getAverageCapacityAdded()
    }

    override fun getAverageChargingCurrent(): Flow<Float> {
        return dao.getAverageChargingCurrent()
    }

    override fun getTotalCapacityAdded(): Flow<Double?> {
        return dao.getTotalCapacityAdded()
    }

    override suspend fun updateSessionEnd(
        sessionId: Long,
        endTime: LocalDateTime,
        endPercent: Int,
        chargeAddedMah: Double,
        averageCurrentMa: Double
    ) {
        dao.updateSessionEnd(sessionId, endTime, endPercent, chargeAddedMah, averageCurrentMa)
    }

    override suspend fun deleteOldCompletedSessions(oldestDate: LocalDateTime) {
        dao.deleteOldCompletedSessions(oldestDate)
    }
}

