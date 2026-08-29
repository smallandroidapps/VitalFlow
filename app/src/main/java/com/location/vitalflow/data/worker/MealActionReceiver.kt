package com.location.vitalflow.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.location.vitalflow.data.database.MealLogEntity
import com.location.vitalflow.data.database.VitalFlowDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MealActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: VitalFlowDatabase

    override fun onReceive(context: Context, intent: Intent) {
        val mealType = intent.getStringExtra("MEAL_TYPE") ?: "Meal"
        val action = intent.action
        
        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                "ACTION_ATE" -> {
                    database.mealDao().insertMealLog(
                        MealLogEntity(mealType = mealType, status = "ATE")
                    )
                }
                "ACTION_SKIP" -> {
                    database.mealDao().insertMealLog(
                        MealLogEntity(mealType = mealType, status = "SKIPPED")
                    )
                }
                "ACTION_SNOOZE" -> {
                    // In a real app, reschedule the WorkManager task for +15m
                }
            }
        }
        
        // Dismiss notification
        val manager = context.getSystemService(android.app.NotificationManager::class.java)
        manager.cancel(200) // MEAL_NOTIFICATION_ID
    }
}
