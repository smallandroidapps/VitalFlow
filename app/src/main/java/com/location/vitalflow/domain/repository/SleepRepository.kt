package com.location.vitalflow.domain.repository

import com.location.vitalflow.domain.model.SleepLog
import kotlinx.coroutines.flow.Flow

interface SleepRepository {
    fun getAllSleepLogs(): Flow<List<SleepLog>>
    suspend fun insertSleepLog(log: SleepLog)
    suspend fun updateSleepLog(log: SleepLog)
    suspend fun deleteSleepLog(log: SleepLog)
}
