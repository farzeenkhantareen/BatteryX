package com.fast.batteryx.data.repository

import com.fast.batteryx.data.dao.BatteryPredictionDao
import com.fast.batteryx.data.entity.BatteryPrediction
import com.fast.batteryx.domain.repository.BatteryPredictionRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class BatteryPredictionRepositoryImpl @Inject constructor(
    private val dao: BatteryPredictionDao
) : BatteryPredictionRepository {
    override suspend fun insertPrediction(prediction: BatteryPrediction) = dao.insert(prediction)
    override suspend fun insertAllPredictions(predictions: List<BatteryPrediction>) = dao.insertAll(predictions)
    override fun getPredictionsByDate(date: LocalDate): Flow<List<BatteryPrediction>> = dao.getPredictionsByDate(date)
    override fun getPredictionsByRange(startDate: LocalDate, endDate: LocalDate): Flow<List<BatteryPrediction>> = dao.getPredictionsByRange(startDate, endDate)
    override fun getLatestPredictionForDate(targetDate: LocalDate): Flow<BatteryPrediction?> = dao.getLatestPredictionForDate(targetDate)
    override suspend fun deleteOlderThan(oldestDate: LocalDate) = dao.deleteOlderThan(oldestDate)
}
