package com.fast.batteryx.domain.repository

import com.fast.batteryx.data.entity.BatterySample
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface BatterySampleRepository {
    fun getAllSamples(): Flow<List<BatterySample>>
    fun getSamplesSince(since: LocalDateTime): Flow<List<BatterySample>>
    fun getRecentSamples(limit: Int = 100): Flow<List<BatterySample>>
    suspend fun insertSample(sample: BatterySample)
    suspend fun insertSamples(samples: List<BatterySample>)
    suspend fun deleteOldSamples(before: LocalDateTime)
    fun getCurrentBattery(): Flow<BatterySample?>
}
