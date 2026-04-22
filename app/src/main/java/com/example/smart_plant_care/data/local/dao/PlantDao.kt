package com.example.smart_plant_care.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.smart_plant_care.data.local.entity.MyPlantEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface PlantDao {
    @Query("SELECT * FROM my_plants")
    fun getAllPlants(): Flow<List<MyPlantEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertPlant(plant: MyPlantEntity): Long

    @Query("SELECT COUNT(*) > 0 FROM my_plants WHERE remotePlantId = :remotePlantId")
    fun existsByRemotePlantId(remotePlantId: Int): Boolean

    @Query("SELECT * FROM my_plants WHERE id = :plantId LIMIT 1")
    fun getPlantById(plantId: Int): MyPlantEntity?

    @Query("UPDATE my_plants SET nextWateringDate = :nextWateringDate WHERE id = :plantId")
    fun updateNextWateringDateById(plantId: Int, nextWateringDate: Long)

    @Update
    fun updatePlant(plant: MyPlantEntity)

    @Query("DELETE FROM my_plants WHERE id = :plantId")
    fun deletePlantById(plantId: Int)
}