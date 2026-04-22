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
import com.example.smart_plant_care.data.local.db.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantReminderReceiver : BroadcastReceiver() {

	@SuppressLint("MissingPermission")
	override fun onReceive(context: Context, intent: Intent) {
		val plantId = PlantReminderScheduler.readPlantId(intent)
		if (plantId <= 0) return
		val action = PlantReminderScheduler.readAction(intent)

		val pendingResult = goAsync()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				val plantDao = AppDatabase.getDatabase(context).plantDao()
				val plant = plantDao.getPlantById(plantId) ?: return@launch

				val nextWateringDate = calculateNextWateringDate(plant.waterIntervalDays)
				plantDao.updateNextWateringDateById(plantId, nextWateringDate)
				val updatedPlant = plant.copy(nextWateringDate = nextWateringDate)
				PlantReminderScheduler.scheduleReminder(context, updatedPlant)

				if (action == PlantReminderScheduler.ACTION_MARK_WATERED) {
					NotificationManagerCompat.from(context).cancel(plantId)
					return@launch
				}

				if (!PlantReminderScheduler.hasNotificationPermission(context)) return@launch

				PlantReminderScheduler.createNotificationChannel(context)

				val plantName = PlantReminderScheduler.readPlantName(intent).ifBlank { plant.customName }
				showReminderNotification(context, plantId, plantName)
			} finally {
				pendingResult.finish()
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun showReminderNotification(context: Context, plantId: Int, plantName: String) {
		val openAppIntent = Intent(context, MainActivity::class.java)
		val contentIntent = PendingIntent.getActivity(
			context,
			plantId,
			openAppIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val markWateredIntent = PlantReminderScheduler.buildMarkWateredPendingIntent(
			context = context,
			plantId = plantId,
			plantName = plantName
		)

		val notification = NotificationCompat.Builder(context, PlantReminderScheduler.CHANNEL_ID)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle("Watering reminder")
			.setContentText("Time to water $plantName")
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setContentIntent(contentIntent)
			.addAction(android.R.drawable.ic_menu_edit, "Mark as watered", markWateredIntent)
			.build()

		NotificationManagerCompat.from(context).notify(plantId, notification)
	}

	private fun calculateNextWateringDate(waterIntervalDays: Int): Long {
		val days = waterIntervalDays.coerceAtLeast(1)
		return System.currentTimeMillis() + days * 24L * 60L * 60L * 1000L
	}
}


