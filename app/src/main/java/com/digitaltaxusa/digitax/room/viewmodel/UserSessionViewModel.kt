package com.digitaltaxusa.digitax.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.digitaltaxusa.digitax.room.entity.UserSessionEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.digitax.room.repository.UserSessionRepository
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.logger.Logger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class UserSessionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val sessionRepository: UserSessionRepository = UserSessionRepository(application)

    /**
     * Retrieve data from Room database tables.
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    val entity: LiveData<UserSessionEntity> = sessionRepository.entity

    /**
     * Method is used to perform INSERT operation.
     *
     * @param userSessionEntity User session entity.
     * @return Job
     */
    fun insert(userSessionEntity: UserSessionEntity) = viewModelScope.launch {
        sessionRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.INSERT,
            userSessionEntity = userSessionEntity
        )
    }

    /**
     * Method is used to perform UPDATE operation.
     *
     * @param userSessionEntity User session entity.
     * @return Job
     */
    fun update(userSessionEntity: UserSessionEntity) = viewModelScope.launch {
        sessionRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.UPDATE,
            userSessionEntity = userSessionEntity
        )
    }

    /**
     * Method is used to perform DELETE operation.
     *
     * @return Job
     */
    fun delete() = viewModelScope.launch {
        sessionRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.DELETE,
            userSessionEntity = null
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