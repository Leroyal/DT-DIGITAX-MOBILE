package com.digitaltaxusa.digitax.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.digitaltaxusa.digitax.room.entity.DrivingEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.digitax.room.repository.DrivingRepository
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.logger.Logger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class DrivingViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val drivingRepository: DrivingRepository = DrivingRepository(application)

    /**
     * Retrieve data from Room database tables.
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    var entities: LiveData<List<DrivingEntity>> = drivingRepository.entities
    var entity: LiveData<DrivingEntity> = drivingRepository.entity

    /**
     * Retrieve data from Room database tables.
     *
     * @param uid Int Unique identifier representing the primary key for this database.
     */
    fun entity(uid: Int) {
        entity = drivingRepository.drivingDao.entity(uid)
    }

    /**
     * Method is used to perform INSERT operation.
     *
     * @param drivingEntity Driving entity.
     * @return Job
     */
    fun insert(drivingEntity: DrivingEntity) = viewModelScope.launch {
        drivingRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.INSERT,
            drivingEntity = drivingEntity
        )
    }

    /**
     * Method is used to perform UPDATE operation.
     *
     * @param drivingEntity Driving entity.
     * @return Job
     */
    fun update(drivingEntity: DrivingEntity) = viewModelScope.launch {
        drivingRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.UPDATE,
            drivingEntity = drivingEntity
        )
    }

    /**
     * Method is used to perform DELETE operation.
     *
     * @param drivingEntity Driving entity.
     * @return Job
     */
    fun delete(drivingEntity: DrivingEntity) = viewModelScope.launch {
        drivingRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.DELETE,
            drivingEntity = drivingEntity
        )
    }

    /**
     * Method is used to perform DELETE operation.
     *
     * <p>This will clear the entire database table.</p>
     */
    fun deleteAll() = viewModelScope.launch {
        drivingRepository.performDatabaseOperation(
            Enums.DatabaseOperation.DELETE_ALL
        )
    }

    /**
     * Method overridden to cancel the [viewModelScope] children in case a Job
     * is running in background.
     */
    override fun onCleared() {
        try {
            viewModelScope.coroutineContext.cancel()
        } catch (e: CancellationException) {
            e.printStackTrace()
            Logger.e(Constants.TAG, e.message.orEmpty(), e)
        }
    }
}