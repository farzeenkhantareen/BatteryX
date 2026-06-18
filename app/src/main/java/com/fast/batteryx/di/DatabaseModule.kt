package com.fast.batteryx.di

import android.content.Context
import com.fast.batteryx.data.database.BatteryXDatabase
import com.fast.batteryx.data.dao.*
import com.fast.batteryx.data.source.BatteryBroadcastListener
import com.fast.batteryx.data.source.BatteryDataCollector
import com.fast.batteryx.service.BatteryNotificationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideBatteryXDatabase(@ApplicationContext context: Context): BatteryXDatabase =
        BatteryXDatabase.getInstance(context)

    @Provides @Singleton
    fun provideBatterySampleDao(db: BatteryXDatabase): BatterySampleDao = db.batterySampleDao()

    @Provides @Singleton
    fun provideChargingSessionDao(db: BatteryXDatabase): ChargingSessionDao = db.chargingSessionDao()

    @Provides @Singleton
    fun provideHealthHistoryDao(db: BatteryXDatabase): HealthHistoryDao = db.healthHistoryDao()

    @Provides @Singleton
    fun provideTemperatureHistoryDao(db: BatteryXDatabase): TemperatureHistoryDao = db.temperatureHistoryDao()

    @Provides @Singleton
    fun provideUserSettingsDao(db: BatteryXDatabase): UserSettingsDao = db.userSettingsDao()

    @Provides @Singleton
    fun provideBatteryPredictionDao(db: BatteryXDatabase): BatteryPredictionDao = db.batteryPredictionDao()

    @Provides @Singleton
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides @Singleton
    fun provideBatteryBroadcastListener(@ApplicationContext context: Context): BatteryBroadcastListener =
        BatteryBroadcastListener(context)

    @Provides @Singleton
    fun provideBatteryDataCollector(@ApplicationContext context: Context): BatteryDataCollector =
        BatteryDataCollector(context)

    @Provides @Singleton
    fun provideBatteryNotificationManager(@ApplicationContext context: Context): BatteryNotificationManager =
        BatteryNotificationManager(context)
}
