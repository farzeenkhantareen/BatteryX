package com.fast.batteryx.data.dao

import androidx.room.*
import com.fast.batteryx.data.entity.BatteryPrediction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Data Access Object for BatteryPrediction entity.
 * Manages battery health predictions and forecasts.
 */
@Dao
interface BatteryPredictionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: BatteryPrediction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(predictions: List<BatteryPrediction>)

    @Query("""
        SELECT * FROM battery_predictions 
        WHERE predictionDate = :date
        ORDER BY targetDate ASC
    """)
    fun getPredictionsByDate(date: LocalDate): Flow<List<BatteryPrediction>>

    @Query("""
        SELECT * FROM battery_predictions 
        WHERE targetDate >= :startDate AND targetDate <= :endDate
        ORDER BY targetDate ASC
    """)
    fun getPredictionsByRange(startDate: LocalDate, endDate: LocalDate): Flow<List<BatteryPrediction>>

    @Query("""
        SELECT * FROM battery_predictions 
        WHERE targetDate = :targetDate
        ORDER BY predictionDate DESC
        LIMIT 1
    """)
    fun getLatestPredictionForDate(targetDate: LocalDate): Flow<BatteryPrediction?>

    @Delete
    suspend fun delete(prediction: BatteryPrediction)

    @Query("DELETE FROM battery_predictions WHERE predictionDate < :oldestDate")
    suspend fun deleteOlderThan(oldestDate: LocalDate)
}

