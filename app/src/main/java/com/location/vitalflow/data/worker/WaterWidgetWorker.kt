package com.location.vitalflow.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.location.vitalflow.data.database.WaterLogEntity
import com.location.vitalflow.data.database.VitalFlowDatabase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class WaterWidgetWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WaterWidgetWorkerEntryPoint {
        fun database(): VitalFlowDatabase
    }

    override suspend fun doWork(): Result {
        val amount = inputData.getInt("AMOUNT", 250)
        
        val entryPoint = EntryPointAccessors.fromApplication(
            applicationContext,
            WaterWidgetWorkerEntryPoint::class.java
        )
        val database = entryPoint.database()
        
        database.waterDao().insertWaterLog(WaterLogEntity(amountMl = amount))
        
        return Result.success()
    }
}
