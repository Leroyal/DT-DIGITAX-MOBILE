package com.digitaltaxusa.digitax.room.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.digitaltaxusa.digitax.room.dao.TripDao
import com.digitaltaxusa.digitax.room.database.AppDatabase
import com.digitaltaxusa.digitax.room.entity.TripEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TripRepository(
    context: Context,
    uid: Int = 0
) {

    // dao used internally in the view model
    internal val tripDao: TripDao

    /**
     * Retrieve data from Room database tables.
     *
     * Driving live data - [TripDao].
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    val entities: LiveData<List<TripEntity>>
    var entity: LiveData<TripEntity>

    init {
        val database = AppDatabase.getDatabase(context.applicationContext)
        // instantiate DAO (data access objects)
        tripDao = database?.tripDao()!!
        // instantiate live data objects
        entities = tripDao.entities()
        entity = tripDao.entity(uid)
    }

    /**
     * Method is used to perform operations for [TripEntity].
     *
     * @param operation The CRUD operation to perform, e.g. INSERT, UPDATE, DELETE, ect.
     * @param drivingEntity Driving entity.
     */
    suspend fun performDatabaseOperation(
        operation: Enums.DatabaseOperation,
        drivingEntity: TripEntity = TripEntity()
    ) = withContext(Dispatchers.IO) {
        when (operation) {
            Enums.DatabaseOperation.INSERT -> {
                tripDao.insert(drivingEntity)
            }
            Enums.DatabaseOperation.UPDATE -> {
                tripDao.update(drivingEntity)
            }
            Enums.DatabaseOperation.DELETE -> {
                tripDao.delete(drivingEntity)
            }
            Enums.DatabaseOperation.DELETE_ALL -> {
                tripDao.deleteAll()
            }
        }
    }
}