package com.location.vitalflow.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllWaterLogs(): Flow<List<WaterLogEntity>>

    @Insert
    suspend fun insertWaterLog(log: WaterLogEntity)

    @Update
    suspend fun updateWaterLog(log: WaterLogEntity)

    @Delete
    suspend fun deleteWaterLog(log: WaterLogEntity)

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLogById(id: Int)
}

@Dao
interface SleepDao {
    @Query("SELECT * FROM sleep_logs ORDER BY startTime DESC")
    fun getAllSleepLogs(): Flow<List<SleepLogEntity>>

    @Insert
    suspend fun insertSleepLog(log: SleepLogEntity)

    @Update
    suspend fun updateSleepLog(log: SleepLogEntity)

    @Delete
    suspend fun deleteSleepLog(log: SleepLogEntity)
}

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMealLogs(): Flow<List<MealLogEntity>>

    @Insert
    suspend fun insertMealLog(log: MealLogEntity)

    @Update
    suspend fun updateMealLog(log: MealLogEntity)

    @Delete
    suspend fun deleteMealLog(log: MealLogEntity)
}
