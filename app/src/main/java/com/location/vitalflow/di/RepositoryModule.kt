package com.location.vitalflow.di

import com.location.vitalflow.data.repository.WaterRepositoryImpl
import com.location.vitalflow.data.repository.SleepRepositoryImpl
import com.location.vitalflow.data.repository.MealRepositoryImpl
import com.location.vitalflow.domain.repository.WaterRepository
import com.location.vitalflow.domain.repository.SleepRepository
import com.location.vitalflow.domain.repository.MealRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindWaterRepository(
        waterRepositoryImpl: WaterRepositoryImpl
    ): WaterRepository

    @Binds
    @Singleton
    abstract fun bindSleepRepository(
        sleepRepositoryImpl: SleepRepositoryImpl
    ): SleepRepository

    @Binds
    @Singleton
    abstract fun bindMealRepository(
        mealRepositoryImpl: MealRepositoryImpl
    ): MealRepository
}
