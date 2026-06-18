package com.fast.batteryx.domain.repository

import com.fast.batteryx.data.entity.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user settings and preferences.
 */
interface UserSettingsRepository {

    /**
     * Get current user settings
     */
    fun getSettings(): Flow<UserSettings?>

    /**
     * Update user settings
     */
    suspend fun updateSettings(settings: UserSettings)

    /**
     * Reset settings to defaults
     */
    suspend fun resetToDefaults()
}

