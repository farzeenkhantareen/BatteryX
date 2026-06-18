package com.fast.batteryx.data.source

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Collects real-time battery information using BatteryManager.
 * Provides access to detailed battery metrics through privileged APIs.
 */
class BatteryDataCollector @Inject constructor(
    private val context: Context
) {

    private val batteryManager: BatteryManager? by lazy {
        context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
    }

    /**
     * Get current battery charge in microampere-hours (μAh)
     */
    suspend fun getChargeMicroampereHours(): Int = withContext(Dispatchers.Default) {
        try {
            batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get current average current in milliamps (mA)
     */
    suspend fun getCurrentAverageMilliamps(): Int = withContext(Dispatchers.Default) {
        try {
            val microAmps = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_AVERAGE) ?: 0
            microAmps / 1000
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get instantaneous current in milliamps (mA)
     */
    suspend fun getCurrentMilliamps(): Int = withContext(Dispatchers.Default) {
        try {
            val microAmps = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) ?: 0
            microAmps / 1000
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get full battery capacity in milliampere-hours (mAh)
     */
    suspend fun getFullChargeCapacityMilliampereHours(): Int = withContext(Dispatchers.Default) {
        try {
            // OEM specific sys files fallback
            listOf(
                "/sys/class/power_supply/battery/charge_full",
                "/sys/class/power_supply/battery/charge_full_design"
            ).forEach { path ->
                try {
                    val file = java.io.File(path)
                    if (file.exists()) {
                        val content = file.readText().trim()
                        val value = content.toIntOrNull()
                        if (value != null && value > 0) {
                            return@withContext if (value > 50000) value / 1000 else value
                        }
                    }
                } catch (ignored: Exception) {}
            }

            // Estimate using charge counter & battery level
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            if (level > 0 && scale > 0) {
                val pct = (level * 100) / scale
                val chargeCounterUah = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER) ?: 0
                if (chargeCounterUah > 0 && pct > 0) {
                    val currentMcap = chargeCounterUah / 1000
                    val estimatedFull = (currentMcap * 100) / pct
                    if (estimatedFull > 0) {
                        return@withContext estimatedFull
                    }
                }
            }
            
            // Fallback to PowerProfile design capacity
            getDesignCapacityMilliampereHours()
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get design capacity in milliampere-hours (mAh)
     */
    suspend fun getDesignCapacityMilliampereHours(): Int = withContext(Dispatchers.Default) {
        try {
            val powerProfileClass = Class.forName("com.android.internal.os.PowerProfile")
            val powerProfileConstructor = powerProfileClass.getConstructor(Context::class.java)
            val powerProfile = powerProfileConstructor.newInstance(context)
            val batteryCapacity = powerProfileClass
                .getMethod("getBatteryCapacity")
                .invoke(powerProfile) as Double
            batteryCapacity.toInt()
        } catch (e: Exception) {
            // OEM specific sys files fallback
            listOf(
                "/sys/class/power_supply/battery/charge_full_design",
                "/sys/class/power_supply/battery/batt_attr_text"
            ).forEach { path ->
                try {
                    val file = java.io.File(path)
                    if (file.exists()) {
                        val content = file.readText().trim()
                        val value = content.toIntOrNull()
                        if (value != null && value > 0) {
                            return@withContext if (value > 50000) value / 1000 else value
                        }
                    }
                } catch (ignored: Exception) {}
            }
            0
        }
    }

    /**
     * Get battery temperature in decigrade Celsius (0.1°C units)
     */
    suspend fun getTemperatureDecigrade(): Int = withContext(Dispatchers.Default) {
        try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 0
        } catch (e: Exception) {
            0
        }
    }

    /**
     * Get battery voltage in millivolts (mV)
     */
    suspend fun getVoltageMillivolts(): Int = withContext(Dispatchers.Default) {
        try {
            val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            intent?.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) ?: 0
        } catch (e: Exception) {
            0
        }
    }
}

