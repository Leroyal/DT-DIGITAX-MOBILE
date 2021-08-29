package com.digitaltaxusa.framework.map.listeners

import com.digitaltaxusa.framework.map.enums.TravelMode
import com.google.android.gms.maps.model.LatLng
import java.io.UnsupportedEncodingException

interface GoogleServicesApiInterface {

    /**
     * Method is used to get the address based on latitude and longitude coordinates.
     *
     * @param latLng An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees.
     * @param listener Callback for when address is retrieved.
     */
    fun getAddress(
        latLng: LatLng,
        listener: AddressListener?
    )

    /**
     * Method is used to get address using an address String value.
     *
     * @param address The address of the latitude and longitude coordinates location.
     * @param listener Callback for when address is retrieved.
     */
    fun getAddress(
        address: String,
        listener: AddressListener?
    )

    /**
     * Method is used to retrieve eta between two locations based on latitude and longitude coordinates
     *
     * @param origin An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Origin).
     * @param destination An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Destination).
     * @param unit Unit of measurement.
     * @param listener Callback for when eta is retrieved.
     */
    fun getDistanceEta(
        origin: LatLng,
        destination: LatLng,
        unit: String?,
        listener: DistanceListener?
    )

    /**
     * Method is used to retrieve the distance between two locations based on latitude and
     * longitude coordinates.
     *
     * @param origin An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Origin).
     * @param destination An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Destination).
     * @param isDriving True if user is driving, otherwise false.
     * @param listener Callback for when distance between two locations is retrieved.
     */
    fun getDistance(
        origin: LatLng,
        destination: LatLng,
        isDriving: Boolean,
        listener: DistanceListener?
    )

    /**
     * Method is used for Google Places API calls.
     *
     * Predictions are made based on factors, like the popularity and freshness of search terms.
     * When you choose a prediction, you do a search using the term you selected.
     *
     * @param input The user input.
     * @param latLng An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees.
     * @param radius The distance from the center of a circle to a point on the circle.
     * @param listener Callback for when Google returns back prediction results.
     * @throws UnsupportedEncodingException Thrown when a program asks for a particular
     * character converter that is unavailable.
     */
    fun getPlacesAutoComplete(
        input: String?,
        latLng: LatLng?,
        radius: Int,
        listener: PlacesAutoCompleteListener?
    )

    /**
     * TODO create listener for nearbyplaces
     * Method is used for Google Places API calls (nearby places).
     *
     * The Google Places API Web Service allows you to query for place information on a
     * variety of categories, such as: establishments, prominent points of interest,
     * geographic locations, and more
     *
     * @param latLng An immutable class representing a pair of latitude and longitude
     * coordinates, stored as degrees.
     * @param radius The distance from the center of a circle to a point on the circle.
     * @param type The Place type (according to Google Places API).
     */
    fun getNearbyPlaces(
        latLng: LatLng,
        radius: Int,
        type: String?
    )

    /**
     * Method is used for returning a URL for Place Photos.
     *
     * The Place Photo service, part of the Google Places API Web Service, is a read-only API
     * that allows you to add high quality photographic content to your application.
     *
     * @param photoReference A string used to identify the photo when you perform a Photo request.
     * @param maxWidth The maximum width of the image.
     * @param maxHeight The maximum height of the image.
     */
    fun getPlacesPhotoUrl(
        photoReference: String?,
        maxWidth: Int?,
        maxHeight: Int?
    ): String

    /**
     * Method is used for Google Places API calls (get location details).
     *
     * The Google Places API Web Service allows you to query for place information on a
     * variety of categories, such as: establishments, prominent points of interest,
     * geographic locations, and more.
     *
     * @param placeId A place ID is a textual identifier that uniquely identifies a place.
     * @param listener Callback for when location details are retrieved.
     */
    fun getPlacesDetail(
        placeId: String,
        listener: AddressListener?
    )

    /**
     * Method is used for Google Directions API calls.
     *
     * @param origin An immutable class representing a pair of latitude and longitude
     * coordinates, stored as degrees (Origin).
     * @param destination An immutable class representing a pair of latitude and longitude
     * coordinates, stored as degrees (Destination).
     * @param listener Callback for when directions are retrieved.
     */
    fun getDirections(
        origin: String,
        destination: String,
        listener: DirectionsListener?
    )

    /**
     * Method is used to retrieve turn by turn directions.
     *
     * @param origin An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Origin).
     * @param destination An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Destination).
     * @param travelMode String which is either [KEY_TRAVEL_MODE_WALKING] or [KEY_TRAVEL_MODE_DRIVING].
     * @param listener Callback for when directions are retrieved.
     */
    fun getTurnByTurnDirections(
        origin: LatLng,
        destination: LatLng,
        travelMode: TravelMode,
        listener: TurnByTurnListener?
    )

}