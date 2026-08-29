package com.location.vitalflow.domain.repository

import com.location.vitalflow.domain.model.MealLog
import kotlinx.coroutines.flow.Flow

interface MealRepository {
    fun getAllMealLogs(): Flow<List<MealLog>>
    suspend fun insertMealLog(log: MealLog)
    suspend fun updateMealLog(log: MealLog)
    suspend fun deleteMealLog(log: MealLog)
}
