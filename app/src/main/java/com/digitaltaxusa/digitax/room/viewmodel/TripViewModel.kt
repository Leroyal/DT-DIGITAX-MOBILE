package com.digitaltaxusa.digitax.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.digitaltaxusa.digitax.room.entity.TripEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.digitax.room.repository.TripRepository
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.logger.Logger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class TripViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val tripRepository: TripRepository = TripRepository(application)

    /**
     * Retrieve data from Room database tables.
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    var entities: LiveData<List<TripEntity>> = tripRepository.entities
    var entity: LiveData<TripEntity> = tripRepository.entity

    /**
     * Retrieve data from Room database tables.
     *
     * @param uid Int Unique identifier representing the primary key for this database.
     */
    fun entity(uid: Int) {
        entity = tripRepository.tripDao.entity(uid)
    }

    /**
     * Method is used to perform INSERT operation.
     *
     * @param drivingEntity Driving entity.
     * @return Job
     */
    fun insert(drivingEntity: TripEntity) = viewModelScope.launch {
        tripRepository.performDatabaseOperation(
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
    fun update(drivingEntity: TripEntity) = viewModelScope.launch {
        tripRepository.performDatabaseOperation(
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
    fun delete(drivingEntity: TripEntity) = viewModelScope.launch {
        tripRepository.performDatabaseOperation(
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
        tripRepository.performDatabaseOperation(
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