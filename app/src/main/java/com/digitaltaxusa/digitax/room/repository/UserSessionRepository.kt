package com.digitaltaxusa.digitax.room.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.digitaltaxusa.digitax.room.dao.UserSessionDao
import com.digitaltaxusa.digitax.room.database.AppDatabase
import com.digitaltaxusa.digitax.room.entity.UserSessionEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserSessionRepository(
    context: Context,
    uid: Int = 0
) {

    // dao used internally in the view model
    internal val userSessionDao: UserSessionDao

    /**
     * Retrieve data from Room database tables.
     *
     * User session live data - [UserSessionDao].
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    var entity: LiveData<UserSessionEntity>

    init {
        val database = AppDatabase.getDatabase(context.applicationContext)
        // instantiate DAO (data access objects)
        userSessionDao = database?.userSessionDao()!!
        // instantiate live data objects
        entity = userSessionDao.entity
    }

    /**
     * Method is used to perform operations for [UserSessionEntity].
     *
     * @param operation The CRUD operation to perform, e.g. INSERT, UPDATE, DELETE, ect.
     * @param userSessionEntity User session entity.
     */
    suspend fun performDatabaseOperation(
        operation: Enums.DatabaseOperation,
        userSessionEntity: UserSessionEntity? = null,
    ) = withContext(Dispatchers.IO) {
        when (operation) {
            Enums.DatabaseOperation.INSERT -> {
                userSessionDao.insert(userSessionEntity)
            }
            Enums.DatabaseOperation.UPDATE -> {
                userSessionDao.update(userSessionEntity)
            }
            Enums.DatabaseOperation.DELETE -> {
                userSessionDao.delete()
            }
            else -> {
                // no-op
            }
        }
    }
}