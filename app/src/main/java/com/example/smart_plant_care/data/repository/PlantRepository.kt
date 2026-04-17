package com.example.smart_plant_care.data.repository

import com.example.smart_plant_care.data.local.dao.PlantDao
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlantRepository( private val plantDao: PlantDao) {


    fun getAllPlants(): Flow<List<MyPlantEntity>> {
        return plantDao.getAllPlants()
    }

    suspend fun insertPlant(plant: MyPlantEntity) {
        withContext(Dispatchers.IO) {
            plantDao.insertPlant(plant)
        }
    }

    suspend fun updatePlant(plant: MyPlantEntity) {
        withContext(Dispatchers.IO) {
            plantDao.updatePlant(plant)
        }
    }

    suspend fun deletePlant(id: Int) {
        withContext(Dispatchers.IO) {
            plantDao.deletePlantById(id)
        }
    }
}