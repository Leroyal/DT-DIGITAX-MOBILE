package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driving_table")
class DrivingEntity {
    @PrimaryKey(autoGenerate = true)
    var uid = 1
    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null
    @ColumnInfo(name = "miles_driven")
    var milesDriven: String? = null
    @ColumnInfo(name = "origin_label")
    var originLabel: String? = null
    @ColumnInfo(name = "destination_label")
    var destinationLabel: String? = null
    @ColumnInfo(name = "origin_address")
    var originAddress: String? = null
    @ColumnInfo(name = "destination_address")
    var destinationAddress: String? = null
    @ColumnInfo(name = "origin_latlng")
    var originLatlng: String? = null
    @ColumnInfo(name = "destination_latlng")
    var destinationLatlng: String? = null
    // [TripType] enumeration e.g. business, personal, unclassified
    @ColumnInfo(name = "trip_type")
    var tripType: String? = null
    @ColumnInfo(name = "device_id")
    var deviceId: String? = null
}