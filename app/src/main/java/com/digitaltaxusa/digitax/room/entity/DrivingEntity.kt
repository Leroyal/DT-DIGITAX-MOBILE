package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for tracking driving information while on a trip.
 *
 * @property uid Int Marks a field in an Entity as the primary key.
 * @property timestamp String? The timestamp for the logged event in database.
 * @property metersDriven Float? The distance driven (in meters) along the way.
 * @property speed Float? The recorded speed when the driving entry was logged.
 * @property latlng String? The geographical location stored as "latitude,longitude"
 * e.g. 34.4520644,-118.4977783.
 * @property deviceId String? The device unique identifier.
 */
@Entity(tableName = "driving_table")
class DrivingEntity {
    @PrimaryKey(autoGenerate = true)
    var uid = 1

    @ColumnInfo(name = "timestamp")
    var timestamp: String? = null

    // the distance driven (in meters) along the way
    @ColumnInfo(name = "meters_driven")
    var metersDriven: Float? = null

    @ColumnInfo(name = "speed")
    var speed: Float? = null

    // LagLng stored as "latitude,longitude" e.g. 34.4520644,-118.4977783
    @ColumnInfo(name = "latlng")
    var latlng: String? = null

    @ColumnInfo(name = "device_id")
    var deviceId: String? = null
}