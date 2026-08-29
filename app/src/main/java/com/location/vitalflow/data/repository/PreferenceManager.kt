package com.location.vitalflow.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "settings")

@Singleton
class PreferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val SLEEP_START_HOUR = intPreferencesKey("sleep_start_hour")
    private val SLEEP_END_HOUR = intPreferencesKey("sleep_end_hour")
    private val USER_EMAIL = stringPreferencesKey("user_email")
    private val DAILY_WATER_GOAL = intPreferencesKey("daily_water_goal")
    private val LAST_SCREEN_OFF_TIME = intPreferencesKey("last_screen_off_time")

    val sleepStartHour: Flow<Int> = context.dataStore.data.map { it[SLEEP_START_HOUR] ?: 22 }
    val sleepEndHour: Flow<Int> = context.dataStore.data.map { it[SLEEP_END_HOUR] ?: 8 }
    val dailyWaterGoal: Flow<Int> = context.dataStore.data.map { it[DAILY_WATER_GOAL] ?: 2500 }
    val userEmail: Flow<String?> = context.dataStore.data.map { it[USER_EMAIL] }

    suspend fun setSleepWindow(start: Int, end: Int) {
        context.dataStore.edit {
            it[SLEEP_START_HOUR] = start
            it[SLEEP_END_HOUR] = end
        }
    }

    suspend fun setUserEmail(email: String?) {
        context.dataStore.edit {
            if (email == null) it.remove(USER_EMAIL)
            else it[USER_EMAIL] = email
        }
    }

    suspend fun setDailyWaterGoal(goal: Int) {
        context.dataStore.edit {
            it[DAILY_WATER_GOAL] = goal
        }
    }

    val lastScreenOffTime: Flow<Long> = context.dataStore.data.map { it[intPreferencesKey("screen_off")]?.toLong() ?: 0L }

    suspend fun setLastScreenOffTime(time: Long) {
        context.dataStore.edit {
            it[intPreferencesKey("screen_off")] = time.toInt()
        }
    }
}
