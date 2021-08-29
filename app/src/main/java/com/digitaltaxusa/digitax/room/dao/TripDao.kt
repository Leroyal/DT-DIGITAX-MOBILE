package com.digitaltaxusa.digitax.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.digitaltaxusa.digitax.room.entity.TripEntity

@Dao
interface TripDao {

    // wrap inside of LiveData to avoid any threading issues
    @Query("SELECT * FROM trip_table ORDER BY uid ASC")
    fun entities(): LiveData<List<TripEntity>>

    // wrap inside of LiveData to avoid any threading issues
    @Query("SELECT * FROM trip_table where uid = :uid")
    fun entity(uid: Int): LiveData<TripEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tripEntity: TripEntity)

    @Update
    suspend fun update(tripEntity: TripEntity)

    @Delete
    suspend fun delete(tripEntity: TripEntity)

    @Query("DELETE FROM trip_table")
    suspend fun deleteAll()
}