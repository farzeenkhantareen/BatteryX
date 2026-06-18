package com.fast.batteryx.data.dao

import androidx.room.*
import com.fast.batteryx.data.entity.UserSettings
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for UserSettings entity.
 * Manages user preferences and configuration.
 */
@Dao
interface UserSettingsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(settings: UserSettings)

    @Update
    suspend fun update(settings: UserSettings)

    @Query("SELECT * FROM user_settings WHERE id = 0")
    fun getSettings(): Flow<UserSettings?>

    @Query("DELETE FROM user_settings")
    suspend fun clear()
}

