package com.location.vitalflow.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class NapPromptWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val notificationHelper = NotificationHelper(applicationContext)
        // Show a general prompt to log a nap if they just woke up after 1h+
        // In a real app, this would be more specific
        notificationHelper.showMealReminder("Did you take a nap? Tap to log.")
        return Result.success()
    }
}
