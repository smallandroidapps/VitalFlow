package com.location.vitalflow.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val amountMl: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sleep_logs")
data class SleepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long?,
    val isAutomatic: Boolean,
    val type: String // "SLEEP" or "NAP"
)

@Entity(tableName = "meal_logs")
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealType: String, // "BREAKFAST", "LUNCH", "SNACK", "DINNER"
    val status: String, // "ATE", "SKIPPED", "SNOOZED"
    val timestamp: Long = System.currentTimeMillis(),
    val calories: Int? = null,
    val protein: Int? = null
)
