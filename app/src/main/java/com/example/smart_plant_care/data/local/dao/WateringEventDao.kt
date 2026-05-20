package com.example.smart_plant_care.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.smart_plant_care.data.local.entity.WateringEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WateringEventDao {
    @Insert
    fun insertEvent(event: WateringEventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(events: List<WateringEventEntity>)

    @Query("SELECT * FROM watering_events WHERE plantId = :plantId ORDER BY wateredAt DESC LIMIT :limit")
    fun getRecentEventsByPlantId(plantId: Int, limit: Int): Flow<List<WateringEventEntity>>

    @Query("SELECT * FROM watering_events WHERE plantId = :plantId ORDER BY wateredAt DESC")
    fun getEventsByPlantId(plantId: Int): Flow<List<WateringEventEntity>>

    @Query("DELETE FROM watering_events WHERE plantId = :plantId")
    fun deleteEventsByPlantId(plantId: Int)
}
