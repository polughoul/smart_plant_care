package com.example.smart_plant_care.data.repository

import com.example.smart_plant_care.data.local.dao.PlantDao
import com.example.smart_plant_care.data.local.dao.WateringEventDao
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import com.example.smart_plant_care.util.calculateNextWateringDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed interface InsertPlantResult {
    data object Added : InsertPlantResult
    data object Duplicate : InsertPlantResult
}

class PlantRepository(private val plantDao: PlantDao, private val wateringEventDao: WateringEventDao) {
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
}
