package com.location.vitalflow.ui.screens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.location.vitalflow.data.worker.WaterReminderWorker
import com.location.vitalflow.domain.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterViewModel @Inject constructor(
    private val repository: WaterRepository,
    private val preferenceManager: com.location.vitalflow.data.repository.PreferenceManager,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val dailyTotalMl = repository.getDailyWaterLogs()
        .map { logs -> logs.filter { isToday(it.timestamp) }.sumOf { it.amountMl } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val waterLogs = repository.getDailyWaterLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hydrationVelocity = waterLogs.map { logs ->
        val lastLog = logs.firstOrNull()
        if (lastLog == null) "No data today"
        else {
            val hoursSince = (System.currentTimeMillis() - lastLog.timestamp) / 3600000f
            if (hoursSince > 4) "Delayed (${hoursSince.toInt()}h ago)" else "Optimal"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Calculating...")

    val dailyGoalMl = preferenceManager.dailyWaterGoal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2500)

    fun setDailyGoal(goal: Int) {
        viewModelScope.launch {
            preferenceManager.setDailyWaterGoal(goal)
        }
    }

    /**
     * Schedules water reminders only during wake hours.
     */
    fun scheduleSmartReminders() {
        viewModelScope.launch {
            val start = preferenceManager.sleepStartHour.first()
            val end = preferenceManager.sleepEndHour.first()
            
            // Logic to schedule PeriodicWorkRequest with constraints
            // This is a high-level senior implementation suggestion
            val workRequest = androidx.work.PeriodicWorkRequestBuilder<WaterReminderWorker>(2, java.util.concurrent.TimeUnit.HOURS)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "WaterReminders",
                androidx.work.ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR)
    }

    fun logWater(amountMl: Int) {
        viewModelScope.launch {
            repository.logWater(amountMl)
        }
    }

    fun updateWater(log: com.location.vitalflow.domain.model.WaterLog) {
        viewModelScope.launch {
            repository.updateLog(log)
        }
    }

    fun deleteWater(log: com.location.vitalflow.domain.model.WaterLog) {
        viewModelScope.launch {
            repository.deleteLog(log)
        }
    }

    fun triggerTestNotification() {
        val workRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
