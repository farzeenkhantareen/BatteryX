package com.fast.batteryx.data.repository

import com.fast.batteryx.data.dao.BatterySampleDao
import com.fast.batteryx.data.entity.BatterySample
import com.fast.batteryx.domain.repository.BatterySampleRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

class BatterySampleRepositoryImpl @Inject constructor(
    private val dao: BatterySampleDao
) : BatterySampleRepository {

    override fun getAllSamples(): Flow<List<BatterySample>> =
        dao.getSamplesByTimeRange(LocalDateTime.MIN, LocalDateTime.now())

    override fun getSamplesSince(since: LocalDateTime): Flow<List<BatterySample>> =
        dao.getSamplesByTimeRange(since, LocalDateTime.now())

    override fun getRecentSamples(limit: Int): Flow<List<BatterySample>> =
        dao.getSamplesByTimeRange(LocalDateTime.now().minusDays(7), LocalDateTime.now())

    override suspend fun insertSample(sample: BatterySample) = dao.insert(sample)

    override suspend fun insertSamples(samples: List<BatterySample>) = dao.insertAll(samples)

    override suspend fun deleteOldSamples(before: LocalDateTime) = dao.deleteOlderThan(before)

    override fun getCurrentBattery(): Flow<BatterySample?> = dao.getLatestSample()
}
