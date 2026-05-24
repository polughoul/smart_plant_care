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
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PlantReminderReceiver : BroadcastReceiver() {

	@SuppressLint("MissingPermission")
	override fun onReceive(context: Context, intent: Intent) {
		val action = PlantReminderScheduler.readAction(intent)

		if (
			action != PlantReminderScheduler.ACTION_DAILY_REMINDER_CHECK &&
			action != PlantReminderScheduler.ACTION_MARK_WATERED
		) {
			return
		}

		val pendingResult = goAsync()

		CoroutineScope(Dispatchers.IO).launch {
			try {
				val database = AppDatabase.getDatabase(context)
				val plantDao = database.plantDao()
				val wateringEventDao = database.wateringEventDao()

				when (action) {
					PlantReminderScheduler.ACTION_MARK_WATERED -> {
						val plantId = PlantReminderScheduler.readPlantId(intent)

						if (plantId <= 0) {
							return@launch
						}

						val plant = plantDao.getPlantById(plantId)

						if (plant != null) {
							val nextWateringDate = calculateNextWateringDate(
								waterIntervalDays = plant.waterIntervalDays
							)

							plantDao.updateNextWateringDateById(
								plantId = plantId,
								nextWateringDate = nextWateringDate
							)

							wateringEventDao.insertEvent(
								WateringEventEntity(
									plantId = plantId,
									wateredAt = System.currentTimeMillis()
								)
							)

							NotificationManagerCompat.from(context).cancel(plantId)
						}
					}

					PlantReminderScheduler.ACTION_DAILY_REMINDER_CHECK -> {
						if (PlantReminderScheduler.hasNotificationPermission(context)) {
							val plants = plantDao.getAllPlantsOnce()

							val duePlants = plants.filter { plant ->
								plant.nextWateringDate <= endOfTodayMillis()
							}

							if (duePlants.isNotEmpty()) {
								PlantReminderScheduler.createNotificationChannel(context)

								if (duePlants.size == 1) {
									val plant = duePlants.first()

									showSinglePlantReminderNotification(
										context = context,
										plantId = plant.id,
										plantName = plant.customName
									)
								} else {
									showDailySummaryNotification(
										context = context,
										count = duePlants.size
									)
								}
							}
						}

						PlantReminderScheduler.scheduleDailyReminderCheck(context)
					}
				}
			} finally {
				pendingResult.finish()
			}
		}
	}

	@SuppressLint("MissingPermission")
	private fun showSinglePlantReminderNotification(
		context: Context,
		plantId: Int,
		plantName: String
	) {
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
		)

		val notification = NotificationCompat.Builder(
			context,
			PlantReminderScheduler.CHANNEL_ID
		)
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

	@SuppressLint("MissingPermission")
	private fun showDailySummaryNotification(
		context: Context,
		count: Int
	) {
		val openAppIntent = Intent(context, MainActivity::class.java)

		val contentIntent = PendingIntent.getActivity(
			context,
			PlantReminderScheduler.DAILY_NOTIFICATION_ID,
			openAppIntent,
			PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
		)

		val notification = NotificationCompat.Builder(
			context,
			PlantReminderScheduler.CHANNEL_ID
		)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentTitle(context.getString(R.string.notification_title_watering_reminder))
			.setContentText(
				context.resources.getQuantityString(
					R.plurals.notification_text_multiple_plants_due,
					count,
					count
				)
			)
			.setPriority(NotificationCompat.PRIORITY_DEFAULT)
			.setAutoCancel(true)
			.setContentIntent(contentIntent)
			.build()

		NotificationManagerCompat.from(context).notify(
			PlantReminderScheduler.DAILY_NOTIFICATION_ID,
			notification
		)
	}

	private fun endOfTodayMillis(): Long {
		val zoneId = ZoneId.systemDefault()

		return LocalDate
			.now(zoneId)
			.plusDays(1)
			.atStartOfDay(zoneId)
			.toInstant()
			.toEpochMilli() - 1
	}
}
