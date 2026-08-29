package com.location.vitalflow.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.location.vitalflow.data.repository.PreferenceManager
import com.location.vitalflow.domain.model.SleepLog
import com.location.vitalflow.domain.repository.SleepRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class SleepReceiver : BroadcastReceiver() {

    @Inject
    lateinit var repository: SleepRepository
    @Inject
    lateinit var preferenceManager: PreferenceManager

    override fun onReceive(context: Context, intent: Intent) {
        val now = System.currentTimeMillis()
        val prefs = preferenceManager
        val notificationHelper = NotificationHelper(context)
        
        CoroutineScope(Dispatchers.IO).launch {
            val startHour = prefs.sleepStartHour.first()
            val endHour = prefs.sleepEndHour.first()
            val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

            when (intent.action) {
                Intent.ACTION_SCREEN_OFF -> {
                    prefs.setLastScreenOffTime(now)
                    if (isHourInWindow(currentHour, startHour, endHour)) {
                        repository.insertSleepLog(SleepLog(startTime = now, endTime = null, isAutomatic = true, type = "SLEEP"))
                    }
                }
                Intent.ACTION_SCREEN_ON -> {
                    val offTime = prefs.lastScreenOffTime.first()
                    val duration = now - offTime
                    val isDaytime = !isHourInWindow(currentHour, startHour, endHour)
                    
                    if (isDaytime && duration > 3600000) { // More than 1 hour
                        notificationHelper.showMealReminder("Detecting long inactivity. Log this as a nap?")
                    }
                }
            }
        }
    }

    private fun isHourInWindow(hour: Int, start: Int, end: Int): Boolean {
        return if (start <= end) {
            hour in start..end
        } else {
            hour >= start || hour <= end
        }
    }
}
