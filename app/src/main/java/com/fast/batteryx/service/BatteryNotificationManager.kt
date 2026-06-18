package com.fast.batteryx.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.fast.batteryx.MainActivity
import com.fast.batteryx.R
import com.fast.batteryx.ui.theme.batteryColor
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages all notification channels and sends battery-specific alerts.
 * Channels created once at app start via BatteryXApplication.
 */
@Singleton
class BatteryNotificationManager @Inject constructor(
    private val context: Context
) {

    companion object {
        const val CHANNEL_MONITOR = "battery_monitor"
        const val CHANNEL_ALERTS = "battery_alerts"
        const val CHANNEL_CHARGE = "charge_alarm"

        const val NOTIF_ID_FOREGROUND = 1001
        const val NOTIF_ID_CHARGE_ALARM = 1002
        const val NOTIF_ID_TEMP_WARNING = 1003
        const val NOTIF_ID_WEAR_ALERT = 1004
        const val NOTIF_ID_EFFICIENCY = 1005
    }

    private val notifManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    /** Call once from Application.onCreate() */
    fun createChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val monitorChannel = NotificationChannel(
            CHANNEL_MONITOR,
            "Battery Monitor",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Persistent battery status notification"
            setShowBadge(false)
        }

        val alertsChannel = NotificationChannel(
            CHANNEL_ALERTS,
            "Battery Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Temperature warnings, wear alerts"
            enableVibration(true)
        }

        val chargeChannel = NotificationChannel(
            CHANNEL_CHARGE,
            "Charge Alarm",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alert when battery reaches your target level"
            enableVibration(true)
        }

        notifManager.createNotificationChannels(
            listOf(monitorChannel, alertsChannel, chargeChannel)
        )
    }

    /** Build the persistent foreground notification shown while service runs */
    fun buildForegroundNotification(
        batteryPercent: Int,
        isCharging: Boolean,
        temperature: Float
    ) = NotificationCompat.Builder(context, CHANNEL_MONITOR)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentTitle(
            if (isCharging) "Charging · $batteryPercent%" else "Battery · $batteryPercent%"
        )
        .setContentText(buildStatusText(isCharging, temperature, batteryPercent))
        .setOngoing(true)
        .setSilent(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(launchIntent())
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .build()

    /** Charge alarm: battery reached user's target % */
    fun sendChargeAlarmNotification(targetPercent: Int) {
        val n = NotificationCompat.Builder(context, CHANNEL_CHARGE)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚡ Charge Target Reached!")
            .setContentText("Battery has reached $targetPercent%. Unplug to protect battery health.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .setContentIntent(launchIntent())
            .build()
        notifManager.notify(NOTIF_ID_CHARGE_ALARM, n)
    }

    /** Temperature warning */
    fun sendTemperatureWarning(tempCelsius: Float) {
        val n = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🌡️ High Battery Temperature")
            .setContentText("Battery is at ${tempCelsius.toInt()}°C. Stop charging and let it cool.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .setContentIntent(launchIntent())
            .build()
        notifManager.notify(NOTIF_ID_TEMP_WARNING, n)
    }

    /** Battery wear / degradation alert */
    fun sendWearAlert(healthPercent: Float) {
        val n = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🔋 Battery Wear Detected")
            .setContentText("Health is now ${healthPercent.toInt()}%. Consider your charging habits.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(launchIntent())
            .build()
        notifManager.notify(NOTIF_ID_WEAR_ALERT, n)
    }

    /** Charging efficiency degraded */
    fun sendChargingEfficiencyAlert(currentMa: Int) {
        val n = NotificationCompat.Builder(context, CHANNEL_ALERTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ Slow Charging Detected")
            .setContentText("Only ${currentMa}mA — try a different cable or charger.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(launchIntent())
            .build()
        notifManager.notify(NOTIF_ID_EFFICIENCY, n)
    }

    private fun buildStatusText(
        isCharging: Boolean,
        temperature: Float,
        percent: Int
    ): String {
        val temp = "${temperature.toInt()}°C"
        return if (isCharging) "Charging · $temp" else "$percent% · $temp"
    }

    private fun launchIntent(): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
