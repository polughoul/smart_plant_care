package com.example.smart_plant_care.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.smart_plant_care.data.local.entity.MyPlantEntity

object PlantReminderScheduler {

    const val CHANNEL_ID = "watering_reminders"
    const val CHANNEL_NAME = "Watering reminders"
    const val ACTION_REMINDER_TRIGGER = "com.example.smart_plant_care.action.REMINDER_TRIGGER"
    const val ACTION_MARK_WATERED = "com.example.smart_plant_care.action.MARK_WATERED"

    private const val EXTRA_PLANT_ID = "extra_plant_id"
    private const val EXTRA_PLANT_NAME = "extra_plant_name"
    private const val MARK_AS_WATERED_REQUEST_OFFSET = 100_000

    fun createNotificationChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notifications for plant watering time"
        }
        manager.createNotificationChannel(channel)
    }

    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun scheduleReminder(context: Context, plant: MyPlantEntity) {
        if (plant.id <= 0) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, plant.id, plant.customName)
        val triggerAtMillis = plant.nextWateringDate.coerceAtLeast(System.currentTimeMillis() + 5_000L)

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context, plantId: Int) {
        if (plantId <= 0) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildPendingIntent(context, plantId, null)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        val markWateredIntent = buildMarkWateredPendingIntent(context, plantId, null)
        markWateredIntent.cancel()
    }

    fun buildMarkWateredPendingIntent(context: Context, plantId: Int, plantName: String?): PendingIntent {
        val intent = Intent(context, PlantReminderReceiver::class.java).apply {
            action = ACTION_MARK_WATERED
            putExtra(EXTRA_PLANT_ID, plantId)
            putExtra(EXTRA_PLANT_NAME, plantName)
        }
        return PendingIntent.getBroadcast(
            context,
            plantId + MARK_AS_WATERED_REQUEST_OFFSET,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildPendingIntent(context: Context, plantId: Int, plantName: String?): PendingIntent {
        val intent = Intent(context, PlantReminderReceiver::class.java).apply {
            action = ACTION_REMINDER_TRIGGER
            putExtra(EXTRA_PLANT_ID, plantId)
            putExtra(EXTRA_PLANT_NAME, plantName)
        }
        return PendingIntent.getBroadcast(
            context,
            plantId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun readPlantId(intent: Intent): Int = intent.getIntExtra(EXTRA_PLANT_ID, 0)

    fun readPlantName(intent: Intent): String = intent.getStringExtra(EXTRA_PLANT_NAME).orEmpty()

    fun readAction(intent: Intent): String = intent.action.orEmpty()
}



