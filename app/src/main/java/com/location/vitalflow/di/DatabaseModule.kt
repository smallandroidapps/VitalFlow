package com.location.vitalflow.di

import android.content.Context
import androidx.room.Room
import com.location.vitalflow.data.database.VitalFlowDatabase
import com.location.vitalflow.data.database.WaterDao
import com.location.vitalflow.data.database.SleepDao
import com.location.vitalflow.data.database.MealDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): VitalFlowDatabase {
        return Room.databaseBuilder(
            context,
            VitalFlowDatabase::class.java,
            "vitalflow_db"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideWaterDao(database: VitalFlowDatabase): WaterDao = database.waterDao()

    @Provides
    fun provideSleepDao(database: VitalFlowDatabase): SleepDao = database.sleepDao()

    @Provides
    fun provideMealDao(database: VitalFlowDatabase): MealDao = database.mealDao()
}
