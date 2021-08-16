package com.digitaltaxusa.digitax.room.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.digitaltaxusa.digitax.room.entity.TaxDeductionEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import com.digitaltaxusa.digitax.room.repository.TaxDeductionRepository
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.logger.Logger
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

class TaxDeductionViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val taxDeductionRepository: TaxDeductionRepository = TaxDeductionRepository(application)

    /**
     * Retrieve data from Room database tables.
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    val entities: LiveData<List<TaxDeductionEntity>> = taxDeductionRepository.entities
    var entity: LiveData<TaxDeductionEntity> = taxDeductionRepository.entity

    /**
     * Retrieve data from Room database tables.
     *
     * @param uid Int Unique identifier representing the primary key for this database.
     */
    fun entity(uid: Int) {
        entity = taxDeductionRepository.taxDeductionDao.entity(uid)
    }

    /**
     * Method is used to perform INSERT operation.
     *
     * @param taxDeductionEntity Tax deduction entity.
     * @return Job
     */
    fun insert(taxDeductionEntity: TaxDeductionEntity) = viewModelScope.launch {
        taxDeductionRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.INSERT,
            taxDeductionEntity = taxDeductionEntity
        )
    }

    /**
     * Method is used to perform UPDATE operation.
     *
     * @param taxDeductionEntity Tax deduction entity.
     * @return Job
     */
    fun update(taxDeductionEntity: TaxDeductionEntity) = viewModelScope.launch {
        taxDeductionRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.UPDATE,
            taxDeductionEntity = taxDeductionEntity
        )
    }

    /**
     * Method is used to perform DELETE operation.
     *
     * @param taxDeductionEntity Tax deduction entity.
     * @return Job
     */
    fun delete(taxDeductionEntity: TaxDeductionEntity) = viewModelScope.launch {
        taxDeductionRepository.performDatabaseOperation(
            operation = Enums.DatabaseOperation.DELETE,
            taxDeductionEntity = taxDeductionEntity
        )
    }

    /**
     * Method is used to perform DELETE operation.
     *
     * <p>This will clear the entire database table.</p>
     */
    fun deleteAll() = viewModelScope.launch {
        taxDeductionRepository.performDatabaseOperation(Enums.DatabaseOperation.DELETE_ALL)
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