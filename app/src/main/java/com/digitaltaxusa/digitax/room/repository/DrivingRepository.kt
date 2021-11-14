package com.digitaltaxusa.digitax.room.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.digitaltaxusa.digitax.room.dao.DrivingDao
import com.digitaltaxusa.digitax.room.database.AppDatabase
import com.digitaltaxusa.digitax.room.entity.DrivingEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DrivingRepository(
    context: Context,
    uid: Int = 0
) {

    // dao used internally in the view model
    internal val drivingDao: DrivingDao

    /**
     * Retrieve data from Room database tables.
     *
     * Driving live data - [DrivingDao].
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    var entities: LiveData<List<DrivingEntity>>
    var entity: LiveData<DrivingEntity>

    init {
        val database = AppDatabase.getDatabase(context.applicationContext)
        // instantiate DAO (data access objects)
        drivingDao = database?.drivingDao()!!
        // instantiate live data objects
        entities = drivingDao.entities()
        entity = drivingDao.entity(uid)
    }

    /**
     * Method is used to perform operations for [DrivingEntity].
     *
     * @param operation The CRUD operation to perform, e.g. INSERT, UPDATE, DELETE, ect.
     * @param drivingEntity Driving entity.
     */
    suspend fun performDatabaseOperation(
        operation: Enums.DatabaseOperation,
        drivingEntity: DrivingEntity = DrivingEntity()
    ) = withContext(Dispatchers.IO) {
        when (operation) {
            Enums.DatabaseOperation.INSERT -> {
                drivingDao.insert(drivingEntity)
            }
            Enums.DatabaseOperation.UPDATE -> {
                drivingDao.update(drivingEntity)
            }
            Enums.DatabaseOperation.DELETE -> {
                drivingDao.delete(drivingEntity)
            }
            Enums.DatabaseOperation.DELETE_ALL -> {
                drivingDao.deleteAll()
            }
        }
    }
}