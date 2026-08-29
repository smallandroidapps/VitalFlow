package com.location.vitalflow.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.location.vitalflow.domain.model.SleepLog
import com.location.vitalflow.domain.repository.SleepRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.location.vitalflow.data.repository.PreferenceManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SleepViewModel @Inject constructor(
    private val repository: SleepRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    val sleepLogs = repository.getAllSleepLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sleepDebt = sleepLogs.map { logs ->
        val weekAgo = System.currentTimeMillis() - 7 * 24 * 3600000L
        val weekLogs = logs.filter { it.startTime > weekAgo && it.endTime != null }
        val totalHours = weekLogs.sumOf { (it.endTime!! - it.startTime) / 3600000.0 }
        val targetHours = 7 * 8.0 // 8 hours per day goal
        val debt = targetHours - totalHours
        if (debt > 0) "${debt.toInt()}h behind" else "Well Rested"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Calculating...")

    val sleepStartHour = preferenceManager.sleepStartHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 22)

    val sleepEndHour = preferenceManager.sleepEndHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 8)

    fun setSleepWindow(start: Int, end: Int) {
        viewModelScope.launch {
            preferenceManager.setSleepWindow(start, end)
        }
    }

    fun logSleep(startTime: Long, endTime: Long?, isAutomatic: Boolean, type: String) {
        viewModelScope.launch {
            repository.insertSleepLog(SleepLog(startTime = startTime, endTime = endTime, isAutomatic = isAutomatic, type = type))
        }
    }

    fun logCustomNap(durationMinutes: Int) {
        val now = System.currentTimeMillis()
        logSleep(now - durationMinutes * 60000, now, false, "NAP")
    }

    fun updateSleep(log: SleepLog) {
        viewModelScope.launch {
            repository.updateSleepLog(log)
        }
    }

    fun deleteSleep(log: SleepLog) {
        viewModelScope.launch {
            repository.deleteSleepLog(log)
        }
    }
}
