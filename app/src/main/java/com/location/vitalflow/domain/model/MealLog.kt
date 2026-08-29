package com.location.vitalflow.domain.model

data class MealLog(
    val id: Int = 0,
    val mealType: String,
    val status: String,
    val timestamp: Long,
    val calories: Int? = null,
    val protein: Int? = null
)
