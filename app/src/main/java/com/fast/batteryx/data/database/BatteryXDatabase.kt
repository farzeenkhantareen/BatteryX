package com.fast.batteryx.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.fast.batteryx.data.dao.*
import com.fast.batteryx.data.entity.*

/**
 * Room Database for BatteryX application.
 * Contains all entities and provides DAOs for data access.
 */
@Database(
    entities = [
        BatterySample::class,
        ChargingSession::class,
        HealthHistory::class,
        TemperatureHistory::class,
        UserSettings::class,
        BatteryPrediction::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DatabaseConverters::class)
abstract class BatteryXDatabase : RoomDatabase() {

    abstract fun batterySampleDao(): BatterySampleDao
    abstract fun chargingSessionDao(): ChargingSessionDao
    abstract fun healthHistoryDao(): HealthHistoryDao
    abstract fun temperatureHistoryDao(): TemperatureHistoryDao
    abstract fun userSettingsDao(): UserSettingsDao
    abstract fun batteryPredictionDao(): BatteryPredictionDao

    companion object {
        private const val DATABASE_NAME = "batteryx_database"

        @Volatile
        private var instance: BatteryXDatabase? = null

        fun getInstance(context: Context): BatteryXDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): BatteryXDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                BatteryXDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}

