package com.fast.batteryx.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity storing user settings and preferences.
 * Allows customization of alerts, thresholds, and app behavior.
 */
@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey
    val id: Int = 0, // Always 0 since we have only one settings record

    // Alert thresholds
    val chargeAlertPercentage: Int = 80, // Alert when battery reaches this %
    val temperatureAlertThreshold: Int = 45, // Alert when temp exceeds this °C
    val lowBatteryPercentage: Int = 15, // Low battery warning threshold

    // Notification settings
    val enableChargeAlert: Boolean = true,
    val enableTemperatureAlert: Boolean = true,
    val enableWearAlert: Boolean = true,
    val enableSoundNotification: Boolean = true,
    val enableVibration: Boolean = true,

    // Theme settings
    val isDarkMode: Boolean = true,
    val useDynamicColor: Boolean = true,

    // Data collection
    val samplingIntervalMinutes: Int = 10, // Collect battery data every N minutes
    val enableBackgroundCollection: Boolean = true,

    // Premium
    val isPremium: Boolean = false
)

