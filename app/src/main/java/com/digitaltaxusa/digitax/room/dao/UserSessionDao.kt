package com.digitaltaxusa.digitax.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.digitaltaxusa.digitax.room.entity.UserSessionEntity

@Dao
interface UserSessionDao {

    // wrap inside of LiveData to avoid any threading issues
    @get:Query("SELECT * FROM user_session_table where uid = 1")
    val entity: LiveData<UserSessionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(userSessionEntity: UserSessionEntity?)

    @Update
    suspend fun update(userSessionEntity: UserSessionEntity?)

    @Query("DELETE FROM user_session_table")
    suspend fun delete()
}