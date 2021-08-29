package com.digitaltaxusa.digitax.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.digitaltaxusa.digitax.room.entity.TaxDeductionEntity

@Dao
interface TaxDeductionDao {

    // wrap inside of LiveData to avoid any threading issues
    @Query("SELECT * FROM tax_deduction_table ORDER BY uid ASC")
    fun entities(): LiveData<List<TaxDeductionEntity>>

    // wrap inside of LiveData to avoid any threading issues
    @Query("SELECT * FROM tax_deduction_table where uid = :uid")
    fun entity(uid: Int): LiveData<TaxDeductionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taxDeductionEntity: TaxDeductionEntity)

    @Update
    suspend fun update(taxDeductionEntity: TaxDeductionEntity)

    @Delete
    suspend fun delete(taxDeductionEntity: TaxDeductionEntity)

    @Query("DELETE FROM tax_deduction_table")
    suspend fun deleteAll()
}