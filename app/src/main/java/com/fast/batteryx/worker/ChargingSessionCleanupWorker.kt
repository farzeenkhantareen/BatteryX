package com.fast.batteryx.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import java.time.LocalDateTime

class ChargingSessionCleanupWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface CleanupWorkerEntryPoint {
        fun sessionRepository(): ChargingSessionRepository
    }

    override suspend fun doWork(): Result {
        return try {
            val entryPoint = EntryPointAccessors.fromApplication(
                applicationContext,
                CleanupWorkerEntryPoint::class.java
            )
            val repository = entryPoint.sessionRepository()

            // Delete sessions older than 90 days
            val thresholdDate = LocalDateTime.now().minusDays(90)
            repository.deleteOldCompletedSessions(thresholdDate)

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
