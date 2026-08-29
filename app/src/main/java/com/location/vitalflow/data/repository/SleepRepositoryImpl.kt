package com.location.vitalflow.data.repository

import com.location.vitalflow.data.database.SleepDao
import com.location.vitalflow.data.database.SleepLogEntity
import com.location.vitalflow.domain.model.SleepLog
import com.location.vitalflow.domain.repository.SleepRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SleepRepositoryImpl @Inject constructor(
    private val sleepDao: SleepDao
) : SleepRepository {
    override fun getAllSleepLogs(): Flow<List<SleepLog>> {
        return sleepDao.getAllSleepLogs().map { entities ->
            entities.map { SleepLog(it.id, it.startTime, it.endTime, it.isAutomatic, it.type) }
        }
    }

    override suspend fun insertSleepLog(log: SleepLog) {
        sleepDao.insertSleepLog(SleepLogEntity(startTime = log.startTime, endTime = log.endTime, isAutomatic = log.isAutomatic, type = log.type))
    }

    override suspend fun updateSleepLog(log: SleepLog) {
        sleepDao.updateSleepLog(SleepLogEntity(id = log.id, startTime = log.startTime, endTime = log.endTime, isAutomatic = log.isAutomatic, type = log.type))
    }

    override suspend fun deleteSleepLog(log: SleepLog) {
        sleepDao.deleteSleepLog(SleepLogEntity(id = log.id, startTime = log.startTime, endTime = log.endTime, isAutomatic = log.isAutomatic, type = log.type))
    }
}
