package com.example.smart_plant_care.data.repository

import android.content.Context
import android.net.Uri
import com.example.smart_plant_care.data.local.dao.PlantDao
import com.example.smart_plant_care.data.local.dao.WateringEventDao
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import com.example.smart_plant_care.util.calculateNextWateringDate
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed interface InsertPlantResult {
    data object Added : InsertPlantResult
    data object Duplicate : InsertPlantResult
}

class PlantRepository(private val plantDao: PlantDao, private val wateringEventDao: WateringEventDao) {
    companion object {
        const val DEMO_PLANT_NOTE_MARKER = "__demo_reminder__"
        private const val IMAGE_CACHE_DIR = "plant_images"
    }

    fun getAllPlants(): Flow<List<MyPlantEntity>> {
        return plantDao.getAllPlants()
    }

    fun getRecentWateringEvents(plantId: Int, limit: Int): Flow<List<WateringEventEntity>> {
        return wateringEventDao.getRecentEventsByPlantId(plantId, limit)
    }

    fun getAllWateringEvents(plantId: Int): Flow<List<WateringEventEntity>> {
        return wateringEventDao.getEventsByPlantId(plantId)
    }

    suspend fun insertPlant(plant: MyPlantEntity): InsertPlantResult {
        return withContext(Dispatchers.IO) {
            val insertId = plantDao.insertPlant(plant)
            if (insertId == -1L) InsertPlantResult.Duplicate else InsertPlantResult.Added
        }
    }

    suspend fun updatePlant(plant: MyPlantEntity) {
        withContext(Dispatchers.IO) {
            plantDao.updatePlant(plant)
        }
    }

    suspend fun deletePlant(id: Int) {
        withContext(Dispatchers.IO) {
            wateringEventDao.deleteEventsByPlantId(id)
            plantDao.deletePlantById(id)
        }
    }

    suspend fun clearWateringHistory(plantId: Int) {
        withContext(Dispatchers.IO) {
            wateringEventDao.deleteEventsByPlantId(plantId)
        }
    }

    suspend fun restoreWateringEvents(events: List<WateringEventEntity>) {
        if (events.isEmpty()) return
        withContext(Dispatchers.IO) {
            val normalizedEvents = events.map { it.copy(id = 0) }
            wateringEventDao.insertEvents(normalizedEvents)
        }
    }

    suspend fun markPlantAsWatered(plantId: Int): Boolean {
        return withContext(Dispatchers.IO) {
            val plant = plantDao.getPlantById(plantId) ?: return@withContext false
            val nextWateringDate = calculateNextWateringDate(plant.waterIntervalDays)
            plantDao.updateNextWateringDateById(plantId, nextWateringDate)
            wateringEventDao.insertEvent(
                WateringEventEntity(plantId = plantId, wateredAt = System.currentTimeMillis())
            )
            true
        }
    }

    suspend fun replaceDemoDuePlants(count: Int) {
        val normalizedCount = count.coerceAtLeast(1)
        withContext(Dispatchers.IO) {
            plantDao.deletePlantsByNoteText(DEMO_PLANT_NOTE_MARKER)
            val dueNow = System.currentTimeMillis()
            repeat(normalizedCount) { index ->
                val demoName = if (normalizedCount == 1) {
                    "Demo plant"
                } else {
                    "Demo plant ${index + 1}"
                }
                plantDao.insertPlant(
                    MyPlantEntity(
                        customName = demoName,
                        speciesName = demoName,
                        waterIntervalDays = 1,
                        nextWateringDate = dueNow,
                        noteText = DEMO_PLANT_NOTE_MARKER
                    )
                )
            }
        }
    }

    suspend fun cacheRemoteImageForPlant(
        context: Context,
        plant: MyPlantEntity
    ): MyPlantEntity {
        return withContext(Dispatchers.IO) {
            val currentUrl = plant.imageUrl ?: return@withContext plant
            if (!isRemoteHttpUrl(currentUrl)) return@withContext plant

            val cachedUri = downloadToInternalStorage(
                context = context,
                remoteUrl = currentUrl,
                cacheKey = "${plant.remotePlantId ?: plant.id}_${currentUrl.hashCode()}"
            ) ?: return@withContext plant

            plant.copy(imageUrl = cachedUri)
        }
    }

    private fun isRemoteHttpUrl(value: String): Boolean {
        val lower = value.lowercase()
        return lower.startsWith("http://") || lower.startsWith("https://")
    }

    private fun downloadToInternalStorage(
        context: Context,
        remoteUrl: String,
        cacheKey: String
    ): String? {
        return runCatching {
            val imagesDir = File(context.filesDir, IMAGE_CACHE_DIR)
            if (!imagesDir.exists()) imagesDir.mkdirs()

            val extension = inferExtensionFromUrl(remoteUrl)
            val targetFile = File(imagesDir, "plant_$cacheKey.$extension")
            if (targetFile.exists() && targetFile.length() > 0L) {
                return@runCatching Uri.fromFile(targetFile).toString()
            }

            val connection = (URL(remoteUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 15_000
                instanceFollowRedirects = true
            }

            connection.connect()
            if (connection.responseCode !in 200..299) {
                connection.disconnect()
                return@runCatching null
            }

            connection.inputStream.use { input ->
                targetFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            connection.disconnect()

            if (targetFile.length() <= 0L) {
                targetFile.delete()
                null
            } else {
                Uri.fromFile(targetFile).toString()
            }
        }.getOrNull()
    }

    private fun inferExtensionFromUrl(url: String): String {
        val cleanedPath = url.substringBefore('?').substringBefore('#')
        return when {
            cleanedPath.endsWith(".png", ignoreCase = true) -> "png"
            cleanedPath.endsWith(".webp", ignoreCase = true) -> "webp"
            cleanedPath.endsWith(".gif", ignoreCase = true) -> "gif"
            else -> "jpg"
        }
    }
}
