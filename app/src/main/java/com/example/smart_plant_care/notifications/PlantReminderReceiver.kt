package com.example.smart_plant_care.notifications

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.smart_plant_care.MainActivity
import com.example.smart_plant_care.R

class PlantReminderReceiver : BroadcastReceiver() {

	@SuppressLint("MissingPermission")
	override fun onReceive(context: Context, intent: Intent) {
		if (!PlantReminderScheduler.hasNotificationPermission(context)) return

		PlantReminderScheduler.createNotificationChannel(context)

		val plantId = PlantReminderScheduler.readPlantId(intent)
		if (plantId <= 0) return

		val plantName = PlantReminderScheduler.readPlantName(intent).ifBlank { "Your plant" }

		val openAppIntent = Intent(context, MainActivity::class.java)
		val contentIntent = PendingIntent.getActivity(
			context,
			plantId,
			openAppIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val notification = NotificationCompat.Builder(context, PlantReminderScheduler.CHANNEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle("Watering reminder")
			.setContentText("Time to water $plantName")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setContentIntent(contentIntent)
			.build()

		NotificationManagerCompat.from(context).notify(plantId, notification)
	}
}


