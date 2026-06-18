package com.fast.batteryx.data.repository

import com.fast.batteryx.data.dao.TemperatureHistoryDao
import com.fast.batteryx.data.entity.TemperatureHistory
import com.fast.batteryx.domain.repository.TemperatureHistoryRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class TemperatureHistoryRepositoryImpl @Inject constructor(
    private val dao: TemperatureHistoryDao
) : TemperatureHistoryRepository {

    override suspend fun insertTemperature(history: TemperatureHistory) = dao.insert(history)

    override fun getLatestTemperature(): Flow<TemperatureHistory?> = dao.getLatestTemperature()

    override fun getTemperatureByRange(
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ): Flow<List<TemperatureHistory>> = dao.getTemperatureByRange(startTime, endTime)

    override fun getAverageChargingTemp(): Flow<Float> = dao.getAverageChargingTemp()

    override fun getPeakChargingTemp(): Flow<Int> = dao.getPeakChargingTemp()

    override fun getOverallAverageTemp(): Flow<Float> = dao.getOverallAverageTemp()

    override suspend fun deleteOlderThan(oldestDate: LocalDateTime) = dao.deleteOlderThan(oldestDate)
}
