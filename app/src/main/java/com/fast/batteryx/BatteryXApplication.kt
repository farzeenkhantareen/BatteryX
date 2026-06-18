package com.fast.batteryx

import android.app.Application
import com.fast.batteryx.service.BatteryNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class for BatteryX - initializes Hilt dependency injection
 * and sets up global application configuration.
 */
@HiltAndroidApp
class BatteryXApplication : Application() {

    @Inject
    lateinit var notificationManager: BatteryNotificationManager

    override fun onCreate() {
        super.onCreate()
        // Initialize notification channels at application start
        notificationManager.createChannels()

        // Schedule background database workers
        scheduleBackgroundWorkers()
    }

    private fun scheduleBackgroundWorkers() {
        val workManager = androidx.work.WorkManager.getInstance(this)

        val healthWorkRequest = androidx.work.PeriodicWorkRequestBuilder<com.fast.batteryx.worker.HealthSnapshotWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "HealthSnapshotWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            healthWorkRequest
        )

        val cleanupWorkRequest = androidx.work.PeriodicWorkRequestBuilder<com.fast.batteryx.worker.ChargingSessionCleanupWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        ).build()

        workManager.enqueueUniquePeriodicWork(
            "ChargingSessionCleanupWorker",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            cleanupWorkRequest
        )
    }
}
