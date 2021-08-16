package com.digitaltaxusa.digitax.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.digitaltaxusa.digitax.room.entity.DrivingEntity

@Dao
interface DrivingDao {

    // wrap inside of LiveData to avoid any threading issues
    @Query("SELECT * FROM driving_table ORDER BY uid ASC")
    fun entities(): LiveData<List<DrivingEntity>>

    // wrap inside of LiveData to avoid any threading issues
    @Query("SELECT * FROM driving_table where uid = :uid")
    fun entity(uid: Int): LiveData<DrivingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(drivingEntity: DrivingEntity)

    @Query("UPDATE driving_table SET origin_label= :originLabel, destination_label= :destinationLabel, trip_type= :tripType WHERE uid = :uid")
    fun update(
        uid: Int? = 1,
        originLabel: String? = "",
        destinationLabel: String? = "",
        tripType: String? = ""
    )

    @Delete
    suspend fun delete(drivingEntity: DrivingEntity)

    @Query("DELETE FROM driving_table")
    suspend fun deleteAll()
}