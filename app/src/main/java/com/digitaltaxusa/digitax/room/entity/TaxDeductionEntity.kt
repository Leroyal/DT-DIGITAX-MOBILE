package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for tracking user tax deductible events.
 *
 * @property uid Int Marks a field in an Entity as the primary key.
 * @property timestamp String? The timestamp for the logged event in database.
 * @property label String? The name of the event.
 * @property cost String? The recorded cost of the event.
 * @property scannedImagePath String? The stored image path or attachment for the tax
 * deductible event. This can be photos of receipts, invoices, ect.
 * @property notes String? Notes about the event.
 * @property deviceId String? The device unique identifier.
 */
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