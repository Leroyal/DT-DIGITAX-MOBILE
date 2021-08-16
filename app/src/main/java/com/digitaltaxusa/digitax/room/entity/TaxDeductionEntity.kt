package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tax_deduction_table")
class TaxDeductionEntity {
    @PrimaryKey(autoGenerate = true)
    var uid = 1
    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null
    @ColumnInfo(name = "label")
    var label: String? = null
    @ColumnInfo(name = "cost")
    var cost: String? = null
    @ColumnInfo(name = "scanned_image_path")
    var scannedImagePath: String? = null
    @ColumnInfo(name = "notes")
    var notes: String? = null
    @ColumnInfo(name = "device_id")
    var deviceId: String? = null
}