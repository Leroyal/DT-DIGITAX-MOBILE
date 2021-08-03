package com.digitaltaxusa.framework.map.listeners

import com.digitaltaxusa.framework.map.model.Address

interface AddressListener {

    /**
     * Interface for when address information is received.
     *
     * @param address The address of the latitude and longitude coordinates location.
     */
    fun onAddressResponse(address: Address?)

    /**
     * Interface for when retrieving address information fails.
     */
    fun onAddressError()

    /**
     * Interface for when there are no results when retrieving address information.
     */
    fun onZeroResults()
}