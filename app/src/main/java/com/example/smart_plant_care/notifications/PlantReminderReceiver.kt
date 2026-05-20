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
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import com.example.smart_plant_care.util.calculateNextWateringDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantReminderReceiver : BroadcastReceiver() {

	@SuppressLint("MissingPermission")
	override fun onReceive(context: Context, intent: Intent) {
		val plantId = PlantReminderScheduler.readPlantId(intent)
		if (plantId <= 0) return
		val action = PlantReminderScheduler.readAction(intent)
		if (
			action != PlantReminderScheduler.ACTION_REMINDER_TRIGGER &&
			action != PlantReminderScheduler.ACTION_MARK_WATERED
		) return

		val pendingResult = goAsync()
		CoroutineScope(Dispatchers.IO).launch {
			try {
				val database = AppDatabase.getDatabase(context)
				val plantDao = database.plantDao()
				val wateringEventDao = database.wateringEventDao()
				when (action) {
					PlantReminderScheduler.ACTION_MARK_WATERED -> {
						val plant = plantDao.getPlantById(plantId)
						if (plant != null) {
							val nextWateringDate = calculateNextWateringDate(plant.waterIntervalDays)
							plantDao.updateNextWateringDateById(plantId, nextWateringDate)
							wateringEventDao.insertEvent(
								WateringEventEntity(plantId = plantId, wateredAt = System.currentTimeMillis())
							)
							val updatedPlant = plant.copy(nextWateringDate = nextWateringDate)
							PlantReminderScheduler.scheduleReminder(context, updatedPlant)
						}
						NotificationManagerCompat.from(context).cancel(plantId)
					}

					PlantReminderScheduler.ACTION_REMINDER_TRIGGER -> {
						val plant = plantDao.getPlantById(plantId) ?: return@launch
						if (!PlantReminderScheduler.hasNotificationPermission(context)) return@launch

						PlantReminderScheduler.createNotificationChannel(context)
						val plantName = PlantReminderScheduler.readPlantName(intent).ifBlank { plant.customName }
						showReminderNotification(context, plantId, plantName)
					}
				}
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
			.setContentTitle(context.getString(R.string.notification_title_watering_reminder))
			.setContentText(context.getString(R.string.notification_text_time_to_water, plantName))
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setContentIntent(contentIntent)
			.addAction(
				android.R.drawable.ic_menu_edit,
				context.getString(R.string.notification_action_mark_watered),
				markWateredIntent
			)
			.build()

		NotificationManagerCompat.from(context).notify(plantId, notification)
	}
}
