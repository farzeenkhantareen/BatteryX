package com.fast.batteryx.domain.repository

import com.fast.batteryx.data.entity.BatteryPrediction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface BatteryPredictionRepository {
    suspend fun insertPrediction(prediction: BatteryPrediction)
    suspend fun insertAllPredictions(predictions: List<BatteryPrediction>)
    fun getPredictionsByDate(date: LocalDate): Flow<List<BatteryPrediction>>
    fun getPredictionsByRange(startDate: LocalDate, endDate: LocalDate): Flow<List<BatteryPrediction>>
    fun getLatestPredictionForDate(targetDate: LocalDate): Flow<BatteryPrediction?>
    suspend fun deleteOlderThan(oldestDate: LocalDate)
}
