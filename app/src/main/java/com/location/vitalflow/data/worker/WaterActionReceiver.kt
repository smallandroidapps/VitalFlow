package com.location.vitalflow.data.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.location.vitalflow.data.database.WaterLogEntity
import com.location.vitalflow.data.database.VitalFlowDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WaterActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var database: VitalFlowDatabase

    override fun onReceive(context: Context, intent: Intent) {
        val amount = when (intent.action) {
            "LOG_100ML" -> 100
            "LOG_250ML" -> 250
            else -> 0
        }

        if (amount > 0) {
            CoroutineScope(Dispatchers.IO).launch {
                database.waterDao().insertWaterLog(
                    WaterLogEntity(amountMl = amount)
                )
            }
            Toast.makeText(context, "Logged ${amount}ml water", Toast.LENGTH_SHORT).show()
        }
    }
}
