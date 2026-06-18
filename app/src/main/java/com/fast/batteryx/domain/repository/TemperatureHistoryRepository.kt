package com.fast.batteryx.domain.repository

import com.fast.batteryx.data.entity.TemperatureHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface TemperatureHistoryRepository {
    suspend fun insertTemperature(history: TemperatureHistory)
    fun getLatestTemperature(): Flow<TemperatureHistory?>
    fun getTemperatureByRange(startTime: LocalDateTime, endTime: LocalDateTime): Flow<List<TemperatureHistory>>
    fun getAverageChargingTemp(): Flow<Float>
    fun getPeakChargingTemp(): Flow<Int>
    fun getOverallAverageTemp(): Flow<Float>
    suspend fun deleteOlderThan(oldestDate: LocalDateTime)
}
