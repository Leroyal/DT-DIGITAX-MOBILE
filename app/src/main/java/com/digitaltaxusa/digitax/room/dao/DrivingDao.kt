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

    @Update
    suspend fun update(drivingEntity: DrivingEntity)

    @Delete
    suspend fun delete(drivingEntity: DrivingEntity)

    @Query("DELETE FROM driving_table")
    suspend fun deleteAll()
}