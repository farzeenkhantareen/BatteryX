package com.fast.batteryx.data.repository

import com.fast.batteryx.data.dao.HealthHistoryDao
import com.fast.batteryx.data.entity.HealthHistory
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementation of HealthHistoryRepository.
 */
class HealthHistoryRepositoryImpl @Inject constructor(
    private val dao: HealthHistoryDao
) : HealthHistoryRepository {

    override suspend fun recordHealth(health: HealthHistory) {
        dao.insert(health)
    }

    override fun getLatestHealth(): Flow<HealthHistory?> {
        return dao.getLatestHealth()
    }

    override fun getHealthByDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<HealthHistory>> {
        return dao.getHealthByDateRange(startDate, endDate)
    }

    override fun getRecentHealth(limit: Int): Flow<List<HealthHistory>> {
        return dao.getRecentHealth(limit)
    }

    override fun getAverageHealth(): Flow<Float> {
        return dao.getAverageHealth()
    }

    override fun getAverageWear(): Flow<Float> {
        return dao.getAverageWear()
    }

    override fun getTotalHistoryCount(): Flow<Int> {
        return dao.getTotalHistoryCount()
    }

    override suspend fun deleteOlderThan(oldestDate: LocalDate) {
        dao.deleteOlderThan(oldestDate)
    }
}

