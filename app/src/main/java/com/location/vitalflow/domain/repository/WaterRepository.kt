package com.location.vitalflow.domain.repository

import com.location.vitalflow.domain.model.WaterLog
import kotlinx.coroutines.flow.Flow

interface WaterRepository {
    fun getDailyWaterLogs(): Flow<List<WaterLog>>
    suspend fun logWater(amountMl: Int)
    suspend fun updateLog(log: WaterLog)
    suspend fun deleteLog(log: WaterLog)
    suspend fun deleteLogById(id: Int)
}
