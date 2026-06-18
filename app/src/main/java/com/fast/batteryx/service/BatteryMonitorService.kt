package com.fast.batteryx.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.fast.batteryx.data.entity.BatterySample
import com.fast.batteryx.data.entity.ChargingSession
import com.fast.batteryx.data.entity.TemperatureHistory
import com.fast.batteryx.data.source.BatteryBroadcastListener
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.domain.repository.BatterySampleRepository
import com.fast.batteryx.domain.repository.ChargingSessionRepository
import com.fast.batteryx.domain.repository.UserSettingsRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Foreground service that continuously monitors battery metrics.
 *
 * Responsibilities:
 *  - Collect battery samples every 30 seconds → Room BatterySamples table
 *  - Detect charging session start/end → Room ChargingSessions table
 *  - Fire charge alarm notification when target % reached
 *  - Fire temperature warning when temp > 45°C
 *  - Update foreground notification with live status
 */
@AndroidEntryPoint
class BatteryMonitorService : Service() {

    @Inject lateinit var batteryCollector: BatteryDataCollector
    @Inject lateinit var broadcastListener: BatteryBroadcastListener
    @Inject lateinit var sampleRepository: BatterySampleRepository
    @Inject lateinit var sessionRepository: ChargingSessionRepository
    @Inject lateinit var settingsRepository: UserSettingsRepository
    @Inject lateinit var notificationManager: BatteryNotificationManager

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var samplingJob: Job? = null

    // Charging session tracking
    private var activeSessionId: Long? = null
    private var sessionStartPercent: Int = 0
    private var sessionStartTime: LocalDateTime? = null
    private var sessionChargeAccumulated: Double = 0.0
    private var lastChargeCounter: Int = 0

    // Alert tracking (avoid repeated alerts)
    private var chargeAlarmFired = false
    private var tempWarningSent = false
    private var lastAlarmTarget = 80

    override fun onCreate() {
        super.onCreate()
        notificationManager.createChannels()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val initialNotif = notificationManager.buildForegroundNotification(
            batteryPercent = 50, isCharging = false, temperature = 25f
        )
        startForeground(BatteryNotificationManager.NOTIF_ID_FOREGROUND, initialNotif)
        startSampling()
        return START_STICKY
    }

    private fun startSampling() {
        samplingJob?.cancel()
        samplingJob = serviceScope.launch {
            while (true) {
                collectSample()
                delay(30_000L) // every 30 seconds
            }
        }
    }

    private suspend fun collectSample() {
        try {
            val broadcastData = broadcastListener.getLatestBatteryInfo()
            val current = batteryCollector.getCurrentMilliamps()
            val avgCurrent = batteryCollector.getCurrentAverageMilliamps()
            val chargeCounter = batteryCollector.getChargeMicroampereHours()
            val capacity = batteryCollector.getFullChargeCapacityMilliampereHours()

            val percent = broadcastData.level
            val tempCelsius = broadcastData.temperature / 10f
            val voltage = broadcastData.voltage
            val isCharging = broadcastData.isCharging
            val plugged = broadcastData.plugged
            val health = broadcastData.health

            // Persist battery sample
            val sample = BatterySample(
                percentage = percent,
                temperature = broadcastData.temperature,
                voltage = voltage,
                current = current,
                health = health,
                status = broadcastData.status,
                plugged = plugged,
                capacity = capacity,
                isCharging = isCharging,
                screenOn = false
            )
            sampleRepository.insertSample(sample)

            // Manage charging session
            manageChargingSession(isCharging, percent, chargeCounter, avgCurrent)

            // Update foreground notification
            val updatedNotif = notificationManager.buildForegroundNotification(
                batteryPercent = percent,
                isCharging = isCharging,
                temperature = tempCelsius
            )
            val nm = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
            nm.notify(BatteryNotificationManager.NOTIF_ID_FOREGROUND, updatedNotif)

            // Check alerts
            checkAlerts(percent, tempCelsius, isCharging)

        } catch (e: Exception) {
            // Silently ignore — service must stay alive
        }
    }

    private suspend fun manageChargingSession(
        isCharging: Boolean,
        percent: Int,
        chargeCounter: Int,
        avgCurrent: Int
    ) {
        if (isCharging && activeSessionId == null) {
            // Session started
            sessionStartPercent = percent
            sessionStartTime = LocalDateTime.now()
            lastChargeCounter = chargeCounter
            sessionChargeAccumulated = 0.0
            chargeAlarmFired = false

            val session = ChargingSession(
                startTime = sessionStartTime!!,
                endTime = null,
                startPercentage = percent,
                endPercentage = percent,
                capacityAdded = 0.0,
                averageCurrent = avgCurrent.toDouble(),
                peakCurrent = avgCurrent.toDouble(),
                chargerType = 0,
                isComplete = false
            )
            activeSessionId = sessionRepository.insertSession(session)

        } else if (!isCharging && activeSessionId != null) {
            // Session ended
            val sid = activeSessionId!!
            sessionRepository.updateSessionEnd(
                sessionId = sid,
                endTime = LocalDateTime.now(),
                endPercent = percent,
                chargeAddedMah = sessionChargeAccumulated,
                averageCurrentMa = avgCurrent.toDouble()
            )
            activeSessionId = null
            sessionChargeAccumulated = 0.0

        } else if (isCharging && activeSessionId != null) {
            // Accumulate charge added
            val delta = chargeCounter - lastChargeCounter
            if (delta > 0) {
                sessionChargeAccumulated += delta / 1000.0 // μAh → mAh
            }
            lastChargeCounter = chargeCounter
        }
    }

    private suspend fun checkAlerts(percent: Int, tempCelsius: Float, isCharging: Boolean) {
        val settings = settingsRepository.getSettings().first()

        // Charge alarm
        val alarmTarget = settings?.chargeAlertPercentage ?: 80
        if (isCharging && percent >= alarmTarget && (!chargeAlarmFired || lastAlarmTarget != alarmTarget)) {
            notificationManager.sendChargeAlarmNotification(alarmTarget)
            chargeAlarmFired = true
            lastAlarmTarget = alarmTarget
        }
        if (!isCharging) chargeAlarmFired = false

        // Temperature warning (>45°C)
        if (tempCelsius > 45f && !tempWarningSent) {
            notificationManager.sendTemperatureWarning(tempCelsius)
            tempWarningSent = true
        }
        if (tempCelsius < 43f) tempWarningSent = false
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
