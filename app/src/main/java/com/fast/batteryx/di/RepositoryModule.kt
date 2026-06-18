package com.fast.batteryx.di

import com.fast.batteryx.data.repository.*
import com.fast.batteryx.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindBatterySampleRepository(impl: BatterySampleRepositoryImpl): BatterySampleRepository

    @Binds @Singleton
    abstract fun bindChargingSessionRepository(impl: ChargingSessionRepositoryImpl): ChargingSessionRepository

    @Binds @Singleton
    abstract fun bindHealthHistoryRepository(impl: HealthHistoryRepositoryImpl): HealthHistoryRepository

    @Binds @Singleton
    abstract fun bindUserSettingsRepository(impl: UserSettingsRepositoryImpl): UserSettingsRepository

    @Binds @Singleton
    abstract fun bindTemperatureHistoryRepository(impl: TemperatureHistoryRepositoryImpl): TemperatureHistoryRepository

    @Binds @Singleton
    abstract fun bindBatteryPredictionRepository(impl: BatteryPredictionRepositoryImpl): BatteryPredictionRepository
}
