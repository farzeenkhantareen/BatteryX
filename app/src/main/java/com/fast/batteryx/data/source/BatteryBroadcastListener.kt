package com.fast.batteryx.data.source

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Data class representing battery broadcast information.
 */
data class BatteryBroadcastInfo(
    val level: Int, // 0-100
    val scale: Int,
    val status: Int, // BatteryManager.BATTERY_STATUS_*
    val health: Int, // BatteryManager.BATTERY_HEALTH_*
    val temperature: Int, // Tenths of a degree Celsius (divide by 10 for °C)
    val voltage: Int, // Millivolts
    val plugged: Int, // BatteryManager.BATTERY_PLUGGED_*
    val timestamp: LocalDateTime = LocalDateTime.now()
) {
    val percentage: Int get() = if (scale > 0) (level * 100) / scale else level
    val isCharging: Boolean get() =
        status == BatteryManager.BATTERY_STATUS_CHARGING ||
        status == BatteryManager.BATTERY_STATUS_FULL

    fun getChargerType(): String = when {
        (plugged and BatteryManager.BATTERY_PLUGGED_AC) != 0 -> "AC Adapter"
        (plugged and BatteryManager.BATTERY_PLUGGED_USB) != 0 -> "USB"
        (plugged and BatteryManager.BATTERY_PLUGGED_WIRELESS) != 0 -> "Wireless"
        else -> "Unknown"
    }
}

/**
 * Listens to battery state changes via broadcast receiver.
 * Also provides one-shot current battery info lookup.
 */
class BatteryBroadcastListener @Inject constructor(private val context: Context) {

    /** One-shot: returns the current battery status without registering a persistent listener */
    fun getLatestBatteryInfo(): BatteryBroadcastInfo {
        val intent = context.registerReceiver(
            null,
            IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        ) ?: return BatteryBroadcastInfo(0, 100, 0, 0, 250, 0, 0)

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
        val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250)
        val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
        val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

        return BatteryBroadcastInfo(
            level = level,
            scale = scale,
            status = status,
            health = health,
            temperature = temperature,
            voltage = voltage,
            plugged = plugged
        )
    }

    /** Continuous flow: emits on every battery change broadcast */
    fun observeBatteryChanges(): Flow<BatteryBroadcastInfo> = callbackFlow {
        // Emit current state immediately
        trySend(getLatestBatteryInfo())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN)
                    val health = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
                    val temperature = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 250)
                    val voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
                    val plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

                    trySend(
                        BatteryBroadcastInfo(
                            level = level,
                            scale = scale,
                            status = status,
                            health = health,
                            temperature = temperature,
                            voltage = voltage,
                            plugged = plugged
                        )
                    )
                }
            }
        }

        val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(receiver, intentFilter)
        }

        awaitClose {
            try { context.unregisterReceiver(receiver) } catch (_: Exception) {}
        }
    }
}
