package com.location.vitalflow.domain.model

data class SleepLog(
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long?,
    val isAutomatic: Boolean,
    val type: String // "SLEEP" or "NAP"
)
