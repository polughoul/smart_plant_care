package com.example.smart_plant_care.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlantDao {
    @Query("SELECT * FROM my_plants")
    fun getAllPlants(): Flow<List<MyPlantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPlant(plant: MyPlantEntity)

    @Query("DELETE FROM my_plants WHERE id = :plantId")
    fun deletePlantById(plantId: Int)
}