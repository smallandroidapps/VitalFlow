package com.location.vitalflow.data.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.location.vitalflow.MainActivity

class NotificationHelper(private val context: Context) {

    companion object {
        const val WATER_CHANNEL_ID = "water_reminders"
        const val MEAL_CHANNEL_ID = "meal_reminders"
        const val WATER_NOTIFICATION_ID = 101
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val waterChannel = NotificationChannel(
                WATER_CHANNEL_ID,
                "Water Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Timely reminders to drink water"
            }

            val mealChannel = NotificationChannel(
                MEAL_CHANNEL_ID,
                "Meal Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for your meal schedule"
            }

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(waterChannel)
            manager.createNotificationChannel(mealChannel)
        }
    }

    fun showWaterReminder() {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        // Quick Actions
        val log100Intent = Intent(context, WaterActionReceiver::class.java).apply {
            action = "LOG_100ML"
        }
        val log100PendingIntent = PendingIntent.getBroadcast(
            context, 1, log100Intent, PendingIntent.FLAG_IMMUTABLE
        )

        val log250Intent = Intent(context, WaterActionReceiver::class.java).apply {
            action = "LOG_250ML"
        }
        val log250PendingIntent = PendingIntent.getBroadcast(
            context, 2, log250Intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, WATER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_help)
            .setContentTitle("Stay Hydrated!")
            .setContentText("It's time for a glass of water. How much did you drink?")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(0, "100ml", log100PendingIntent)
            .addAction(0, "250ml", log250PendingIntent)

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(WATER_NOTIFICATION_ID, builder.build())
    }

    fun showMealReminder(mealType: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, MEAL_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_set_as)
            .setContentTitle("Meal Time: $mealType")
            .setContentText("Don't forget to have your $mealType!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(200, builder.build())
    }
}
