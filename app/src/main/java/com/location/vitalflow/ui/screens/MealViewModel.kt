package com.location.vitalflow.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.location.vitalflow.domain.model.MealLog
import com.location.vitalflow.domain.repository.MealRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MealViewModel @Inject constructor(
    private val repository: MealRepository
) : ViewModel() {

    val mealLogs = repository.getAllMealLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val regularityScore = mealLogs.map { logs ->
        if (logs.isEmpty()) "N/A"
        else {
            val today = java.util.Calendar.getInstance()
            val todayCount = logs.count { 
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = it.timestamp }
                cal.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) &&
                cal.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
            }
            if (todayCount >= 3) "Excellent" else "Needs Work"
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Calculating...")

    val dailyCalories = mealLogs.map { logs ->
        logs.filter { isToday(it.timestamp) }.sumOf { it.calories ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val dailyProtein = mealLogs.map { logs ->
        logs.filter { isToday(it.timestamp) }.sumOf { it.protein ?: 0 }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private fun isToday(timestamp: Long): Boolean {
        val today = java.util.Calendar.getInstance()
        val date = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
        return today.get(java.util.Calendar.YEAR) == today.get(java.util.Calendar.YEAR) &&
                today.get(java.util.Calendar.DAY_OF_YEAR) == today.get(java.util.Calendar.DAY_OF_YEAR)
    }

    fun logMeal(mealType: String, status: String, calories: Int? = null, protein: Int? = null) {
        viewModelScope.launch {
            repository.insertMealLog(
                MealLog(
                    mealType = mealType,
                    status = status,
                    timestamp = System.currentTimeMillis(),
                    calories = calories,
                    protein = protein
                )
            )
        }
    }

    fun updateMeal(log: MealLog) {
        viewModelScope.launch {
            repository.updateMealLog(log)
        }
    }

    fun deleteMeal(log: MealLog) {
        viewModelScope.launch {
            repository.deleteMealLog(log)
        }
    }
}
