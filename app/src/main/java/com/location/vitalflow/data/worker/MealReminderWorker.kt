package com.location.vitalflow.data.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class MealReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val mealType = inputData.getString("MEAL_TYPE") ?: "Meal"
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.showMealReminder(mealType)
        return Result.success()
    }
}
