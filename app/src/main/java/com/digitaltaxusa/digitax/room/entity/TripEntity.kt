package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for tracking trips.
 *
 * @property uid Int Marks a field in an Entity as the primary key.
 * @property originTimestamp String? The timestamp for the logged origin trip in database.
 * @property destinationTimestamp String? The timestamp for the logged destination trip in database.
 * @property totalMetersDriven Float? The total distance driven (in meters).
 * @property tripDuration Float? The trip duration.
 * @property averageSpeed Float? The average speed during the trip.
 * @property originLabel String? The name of the origin trip.
 * @property destinationLabel String? The name of the destination trip.
 * @property originAddress String? The origin address.
 * @property destinationAddress String? The destination address.
 * @property originLatlng String? The origin geographical location stored as
 * "latitude,longitude" e.g. 34.4520644,-118.4977783.
 * @property destinationLatlng String? The destination geographical location stored as
 * "latitude,longitude" e.g. 34.4520644,-118.4977783.
 * @property tripType String? The trip classification enumeration e.g. business,
 * personal, unclassified
 * @property originNotes String? Notes about the origin event.
 * @property destinationNotes String? Notes about the destination event.
 * @property deviceId String? String? The device unique identifier.
 */
@Entity(tableName = "trip_table")
class TripEntity {
    @PrimaryKey(autoGenerate = true)
    var uid: Int? = null

    // time when trip began
    @ColumnInfo(name = "origin_timestamp")
    var originTimestamp: String? = null

    // time when trip ended
    @ColumnInfo(name = "destination_timestamp")
    var destinationTimestamp: String? = null

    // the total distance driven (in meters)
    @ColumnInfo(name = "total_meters_driven")
    var totalMetersDriven: Float? = null

    // the duration of the trip (in milliseconds)
    @ColumnInfo(name = "trip_duration")
    var tripDuration: Long? = null

    @ColumnInfo(name = "average_speed")
    var averageSpeed: Float? = null

    @ColumnInfo(name = "origin_label")
    var originLabel: String? = null

    @ColumnInfo(name = "destination_label")
    var destinationLabel: String? = null

    @ColumnInfo(name = "origin_address")
    var originAddress: String? = null

    @ColumnInfo(name = "destination_address")
    var destinationAddress: String? = null

    // LagLng stored as "latitude,longitude" e.g. 34.4520644,-118.4977783
    @ColumnInfo(name = "origin_latlng")
    var originLatlng: String? = null

    // LagLng stored as "latitude,longitude" e.g. 34.4520644,-118.4977783
    @ColumnInfo(name = "destination_latlng")
    var destinationLatlng: String? = null

    // [TripType] enumeration e.g. business, personal, unclassified
    @ColumnInfo(name = "trip_type")
    var tripType: String? = null

    @ColumnInfo(name = "origin_notes")
    var originNotes: String? = null

    @ColumnInfo(name = "destination_notes")
    var destinationNotes: String? = null

    @ColumnInfo(name = "device_id")
    var deviceId: String? = null
}