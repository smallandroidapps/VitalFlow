package com.location.vitalflow.data.repository

import com.location.vitalflow.data.database.MealDao
import com.location.vitalflow.data.database.MealLogEntity
import com.location.vitalflow.domain.model.MealLog
import com.location.vitalflow.domain.repository.MealRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MealRepositoryImpl @Inject constructor(
    private val mealDao: MealDao
) : MealRepository {
    override fun getAllMealLogs(): Flow<List<MealLog>> {
        return mealDao.getAllMealLogs().map { entities ->
            entities.map { MealLog(it.id, it.mealType, it.status, it.timestamp, it.calories, it.protein) }
        }
    }

    override suspend fun insertMealLog(log: MealLog) {
        mealDao.insertMealLog(MealLogEntity(mealType = log.mealType, status = log.status, timestamp = log.timestamp, calories = log.calories, protein = log.protein))
    }

    override suspend fun updateMealLog(log: MealLog) {
        mealDao.updateMealLog(MealLogEntity(id = log.id, mealType = log.mealType, status = log.status, timestamp = log.timestamp, calories = log.calories, protein = log.protein))
    }

    override suspend fun deleteMealLog(log: MealLog) {
        mealDao.deleteMealLog(MealLogEntity(id = log.id, mealType = log.mealType, status = log.status, timestamp = log.timestamp, calories = log.calories, protein = log.protein))
    }
}
