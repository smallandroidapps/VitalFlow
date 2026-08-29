package com.location.vitalflow.data.repository

import com.location.vitalflow.data.database.WaterDao
import com.location.vitalflow.data.database.WaterLogEntity
import com.location.vitalflow.domain.model.WaterLog
import com.location.vitalflow.domain.repository.WaterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WaterRepositoryImpl @Inject constructor(
    private val waterDao: WaterDao
) : WaterRepository {
    override fun getDailyWaterLogs(): Flow<List<WaterLog>> {
        return waterDao.getAllWaterLogs().map { entities ->
            entities.map { WaterLog(it.id, it.amountMl, it.timestamp) }
        }
    }

    override suspend fun logWater(amountMl: Int) {
        waterDao.insertWaterLog(WaterLogEntity(amountMl = amountMl))
    }

    override suspend fun updateLog(log: WaterLog) {
        waterDao.updateWaterLog(WaterLogEntity(id = log.id, amountMl = log.amountMl, timestamp = log.timestamp))
    }

    override suspend fun deleteLog(log: WaterLog) {
        waterDao.deleteWaterLog(WaterLogEntity(id = log.id, amountMl = log.amountMl, timestamp = log.timestamp))
    }

    override suspend fun deleteLogById(id: Int) {
        waterDao.deleteWaterLogById(id)
    }
}
