package com.digitaltaxusa.framework.map.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

/**
 * Place model used for constructing and deconstructing address components.
 *
 * @property placeId String? Google unique identification that identifies a place/location.
 * @property description String? The description of the location.
 * @property addressLine1 String? Street number and name.
 * @property addressLine2 String? The apartment, suite or space number (or any other
 * designation not literally part of the physical address). If the apartment, suite, or
 * space number is short, and the address is short, it can all be shown on one line.
 * @property locationType String? // TODO location type may not be relevant. It's being used as stop/station in UDI project
 * @property latitude Double A measurement on a globe or map of location north or south
 * of the Equator.
 * @property longitude Double The measurement east or west of the prime meridian.
 * @constructor
 */
class Place(
    parcel: Parcel? = null
) : Parcelable {

    var placeId: String? = null
    var description: String? = null
    var addressLine1: String? = null
    var addressLine2: String? = null
    var locationType: String? = null
    var latitude = 0.0
    var longitude = 0.0

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(placeId)
        parcel.writeString(description)
        parcel.writeString(addressLine1)
        parcel.writeString(addressLine2)
        parcel.writeString(locationType)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    init {
        placeId = parcel?.readString()
        description = parcel?.readString()
        addressLine1 = parcel?.readString()
        addressLine2 = parcel?.readString()
        locationType = parcel?.readString()
        latitude = parcel?.readDouble() ?: 0.0
        longitude = parcel?.readDouble() ?: 0.0
    }

    companion object CREATOR : Creator<Place> {
        override fun createFromParcel(parcel: Parcel): Place {
            return Place(parcel)
        }

        override fun newArray(size: Int): Array<Place?> {
            return arrayOfNulls(size)
        }
    }
}