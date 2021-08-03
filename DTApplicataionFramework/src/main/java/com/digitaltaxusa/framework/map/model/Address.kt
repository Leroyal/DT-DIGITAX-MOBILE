package com.digitaltaxusa.framework.map.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Address model used for constructing and deconstructing address components.
 *
 * @property placeId String? Google unique identification that identifies a place/location.
 * @property addressLine1 String? Street number and name.
 * @property addressLine2 String? The apartment, suite or space number (or any other
 * designation not literally part of the physical address). If the apartment, suite, or
 * space number is short, and the address is short, it can all be shown on one line.
 * @property streetNumber String? The number on a street of a particular building or address.
 * @property city String? The city of the address.
 * @property state String? The state of the address.
 * @property stateCode String? The abbreviation of the state.
 * @property postalCode String? Also known as the zip code. In the U.S. it consists of five digits.
 * @property country String? The country of the address.
 * @property countryCode String? The abbreviation of the country.
 * @property formattedAddress String? The constructed address from it's parts. Will form the
 * full address.
 * @property latitude Double A measurement on a globe or map of location north or south
 * of the Equator.
 * @property longitude Double The measurement east or west of the prime meridian.
 * @constructor
 */
class Address(
    parcel: Parcel? = null
) : Parcelable {

    var placeId: String? = null
    var addressLine1: String? = null
    var addressLine2: String? = null
    var streetNumber: String? = null
    var city: String? = null
    var state: String? = null
    var stateCode: String? = null
    var postalCode: String? = null
    var country: String? = null
    var countryCode: String? = null
    var formattedAddress: String? = null
    var latitude = 0.0
    var longitude = 0.0

    override fun describeContents(): Int {
        return 0
    }

    /**
     * Create a new instance of the Parcelable class, instantiating it from the given Parcel
     * whose data had previously been written.
     *
     * Ref- https://developer.android.com/reference/android/os/Parcelable
     *
     * @param parcel Parcel Container for a message (data and object references) that can
     * be sent through an IBinder.
     * @param flag Int Additional flags about how the object should be written. May be 0 or
     * PARCELABLE_WRITE_RETURN_VALUE. Value is either 0 or a combination of
     * PARCELABLE_WRITE_RETURN_VALUE, and android.os.Parcelable.PARCELABLE_ELIDE_DUPLICATES.
     */
    override fun writeToParcel(
        parcel: Parcel,
        flag: Int
    ) {
        parcel.writeString(placeId)
        parcel.writeString(addressLine1)
        parcel.writeString(addressLine2)
        parcel.writeString(streetNumber)
        parcel.writeString(city)
        parcel.writeString(state)
        parcel.writeString(stateCode)
        parcel.writeString(postalCode)
        parcel.writeString(country)
        parcel.writeString(countryCode)
        parcel.writeString(formattedAddress)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    init {
        placeId = parcel?.readString()
        addressLine1 = parcel?.readString()
        addressLine2 = parcel?.readString()
        streetNumber = parcel?.readString()
        city = parcel?.readString()
        state = parcel?.readString()
        stateCode = parcel?.readString()
        postalCode = parcel?.readString()
        country = parcel?.readString()
        countryCode = parcel?.readString()
        formattedAddress = parcel?.readString()
        latitude = parcel?.readDouble() ?: 0.0
        longitude = parcel?.readDouble() ?: 0.0
    }

    companion object CREATOR : Parcelable.Creator<Address> {
        override fun createFromParcel(parcel: Parcel): Address {
            return Address(parcel)
        }

        override fun newArray(size: Int): Array<Address?> {
            return arrayOfNulls(size)
        }
    }
}