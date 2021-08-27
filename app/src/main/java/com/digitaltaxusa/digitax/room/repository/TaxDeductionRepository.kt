package com.digitaltaxusa.digitax.room.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.digitaltaxusa.digitax.room.dao.TaxDeductionDao
import com.digitaltaxusa.digitax.room.database.AppDatabase
import com.digitaltaxusa.digitax.room.entity.TaxDeductionEntity
import com.digitaltaxusa.digitax.room.enums.Enums
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaxDeductionRepository(
    context: Context,
    uid: Int = 0
) {
    // dao used internally in the view model
    internal val taxDeductionDao: TaxDeductionDao

    /**
     * Retrieve data from Room database tables.
     *
     * Tax deduction live data - [TaxDeductionDao].
     *
     * @return LiveData is a data holder class that can be observed within a given lifecycle.
     */
    val entities: LiveData<List<TaxDeductionEntity>>
    var entity: LiveData<TaxDeductionEntity>

    init {
        val database = AppDatabase.getDatabase(context.applicationContext)
        // instantiate DAO (data access objects)
        taxDeductionDao = database?.taxDeductionDao()!!
        // instantiate live data objects
        entities = taxDeductionDao.entities()
        entity = taxDeductionDao.entity(uid)
    }

    /**
     * Method is used to perform operations for [TaxDeductionEntity].
     *
     * @param operation The CRUD operation to perform, e.g. INSERT, UPDATE, DELETE, ect.
     * @param taxDeductionEntity Tax deduction entity.
     */
    suspend fun performDatabaseOperation(
        operation: Enums.DatabaseOperation,
        taxDeductionEntity: TaxDeductionEntity = TaxDeductionEntity()
    ) = withContext(Dispatchers.IO) {
        when (operation) {
            Enums.DatabaseOperation.INSERT -> {
                taxDeductionDao.insert(taxDeductionEntity)
            }
            Enums.DatabaseOperation.UPDATE -> {
                taxDeductionDao.update(
                    uid = taxDeductionEntity.uid,
                    cost = taxDeductionEntity.cost,
                    label = taxDeductionEntity.label,
                    notes = taxDeductionEntity.notes
                )
            }
            Enums.DatabaseOperation.DELETE -> {
                taxDeductionDao.delete(taxDeductionEntity)
            }
            Enums.DatabaseOperation.DELETE_ALL -> {
                taxDeductionDao.deleteAll()
            }
        }
    }
}