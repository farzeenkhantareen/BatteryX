package com.fast.batteryx.data.repository

import com.fast.batteryx.data.dao.UserSettingsDao
import com.fast.batteryx.data.entity.UserSettings
import com.fast.batteryx.domain.repository.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Implementation of UserSettingsRepository.
 */
class UserSettingsRepositoryImpl @Inject constructor(
    private val dao: UserSettingsDao
) : UserSettingsRepository {

    override fun getSettings(): Flow<UserSettings?> {
        return dao.getSettings()
    }

    override suspend fun updateSettings(settings: UserSettings) {
        dao.update(settings)
    }

    override suspend fun resetToDefaults() {
        dao.clear()
        dao.insert(UserSettings())
    }
}

