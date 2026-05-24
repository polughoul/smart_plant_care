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
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.smart_plant_care.R
import java.time.ZoneId
import java.time.ZonedDateTime

object PlantReminderScheduler {

    const val CHANNEL_ID = "watering_reminders"
    const val ACTION_DAILY_REMINDER_CHECK =
        "com.example.smart_plant_care.action.DAILY_REMINDER_CHECK"
    const val ACTION_MARK_WATERED =
        "com.example.smart_plant_care.action.MARK_WATERED"

    const val DAILY_NOTIFICATION_ID = 60_000

    private const val EXTRA_PLANT_ID = "extra_plant_id"

    private const val DAILY_REMINDER_REQUEST_CODE = 50_000
    private const val MARK_AS_WATERED_REQUEST_OFFSET = 100_000

    fun createNotificationChannel(context: Context) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.notification_channel_description)
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

    fun scheduleDailyReminderCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildDailyReminderPendingIntent(context)
        val triggerAtMillis = nextDailyReminderTimeMillis()

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun scheduleDailyReminderCheckIn5Seconds(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildDailyReminderPendingIntent(context)
        val triggerAtMillis = System.currentTimeMillis() + 5_000L

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun triggerDailyReminderCheckNow(context: Context) {
        val intent = Intent(context, PlantReminderReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER_CHECK
        }
        context.sendBroadcast(intent)
    }

    fun cancelDailyReminderCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = buildDailyReminderPendingIntent(context)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()

        NotificationManagerCompat.from(context).cancel(DAILY_NOTIFICATION_ID)
    }

    fun cancelReminder(context: Context, plantId: Int) {
        if (plantId <= 0) return
        val markWateredIntent = buildMarkWateredPendingIntent(
            context = context,
            plantId = plantId,
        )

        markWateredIntent.cancel()
        NotificationManagerCompat.from(context).cancel(plantId)
    }

    fun buildMarkWateredPendingIntent(
        context: Context,
        plantId: Int
    ): PendingIntent {
        val intent = Intent(context, PlantReminderReceiver::class.java).apply {
            action = ACTION_MARK_WATERED
            putExtra(EXTRA_PLANT_ID, plantId)
        }

        return PendingIntent.getBroadcast(
            context,
            plantId + MARK_AS_WATERED_REQUEST_OFFSET,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun readPlantId(intent: Intent): Int {
        return intent.getIntExtra(EXTRA_PLANT_ID, 0)
    }

    fun readAction(intent: Intent): String {
        return intent.action.orEmpty()
    }

    private fun buildDailyReminderPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, PlantReminderReceiver::class.java).apply {
            action = ACTION_DAILY_REMINDER_CHECK
        }

        return PendingIntent.getBroadcast(
            context,
            DAILY_REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun nextDailyReminderTimeMillis(): Long {
        val zoneId = ZoneId.systemDefault()
        val now = ZonedDateTime.now(zoneId)

        var next = now
            .toLocalDate()
            .atTime(9, 0)
            .atZone(zoneId)

        if (!next.isAfter(now)) {
            next = next.plusDays(1)
        }

        return next.toInstant().toEpochMilli()
    }
}



