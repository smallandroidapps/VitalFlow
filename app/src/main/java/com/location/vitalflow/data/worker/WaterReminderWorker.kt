package com.location.vitalflow.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class WaterReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showWaterReminder()
        return Result.success()
    }
}
