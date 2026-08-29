package com.location.vitalflow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [WaterLogEntity::class, SleepLogEntity::class, MealLogEntity::class],
    version = 2,
    exportSchema = false
)
abstract class VitalFlowDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun sleepDao(): SleepDao
    abstract fun mealDao(): MealDao
}
