package com.fast.batteryx.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fast.batteryx.data.entity.HealthHistory
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.HealthHistoryRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDate

class HealthSnapshotWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HealthSnapshotWorkerEntryPoint {
        fun batteryCollector(): BatteryDataCollector
        fun healthRepository(): HealthHistoryRepository
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                HealthSnapshotWorkerEntryPoint::class.java
            )
            val collector = entryPoint.batteryCollector()
            val repository = entryPoint.healthRepository()

            val designCap = collector.getDesignCapacityMilliampereHours()
            val fullCap = collector.getFullChargeCapacityMilliampereHours()

            if (designCap > 0 && fullCap > 0) {
                val healthPct = (fullCap.toFloat() / designCap * 100f).coerceIn(0f, 100f)
                val wearPct = (100f - healthPct).coerceAtLeast(0f)

                val snapshot = HealthHistory(
                    estimatedCapacity = fullCap,
                    designCapacity = designCap,
                    healthPercentage = healthPct,
                    wearPercentage = wearPct,
                    date = LocalDate.now(),
                    chargeCyclesEstimate = 0f,
                    samplesAnalyzed = 0
                )
                repository.recordHealth(snapshot)
            }
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
