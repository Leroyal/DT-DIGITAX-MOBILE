package com.digitaltaxusa.framework.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import com.digitaltaxusa.framework.constants.Constants
import com.digitaltaxusa.framework.device.DeviceUtils
import com.digitaltaxusa.framework.map.constants.ConfigurationManager
import com.digitaltaxusa.framework.map.enums.TravelMode
import com.digitaltaxusa.framework.map.listeners.*
import com.digitaltaxusa.framework.map.model.Address
import com.digitaltaxusa.framework.map.model.Place
import com.digitaltaxusa.framework.map.polyline.RainbowGroundOverlayUtils
import com.digitaltaxusa.framework.map.polyline.RainbowPoint
import com.digitaltaxusa.framework.map.polyline.RainbowPolyline
import com.digitaltaxusa.framework.map.polyline.RainbowPolylineOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.lang.String.format
import java.net.URISyntaxException
import java.net.URLEncoder
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.math.ln
import kotlin.math.sin

class GoogleServicesClient(
    context: Context
) : GoogleServicesApiInterface {

    private val DISTANCE_MATRIX_UNIT_METRIC = "metric"
    private val ADDRESS_RESULT_CODE = 1001
    private val PLACES_DETAIL_RESULT_CODE = 1003
    private val DISTANCE_MATRIX_RESULT_CODE = 1004

    // rainbow polyline
    private val DEFAULT_STROKE_WIDTH = 16

    // the percentage of the arch that should be painted with the designated mid painted color
    private val ARC_COLOR_PERCENTAGE = 0.85

    // latlngbounds to zoom
    private val LN2 = 0.6931471805599453
    private val WORLD_PX_HEIGHT = 256f
    private val WORLD_PX_WIDTH = 256f
    private val ZOOM_MAX = 19

    // google maps patterns
    private val POLYGON_STROKE_WIDTH_PX = 8
    private val PATTERN_DASH_LENGTH_PX = 20f
    private val PATTERN_GAP_LENGTH_PX = 20f
    private val DASH: PatternItem = Dash(PATTERN_DASH_LENGTH_PX)
    private val GAP: PatternItem = Gap(PATTERN_GAP_LENGTH_PX)

    // create a stroke pattern of a gap followed by a dash
    private val PATTERN_POLYGON_ALPHA: List<PatternItem> =
        Arrays.asList(GAP, DASH)

    // urls
    private val GOOGLE_API_GEOCODE_LATLNG_URL =
        "https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&sensor=true&language=%s&client=%s"
    private val GOOGLE_API_GEOCODE_ADDRESS_URL =
        "https://maps.googleapis.com/maps/api/geocode/json?key=%s&address=%s"
    private val GOOGLE_API_DISTANCE_DRIVING_URL =
        "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s,%s&destinations=%s,%s&mode=driving&departure_time=%s&units=%s&language=US&client=%s"
    private val GOOGLE_API_DISTANCE_WALKING_URL =
        "https://maps.googleapis.com/maps/api/distancematrix/json?origins=%s,%s&destinations=%s,%s&mode=walking&departure_time=%s&units=%s&language=US&client=%s"
    private val GOOGLE_API_PLACES_AUTOCOMPLETE_URL =
        "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&location=%s&radius=%s&components=country:us&key=%s"
    private val GOOGLE_API_PLACES_AUTOCOMPLETE_URL_NO_LOCATION =
        "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=%s&components=country:us&key=%s"
    private val GOOGLE_API_PLACES_DETAIL_URL =
        "https://maps.googleapis.com/maps/api/place/details/json?key=%s&placeid=%s"
    private val GOOGLE_API_DIRECTIONS_URL =
        "https://maps.googleapis.com/maps/api/directions/json?origin=%s&destination=%s&client=%s&mode=%s"
    private val GOOGLE_API_PLACES_NEARBY_URL =
        "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=%s&type=%s&sensor=true&key=%s"
    private val GOOGLE_API_PLACES_PHOTO_URL =
        "https://maps.googleapis.com/maps/api/place/photo?maxwidth=%s&maxheight=%s&photoreference=%s&key=%s"
    private val RESULTS_KEY = "results"
    private val RESULT_KEY = "result"
    private val FORMATTED_ADDRESS_KEY = "formatted_address"
    private val ADDRESS_COMPONENTS_KEY = "address_components"
    private val PLACE_ID_KEY = "place_id"
    private val DESCRIPTION_KEY = "description"
    private val GEOMETRY_KEY = "geometry"
    private val LOCATION_KEY = "location"
    private val TYPES_KEY = "types"
    private val ELEMENTS_KEY = "elements"
    private val ROWS_KEY = "rows"
    private val SHORT_NAME_KEY = "short_name"
    private val LONG_NAME_KEY = "long_name"
    private val PREDICTIONS_KEY = "predictions"
    private val STREET_ADDRESS_TYPE = "street_address"
    private val ROUTE_TYPE = "route"
    private val PREMISE_TYPE = "premise"
    private val KEY_ROUTES = "routes"
    private val KEY_OVERVIEW_POLYLINE = "overview_polyline"
    private val KEY_POLYLINE = "polyline"
    private val KEY_POINTS = "points"
    private val KEY_DURATION_IN_TRAFFIC = "duration_in_traffic"
    private val KEY_TEXT = "text"
    private val KEY_LEGS = "legs"
    private val KEY_STEPS = "steps"
    private val KEY_MANEUVER = "maneuver"
    private val KEY_DISTANCE = "distance"
    private val KEY_DURATION = "duration"
    private val KEY_END_LOCATION = "end_location"
    private val KEY_START_LOCATION = "start_location"
    private val KEY_INSTRUCTIONS = "html_instructions"
    private val KEY_LAT = "lat"
    private val KEY_LNG = "lng"
    private val KEY_TRAVEL_MODE_DRIVING = "driving"
    private val KEY_TRAVEL_MODE_WALKING = "walking"
    private var alRainbowPoints: MutableList<RainbowPoint>? = mutableListOf()
    private var rainbowPolyline: RainbowPolyline? = null
    private var rainbowGroundOverlay: RainbowGroundOverlayUtils? = null

    // json object for street address
    private var streetAddress: JSONObject? = null

    /**
     * Method is used to get the address based on latitude and longitude coordinates.
     *
     * @param latLng An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees.
     * @param listener Callback for when address is retrieved.
     */
    override fun getAddress(
        latLng: LatLng,
        listener: AddressListener?
    ) {
        try {
            val url = format(
                GOOGLE_API_GEOCODE_LATLNG_URL, latLng.latitude, latLng.longitude,
                Locale.getDefault().country, ConfigurationManager.GOOGLE_CLIENT_KEY
            )


        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    /**
     * Method is used to get address using an address String value.
     *
     * @param address The address of the latitude and longitude coordinates location.
     * @param listener Callback for when address is retrieved.
     */
    @Throws(UnsupportedEncodingException::class)
    override fun getAddress(
        address: String,
        listener: AddressListener?
    ) {
        try {
            val url = format(
                GOOGLE_API_GEOCODE_ADDRESS_URL, ConfigurationManager.GOOGLE_API_KEY,
                URLEncoder.encode(address, "utf8")
            )
            // TODO make GET request. Uses PLACES_DETAIL_RESULT_CODE
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

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
    override fun getDistanceEta(
        origin: LatLng,
        destination: LatLng,
        unit: String?,
        listener: DistanceListener?
    ) {
        try {
            val departureTime = System.currentTimeMillis() / 1000
            val url = format(
                GOOGLE_API_DISTANCE_DRIVING_URL,
                origin.latitude,
                origin.longitude,
                destination.latitude,
                destination.longitude,
                departureTime,
                unit,
                ConfigurationManager.GOOGLE_CLIENT_KEY
            )

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

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
    @Throws(UnsupportedEncodingException::class)
    override fun getDistance(
        origin: LatLng,
        destination: LatLng,
        isDriving: Boolean,
        listener: DistanceListener?
    ) {
        try {
            val departureTime = System.currentTimeMillis() / 1000
            val url: String = if (isDriving) {
                format(
                    GOOGLE_API_DISTANCE_DRIVING_URL, origin.latitude, origin.longitude,
                    destination.latitude, destination.longitude, departureTime,
                    DISTANCE_MATRIX_UNIT_METRIC, ConfigurationManager.GOOGLE_CLIENT_KEY
                )
            } else {
                format(
                    GOOGLE_API_DISTANCE_WALKING_URL, origin.latitude, origin.longitude,
                    destination.latitude, destination.longitude, departureTime,
                    DISTANCE_MATRIX_UNIT_METRIC, ConfigurationManager.GOOGLE_CLIENT_KEY
                )
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

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
    @Throws(UnsupportedEncodingException::class)
    override fun getPlacesAutoComplete(
        input: String?,
        latLng: LatLng?,
        radius: Int,
        listener: PlacesAutoCompleteListener?
    ) {
        val url: String = if (latLng == null) {
            format(
                GOOGLE_API_PLACES_AUTOCOMPLETE_URL_NO_LOCATION,
                URLEncoder.encode(input, "utf8"), ConfigurationManager.GOOGLE_API_KEY
            )
        } else {
            format(
                GOOGLE_API_PLACES_AUTOCOMPLETE_URL,
                URLEncoder.encode(input, "utf8"),
                latLng,
                radius,
                ConfigurationManager.GOOGLE_API_KEY
            )
        }
        // TODO make GET request using PLACES_DETAIL_RESULT_CODE
    }

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
    @Throws(UnsupportedEncodingException::class)
    override fun getNearbyPlaces(
        latLng: LatLng,
        radius: Int,
        type: String?
    ) {
        val url = format(
            GOOGLE_API_PLACES_NEARBY_URL,
            latLng,
            radius,
            type,
            ConfigurationManager.GOOGLE_API_KEY
        )
        // TODO make GET request using PLACES_DETAIL_RESULT_CODE
    }

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
    override fun getPlacesPhotoUrl(
        photoReference: String?,
        maxWidth: Int?,
        maxHeight: Int?
    ): String {
        return format(
            GOOGLE_API_PLACES_PHOTO_URL,
            maxWidth,
            maxHeight,
            photoReference,
            ConfigurationManager.GOOGLE_API_KEY
        )
    }

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
    override fun getPlacesDetail(
        placeId: String,
        listener: AddressListener?
    ) {
        val url = format(
            GOOGLE_API_PLACES_DETAIL_URL,
            ConfigurationManager.GOOGLE_API_KEY,
            placeId
        )
        // TODO make GET request using PLACES_DETAIL_RExSULT_CODE

        // TODO logic for when response is received
//                try {
//                    val json = response.getJSONObject(RESULT_KEY)
//                    val addressComponents = json.getJSONArray(ADDRESS_COMPONENTS_KEY)
//                    // TODO have to make it so that Address does not accept a Parcel
//                    val address = Address()
//                    address.streetNumber = parseAddressComponents(
//                        addressComponents,
//                        "street_number"
//                    )
//                    address.addressLine1 = parseAddressComponents(
//                        addressComponents,
//                        "route"
//                    )
//                    address.latitude = json.optJSONObject(GEOMETRY_KEY).optJSONObject(LOCATION_KEY)
//                        .optDouble("lat")
//                    address.longitude = json.optJSONObject(GEOMETRY_KEY).optJSONObject(LOCATION_KEY)
//                        .optDouble("lng")
//                    if (address.streetNumber?.isEmpty() == true ||
//                        address.addressLine1?.isEmpty() == true
//                    ) {
//                        // if street number or street name are empty,
//                        // try getting these details by reverse geocoding
//                        getAddress(context, LatLng(address.latitude, address.longitude), listener)
//                    } else {
//                        address.city = parseAddressComponents(addressComponents, "locality")
//                        if (address.city == null) {
//                            address.city = parseAddressComponents(addressComponents, "sublocality")
//                        }
//                        if (address.city == null) {
//                            address.city = parseAddressComponents(addressComponents, "neighborhood")
//                        }
//                        address.stateCode =
//                            parseAddressComponents(addressComponents, "administrative_area_level_1")
//                        address.postalCode =
//                            parseAddressComponents(addressComponents, "postal_code")
//                        address.countryCode = parseAddressComponents(addressComponents, "country")
//                        address.formattedAddress =
//                            json.optString(FORMATTED_ADDRESS_KEY).replace("" + 65532.toChar(), "")
//                                .trim { it <= ' ' }
//                        listener.onAddressResponse(address)
//                    }
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                    listener.onAddressResponse(null)
//                } catch (e: NullPointerException) {
//                    e.printStackTrace()
//                    listener.onAddressResponse(null)
//                }
    }

    /**
     * Method is used for Google Directions API calls.
     *
     * Ref-
     * https://developers.google.com/maps/documentation/directions/get-directions
     *
     * @param origin An immutable class representing a pair of latitude and longitude
     * coordinates, stored as degrees (Origin).
     * @param destination An immutable class representing a pair of latitude and longitude
     * coordinates, stored as degrees (Destination).
     * @param listener Callback for when directions are retrieved.
     */
    override fun getDirections(
        origin: String,
        destination: String,
        listener: DirectionsListener?
    ) {
        try {
            val url = format(
                GOOGLE_API_DIRECTIONS_URL, origin, destination,
                ConfigurationManager.GOOGLE_CLIENT_KEY, KEY_TRAVEL_MODE_DRIVING
            )

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

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
    override fun getTurnByTurnDirections(
        origin: LatLng,
        destination: LatLng,
        travelMode: TravelMode,
        listener: TurnByTurnListener?
    ) {
        try {
            val url = format(
                GOOGLE_API_DIRECTIONS_URL, origin, destination,
                ConfigurationManager.GOOGLE_CLIENT_KEY, travelMode.toString()
            )


            // TODO logic for when response is received
//                    try {
//                        val alTurnByTurn: ArrayList<TurnByTurn> = ArrayList<TurnByTurn>()
//                        var overviewPolyline: ArrayList<LatLng> = ArrayList<LatLng>()
//                        val routesObject = response.getJSONArray(KEY_ROUTES)
//                        if (routesObject.length() > 0) {
//                            // get legs object from first route
//                            val legsObject = routesObject.getJSONObject(0).getJSONArray(KEY_LEGS)
//                            if (legsObject.length() > 0) {
//                                // get steops object from first leg
//                                val stepsObject =
//                                    legsObject.getJSONObject(0).getJSONArray(KEY_STEPS)
//                                if (stepsObject.length() > 0) {
//                                    for (i in 0 until stepsObject.length()) {
//                                        // create turn by turn model object
//                                        val turnByTurnModel = TurnByTurn()
//                                        // current step distance
//                                        turnByTurnModel.currentStepDistance =
//                                            stepsObject.getJSONObject(i).getJSONObject(KEY_DISTANCE)
//                                                .getString(KEY_TEXT)
//                                        // current step duration
//                                        turnByTurnModel.currentStepDuration =
//                                            stepsObject.getJSONObject(i).getJSONObject(KEY_DURATION)
//                                                .getString(KEY_TEXT)
//
//                                        // current step start and end locations
//                                        val startLocation = stepsObject.getJSONObject(i)
//                                            .getJSONObject(KEY_START_LOCATION)
//                                        val endLocation = stepsObject.getJSONObject(i)
//                                            .getJSONObject(KEY_END_LOCATION)
//                                        turnByTurnModel.startLocation = LatLng(
//                                            startLocation.getString(KEY_LAT).toDouble(),
//                                            startLocation.getString(KEY_LNG).toDouble()
//                                        )
//                                        turnByTurnModel.endLocation = LatLng(
//                                            endLocation.getString(KEY_LAT).toDouble(),
//                                            endLocation.getString(KEY_LNG).toDouble()
//                                        )
//
//                                        // current step instructions
//                                        turnByTurnModel.instructions =
//                                            stepsObject.getJSONObject(i).getString(KEY_INSTRUCTIONS)
//                                                .replace("[<](/)?div[^>]*[>]".toRegex(), " ")
//                                        try {
//                                            // current step polyline
//                                            val polyline = stepsObject.getJSONObject(i)
//                                                .getJSONObject(KEY_POLYLINE)
//                                            val encodedPoints = polyline.getString(KEY_POINTS)
//                                            turnByTurnModel.polyline = decode(
//                                                if (encodedPoints.isNotEmpty()) {
//                                                    encodedPoints
//                                                } else {
//                                                    ""
//                                                }
//                                            ) as ArrayList<LatLng>
//                                        } catch (e: JSONException) {
//                                            e.printStackTrace()
//                                        } catch (e: NullPointerException) {
//                                            e.printStackTrace()
//                                        }
//                                        try {
//                                            // current step maneuver (direction)
//                                            turnByTurnModel.maneuver = if (
//                                                stepsObject.getJSONObject(i) != null &&
//                                                stepsObject.getJSONObject(i).getString(
//                                                    KEY_MANEUVER
//                                                ).isNotEmpty()
//                                            ) {
//                                                stepsObject.getJSONObject(i).getString(
//                                                    KEY_MANEUVER
//                                                )
//                                            } else {
//                                                ""
//                                            }
//                                        } catch (e: JSONException) {
//                                            e.printStackTrace()
//                                        }
//
//                                        // add current step to list
//                                        alTurnByTurn.add(turnByTurnModel)
//                                    }
//                                }
//                            }
//                            val currentRoute = routesObject.getJSONObject(0)
//                            val overViewPolyline = currentRoute.getJSONObject(KEY_OVERVIEW_POLYLINE)
//                            val encodedPoints = overViewPolyline.getString(KEY_POINTS)
//                            overviewPolyline = decode(if (encodedPoints.isNotEmpty()) {
//                                encodedPoints
//                            } else {
//                                ""
//                            }) as ArrayList<LatLng>
//                        }
//                        listener.onSuccess(alTurnByTurn, overviewPolyline)
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    }

        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    /**
     * Method is used to populate a JSONArray of address component keys.
     *
     * @param jsonArray A dense indexed sequence of values.
     * @param isLatLng True if a LatLng was used for the [getAddress] request, false if a
     * Strings address was used for the [getAddress] request.
     * @return A JSONArray containing only the address_components part of the original JSONArray.
     */
    private fun getAddressComponents(
        jsonArray: JSONArray,
        isLatLng: Boolean
    ): JSONArray? {
        if (streetAddress != null) {
            return streetAddress?.optJSONArray(ADDRESS_COMPONENTS_KEY)
        }
        val length = jsonArray.length()
        if (isLatLng) {
            for (i in 0 until length) {
                val json = jsonArray.optJSONObject(i)
                if (isCompatibleType(json.optJSONArray(TYPES_KEY))) {
                    streetAddress = json
                    return json.optJSONArray(ADDRESS_COMPONENTS_KEY)
                }
            }
        } else if (length > 0) {
            val json = jsonArray.optJSONObject(0)
            streetAddress = json
            return json.optJSONArray(ADDRESS_COMPONENTS_KEY)
        }
        return null
    }

    /**
     * Method is used to get formatted address.
     *
     * @param jsonArray A dense indexed sequence of values.
     * @param isLatLng True if a LatLng was used for the [getAddress] request, false if a
     * Strings address was used for the [getAddress] request.
     * @return A String containing only the formatted_address part of the original JSONArray.
     */
    private fun getFormattedAddress(
        jsonArray: JSONArray,
        isLatLng: Boolean
    ): String? {
        if (streetAddress != null) {
            return streetAddress?.optString(FORMATTED_ADDRESS_KEY)
        }
        val length = jsonArray.length()
        if (isLatLng) {
            for (i in 0 until length) {
                val json = jsonArray.optJSONObject(i)
                if (isStreetAddress(json.optJSONArray(TYPES_KEY))) {
                    streetAddress = json
                    return json.optString(FORMATTED_ADDRESS_KEY)
                }
            }
        } else if (length > 0) {
            val json = jsonArray.optJSONObject(0)
            streetAddress = json
            return json.optString(FORMATTED_ADDRESS_KEY)
        }
        return null
    }

    /**
     * Utility method to check if the types are either a street address, a route, or a premise.
     *
     * @param types A dense indexed sequence of values.
     * @return True if type is either street_address, route, or premise, otherwise false.
     */
    private fun isCompatibleType(
        types: JSONArray?
    ): Boolean {
        if (types != null) {
            val length = types.length()
            for (i in 0 until length) {
                val type = types.optString(i)
                if (type.equals(STREET_ADDRESS_TYPE, ignoreCase = true) ||
                    type.equals(ROUTE_TYPE, ignoreCase = true) ||
                    type.equals(PREMISE_TYPE, ignoreCase = true)
                ) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Utility method to check if street address.
     *
     * @param jsonArray A dense indexed sequence of values.
     * @return True if arr is a street_address, otherwise false.
     */
    private fun isStreetAddress(
        jsonArray: JSONArray? = null
    ): Boolean {
        if (jsonArray != null) {
            val length = jsonArray.length()
            for (i in 0 until length) {
                if (jsonArray.optString(i).equals(STREET_ADDRESS_TYPE, ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Method is used to parse street address.
     *
     * @param jsonArray A dense indexed sequence of values.
     * @param isLatLng True if a LatLng was used for the [getAddress] request,
     * false if a Strings address was used for the [getAddress] request.
     * @return An [Address] object.
     */
    private fun parseAddress(
        jsonArray: JSONArray,
        isLatLng: Boolean
    ): Address {
        val address = Address()
        address.formattedAddress = getFormattedAddress(
            jsonArray,
            isLatLng
        )
        address.streetNumber = parseAddressComponents(
            getAddressComponents(
                jsonArray,
                isLatLng
            ), "street_number"
        )
        address.addressLine1 = parseAddressComponents(
            getAddressComponents(
                jsonArray,
                isLatLng
            ), "route"
        )
        address.city = parseAddressComponents(
            getAddressComponents(
                jsonArray,
                isLatLng
            ), "locality"
        )
        if (address.city == null) {
            address.city = parseAddressComponents(
                getAddressComponents(
                    jsonArray,
                    isLatLng
                ), "sublocality"
            )
        }
        if (address.city == null) {
            address.city = parseAddressComponents(
                getAddressComponents(
                    jsonArray,
                    isLatLng
                ), "neighborhood"
            )
        }
        address.stateCode = parseAddressComponents(
            getAddressComponents(
                jsonArray, isLatLng
            ), "administrative_area_level_1"
        )
        address.postalCode = parseAddressComponents(
            getAddressComponents(
                jsonArray,
                isLatLng
            ), "postal_code"
        )
        address.countryCode = parseAddressComponents(
            getAddressComponents(
                jsonArray,
                isLatLng
            ), "country"
        )
        if (jsonArray.length() > 0) {
            address.placeId = jsonArray.optJSONObject(0)
                ?.optString(PLACE_ID_KEY)
            address.latitude = jsonArray.optJSONObject(0)
                ?.optJSONObject(GEOMETRY_KEY)
                ?.optJSONObject(LOCATION_KEY)
                ?.optDouble("lat") ?: 0.0
            address.longitude = jsonArray.optJSONObject(0)
                ?.optJSONObject(GEOMETRY_KEY)
                ?.optJSONObject(LOCATION_KEY)
                ?.optDouble("lng") ?: 0.0
        }
        return address
    }

    /**
     * Method is used to parse street address components.
     *
     * @param addressComponents The components that make up the address.
     * @param type Represents whether the type is locality, or sub-locality or other.
     *
     * Locality indicates an incorporated city or town political entity.
     * Sub-locality indicates a first-order civil entity below a locality.
     *
     * <p>For some locations may receive one of the additional types.</p>
     * @return The parsed address.
     */
    private fun parseAddressComponents(
        addressComponents: JSONArray?,
        type: String
    ): String? {
        if (addressComponents == null) {
            return null
        }
        val length = addressComponents.length()
        for (i in 0 until length) {
            val json = addressComponents.optJSONObject(i)
            val arrayLength = json?.optJSONArray(TYPES_KEY)?.length() ?: 0
            // TODO probably need to wrap this in a try-catch
            for (j in 0 until arrayLength) {
                if (json?.optJSONArray(TYPES_KEY)?.optString(j).equals(type, ignoreCase = true)) {
                    // use the long name if the key is for city name
                    return if (type.equals("locality", ignoreCase = true) ||
                        type.equals("sublocality", ignoreCase = true)
                    ) {
                        json.optString(LONG_NAME_KEY).replace("" + 65532.toChar(), "")
                            .trim { it <= ' ' }
                    } else {
                        json.optString(SHORT_NAME_KEY).replace("" + 65532.toChar(), "")
                            .trim { it <= ' ' }
                    }
                }
            }
        }
        return null
    }

    /**
     * Method is used to parse places from Google Places API.
     *
     * The Google Places API Web Service allows you to query for place information on a
     * variety of categories, such as: establishments, prominent points of interest,
     * geographic locations, and more.
     *
     * @param predictions User input to retrieve list of relevant place information.
     * @return List of Place objects.
     */
    private fun parsePlaces(
        predictions: JSONArray
    ): List<Place>? {
        val length = predictions.length()
        if (length == 0) {
            return null
        }
        val places: MutableList<Place> = ArrayList<Place>()
        for (i in 0 until length) {
            val json = predictions.optJSONObject(i)
            val place = Place()
            place.placeId = json.optString(PLACE_ID_KEY)
            place.description = json.optString(DESCRIPTION_KEY)
            places.add(place)
        }
        return places
    }

    /**
     * Method is used to encode the path created from a list of latitude and
     * longitude coordinates.
     *
     * @param encodedPoints Location points to decode.
     * @return List of LatLng objects.
     */
    private fun decode(
        encodedPoints: String
    ): List<LatLng> {
        val length = encodedPoints.length

        // for speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning
        val path: MutableList<LatLng> = ArrayList<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < length) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPoints[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPoints[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }

    /**
     * Method is used to check Google Play Services.
     *
     * @param activity An activity is a single, focused thing that the user can do.
     * @return True of Google Play Service is available, otherwise false.
     */
    fun checkGooglePlaySevices(
        activity: Activity
    ): Boolean {
        val playServicesResolutionRequest = 9000
        val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode: Int = apiAvailability.isGooglePlayServicesAvailable(activity)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    activity,
                    resultCode,
                    playServicesResolutionRequest
                ).show()
            } else {
                Log.i(Constants.TAG, "This device is not supported.")
            }
            return false
        }
        return true
    }

    /**
     * Method is used to create polyline options.
     *
     * @param context Interface to global information about an application environment.
     * @param strokeColor The desired color of the outline (resource ID).
     * @param fillColor The desired color of the inside (resource ID).
     * @param polygon List of polygons that define the area places need to be in.
     * @return The created [PolylineOptions] object.
     */
    fun createPolygonOptions(
        context: Context,
        strokeColor: Int,
        fillColor: Int,
        polygon: ArrayList<LatLng>
    ): PolygonOptions? {
        val options: PolygonOptions = PolygonOptions().strokeColor(
            ContextCompat.getColor(
                context, strokeColor
            )
        )
            .strokeWidth(POLYGON_STROKE_WIDTH_PX.toFloat())
            .fillColor(ContextCompat.getColor(context, fillColor)).geodesic(true)
        return if (polygon.size > 0) {
            options.addAll(polygon)
        } else {
            null
        }
    }

    /**
     * Method is used to create rainbow polyline.
     *
     * @param context Interface to global information about an application environment.
     * @param color The desired color of the polyline (resource ID).
     * @param width The desired width of the polyline in pixels.
     * @param polyline The list of [LatLng] to be used for the polyline.
     * @return The created [PolylineOptions] object.
     */
    fun createPolylineOptions(
        context: Context,
        color: Int,
        width: Int,
        polyline: ArrayList<LatLng>
    ): PolylineOptions? {
        val options: PolylineOptions = PolylineOptions().color(
            ContextCompat.getColor(
                context,
                color
            )
        ).width(width.toFloat())
        return if (polyline.size > 0) {
            options.addAll(polyline)
        } else {
            null
        }
    }

    /**
     * Method is used to create rainbow polyline (ARC).
     *
     * @param context Interface to global information about an application environment.
     * @param overlayUtils The [RainbowGroundOverlayUtils] to be used.
     * @param origin An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Origin).
     * @param dest An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Destination).
     * @param colorStartEnd The desired start and end color of the polyline (resource ID).
     * @param colorMid The desired middle color of the polyline (resource ID).
     */
    fun createRainbowPolylineArc(
        context: Context,
        overlayUtils: RainbowGroundOverlayUtils,
        origin: LatLng,
        dest: LatLng,
        colorStartEnd: Int,
        colorMid: Int,
        strokeWidth: Int
    ) {
        // remove old points
        clearRainbowPolyline()

        // retrieve rainbow points
        alRainbowPoints = createRainbowArcPoints(context, origin, dest, colorStartEnd, colorMid)
        rainbowGroundOverlay = overlayUtils

        // create rainbow polyline
        val options: RainbowPolylineOptions = RainbowPolylineOptions(null)
            .zIndex(0).strokeWidth(
                if (strokeWidth <= 0) {
                    DEFAULT_STROKE_WIDTH
                } else {
                    strokeWidth
                }
            )
            .strokeColor(Color.BLACK)
            .linearGradient(true)
            .add(alRainbowPoints.orEmpty())

        // create polyline
        rainbowPolyline = options.build()
        rainbowGroundOverlay?.addPolylineShape(rainbowPolyline)
    }

    /**
     * Method is used to create rainbow polyline (STRAIGHT LINE)
     *
     * @param context Interface to global information about an application environment.
     * @param overlayUtils The [RainbowGroundOverlayUtils] to be used.
     * @param origin An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Origin).
     * @param dest An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Destination).
     * @param colorStart The desired start color of the polyline (resource ID).
     * @param colorMid The desired middle color of the polyline (resource ID).
     * @param colorEnd The desired end color of the polyline (resource ID).
     */
    fun createRainbowPolyline(
        context: Context,
        overlayUtils: RainbowGroundOverlayUtils,
        origin: LatLng,
        dest: LatLng,
        colorStart: Int,
        colorMid: Int,
        colorEnd: Int,
        strokeWidth: Int
    ) {
        // remove old points
        clearRainbowPolyline()

        // create new list of rainbow points
        val alPoints: ArrayList<RainbowPoint> = ArrayList<RainbowPoint>()
        alPoints.add(
            RainbowPoint(LatLng(origin.latitude, origin.longitude))
                .color(ContextCompat.getColor(context, colorStart))
        )
        alPoints.add(
            RainbowPoint(LatLng(dest.latitude, dest.longitude))
                .color(ContextCompat.getColor(context, colorEnd))
        )
        // add middle color if exists
        if (colorMid != 0) {
            // calculate distance and heading between two points
            val distance: Double = SphericalUtil.computeDistanceBetween(origin, dest)
            val heading: Double = SphericalUtil.computeHeading(origin, dest)
            // midpoint position
            val midpoint: LatLng = SphericalUtil.computeOffset(
                origin,
                distance * 0.5,
                heading
            )
            alPoints.add(
                RainbowPoint(
                    LatLng(
                        midpoint.latitude,
                        midpoint.longitude
                    )
                ).color(
                    ContextCompat.getColor(
                        context,
                        colorMid
                    )
                )
            )
        }
        // retrieve rainbow points
        alRainbowPoints = alPoints
        rainbowGroundOverlay = overlayUtils

        // create rainbow polyline
        val options: RainbowPolylineOptions = RainbowPolylineOptions(null)
            .zIndex(0).strokeWidth(
                if (strokeWidth <= 0) {
                    DEFAULT_STROKE_WIDTH
                } else {
                    strokeWidth
                }
            ).strokeColor(
                Color.BLACK
            ).linearGradient(true).add(alRainbowPoints)

        // create polyline
        rainbowPolyline = options.build()
        rainbowGroundOverlay?.addPolylineShape(rainbowPolyline)
    }

    /**
     * Method is used to generate points between origin and destination and reposition as
     * Bezier curve.
     *
     * @param context Interface to global information about an application environment
     * @param origin An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Origin).
     * @param dest An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees (Destination).
     * @param colorStartEnd The start and end color of the polyline.
     * @param colorMid  The mid color of the polyline.
     * @return List of [RainbowPoint] that make up the rainbow arc polyline.
     */
    private fun createRainbowArcPoints(
        context: Context,
        origin: LatLng,
        dest: LatLng,
        colorStartEnd: Int,
        colorMid: Int
    ): ArrayList<RainbowPoint> {
        // create new list of rainbow points
        val alPoints: ArrayList<RainbowPoint> = ArrayList<RainbowPoint>()

        // calculate distance and heading between two points
        val distance: Double = SphericalUtil.computeDistanceBetween(origin, dest)
        val heading: Double = SphericalUtil.computeHeading(origin, dest)

        // midpoint position
        val midpoint: LatLng = SphericalUtil.computeOffset(origin, distance * 0.5, heading)

        // calculate arc threshold
        val arcThreshold = computeArcThreshold(distance)

        // calculate position of the circle center
        val x = (1 - arcThreshold * arcThreshold) * distance * 0.5 / (2 * arcThreshold)
        val r = (1 + arcThreshold * arcThreshold) * distance * 0.5 / (2 * arcThreshold)

        // calculate offset
        val offset: LatLng = if (heading > 0) {
            SphericalUtil.computeOffset(midpoint, x, heading + 90.0)
        } else {
            SphericalUtil.computeOffset(midpoint, x, heading - 90.0)
        }

        // calculate heading between circle center and two points
        val headingA: Double = SphericalUtil.computeHeading(offset, origin)
        val headingB: Double = SphericalUtil.computeHeading(offset, dest)

        // calculate number of points
        val numBezierCurvePoints = computeNumberOfCurvePoints(distance)

        // calculate positions of points on circle border and add them to polyline options
        val step = (headingB - headingA) / numBezierCurvePoints
        for (i in 0 until numBezierCurvePoints) {
            // add origin point
            if (i == 0) {
                alPoints.add(
                    RainbowPoint(LatLng(origin.latitude, origin.longitude))
                        .color(ContextCompat.getColor(context, colorStartEnd))
                )
            }
            val pi: LatLng = SphericalUtil.computeOffset(offset, r, headingA + i * step)
            if (i > numBezierCurvePoints * ARC_COLOR_PERCENTAGE ||
                i < numBezierCurvePoints - numBezierCurvePoints * ARC_COLOR_PERCENTAGE
            ) {
                alPoints.add(
                    RainbowPoint(LatLng(pi.latitude, pi.longitude))
                        .color(ContextCompat.getColor(context, colorStartEnd))
                )
            } else {
                alPoints.add(
                    RainbowPoint(LatLng(pi.latitude, pi.longitude))
                        .color(ContextCompat.getColor(context, colorMid))
                )
            }

            // add destination point
            if (i == numBezierCurvePoints - 1) {
                alPoints.add(
                    RainbowPoint(LatLng(dest.latitude, dest.longitude))
                        .color(ContextCompat.getColor(context, colorStartEnd))
                )
            }
        }
        return alPoints
    }

    /**
     * Method is used to compute the number of curve points to be generated based on
     * the distance between pickup and dropoff locations.
     *
     * @param distance The distance between two points.
     * @return The number of generated points necessary to create the rainbow arc polyline.
     */
    private fun computeNumberOfCurvePoints(
        distance: Double
    ): Int {
        if (distance >= 0 && distance < 250) {
            return 30
        } else if (distance >= 250 && distance < 500) {
            return 35
        } else if (distance >= 500 && distance < 750) {
            return 40
        } else if (distance >= 750 && distance < 1000) {
            return 45
        }
        return 50
    }

    /**
     * Method is used to compute the arc threshold based on the distance between
     * pickup and dropoff locations.
     *
     * @param distance The distance between two points.
     * @return Value that represents the arch of the bezier curve.
     *
     * The higher the value, the more the curve will become a full circle.
     */
    private fun computeArcThreshold(
        distance: Double
    ): Double {
        // the arch of the bezier curve
        // The higher the value, the more it becomes a full circle
        if (distance >= 0 && distance < 500) {
            return 0.35
        } else if (distance >= 500 && distance < 1000) {
            return 0.32
        }
        return 0.28
    }

    /**
     * Method is used to style polygon based on type.
     *
     * @param context Interface to global information about an application environment.
     * @param strokeColor Line segment color in ARGB format.
     * @param fillColor Fill color in ARGB format.
     * @param polygon List of polygons that define the area places need to be in.
     */
    fun stylePolygon(
        context: Context,
        strokeColor: Int,
        fillColor: Int,
        polygon: Polygon
    ) {
        polygon.strokeColor = ContextCompat.getColor(context, strokeColor)
        polygon.fillColor = ContextCompat.getColor(context, fillColor)
        polygon.strokeWidth = POLYGON_STROKE_WIDTH_PX.toFloat()
        if (polygon.tag != null) {
            // dash polygon pattern
            if (polygon.tag == PolygonPattern.DASH) {
                polygon.strokePattern = PATTERN_POLYGON_ALPHA
            }
        }
    }

    /**
     * Method is used to check if rainbow polyline is visible.
     *
     * @return True if rainbow arc polyline is visible, otherwise false.
     */
    fun isRainbowPolylineVisible(): Boolean {
        return rainbowPolyline != null && alRainbowPoints?.isNotEmpty() == true
    }

    /**
     * Method is used to remove a list of polygons from the map.
     *
     * @param alPolygon List of polygons to remove.
     */
    fun removePolygons(
        vararg alPolygon: ArrayList<Polygon>
    ) {
        for (poly in alPolygon) {
            for (i in poly.indices) {
                poly[i].remove()
            }
        }
    }

    /**
     * Method is used to remove a list of map markers.
     *
     * @param alMarkers List of map markers to remove.
     */
    @SafeVarargs
    fun removeMapMarkers(
        vararg alMarkers: ArrayList<Marker>
    ) {
        for (marker in alMarkers) {
            for (i in marker.indices) {
                marker[i].remove()
            }
        }
    }

    /**
     * Method is used to clear rainbow polyline
     */
    fun clearRainbowPolyline() {
        if (rainbowPolyline != null && alRainbowPoints?.isNotEmpty() == true) {
            rainbowGroundOverlay?.removePolylineShape(rainbowPolyline)
            rainbowGroundOverlay?.refresh()

            // clear/null objects
            alRainbowPoints?.clear()
            alRainbowPoints = null
            rainbowGroundOverlay = null
            rainbowPolyline = null

            // invoke garbage collector
            System.gc()
        }
    }

    /**
     * Method is used to confirm if two given latlngs are the same.
     *
     * @param latlngA An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees.
     * @param latlngB An immutable class representing a pair of latitude and longitude coordinates,
     * stored as degrees.
     * @return True if the inputted LatLng objects are not the same location.
     */
    fun isLatLngSame(
        latlngA: LatLng,
        latlngB: LatLng
    ): Boolean {
        return latlngA.latitude == latlngB.latitude && latlngA.longitude == latlngB.longitude
    }

    /**
     * Method is used to get a value for zoom level based on a given LatLngBounds and
     * map dimensions.
     *
     * @param context Interface to global information about an application environment.
     * @param bounds The [LatLngBounds] to be used.
     * @param mapWidthPx The width of the MapFragment in pixels.
     * @param mapHeightPx The height of the MapFragment in pixels.
     * @return The calculated zoom level.
     */
    fun getBoundsZoomLevel(
        context: Context,
        bounds: LatLngBounds,
        mapWidthPx: Int,
        mapHeightPx: Int
    ): Float {
        val ne: LatLng = bounds.northeast
        val sw: LatLng = bounds.southwest
        val latFraction = (latRad(ne.latitude) - latRad(sw.latitude)) / Math.PI
        val lngDiff: Double = ne.longitude - sw.longitude
        val lngFraction = (if (lngDiff < 0) lngDiff + 360 else lngDiff) / 360
        val latZoom = zoom(
            mapHeightPx,
            DeviceUtils.convertDpToPixels(context, WORLD_PX_HEIGHT).toInt(), latFraction
        )
        val lngZoom = zoom(
            mapWidthPx,
            DeviceUtils.convertDpToPixels(context, WORLD_PX_WIDTH).toInt(), lngFraction
        )
        val result = latZoom.coerceAtMost(lngZoom)
        return result.coerceAtMost(ZOOM_MAX.toDouble()).toFloat()
    }

    /**
     * Method is used to update N, S, E, W direction instructions to represent
     * North, South, East, West.
     *
     * @param directions The turn by turn directions.
     * @return Directions with abbreviated N, S, E, W values spelled out.
     */
    @SuppressLint("NewApi")
    fun sanitizeDirections(directions: String): String {
        // deconstruct directions
        val strArry = directions.split(" ").toTypedArray()
        for (i in strArry.indices) {
            if (strArry[i].length == 1 || strArry[i].length > 1 && strArry[i][1] == '\n') {
                val c = Character.toLowerCase(strArry[i][0])
                if (c in 'a'..'z') {
                    val builder = StringBuilder()
                    when (c) {
                        'n' -> {
                            // check north and replace with full spelling
                            strArry[i] = builder.append("North")
                                .append(if (strArry[i].length > 1) strArry[i].substring(1) else "")
                                .toString()
                        }
                        's' -> {
                            // check south and replace with full spelling
                            strArry[i] = builder.append("South")
                                .append(if (strArry[i].length > 1) strArry[i].substring(1) else "")
                                .toString()
                        }
                        'e' -> {
                            // check east and replace with full spelling
                            strArry[i] = builder.append("East")
                                .append(if (strArry[i].length > 1) strArry[i].substring(1) else "")
                                .toString()
                        }
                        'w' -> {
                            // check west and replace with full spelling
                            strArry[i] = builder.append("West")
                                .append(if (strArry[i].length > 1) strArry[i].substring(1) else "")
                                .toString()
                        }
                    }
                }
            }
        }

        // construct directions
        val stringBuilder = StringBuilder()
        for (i in strArry.indices) {
            if (i > 0) {
                stringBuilder.append(" ")
            }
            stringBuilder.append(strArry[i])
        }
        return stringBuilder.toString()
    }

    /**
     * Method is used to convert latitude to radians.
     *
     * @param lat The latitude value to convert.
     * @return The converted value in radians.
     */
    private fun latRad(
        lat: Double
    ): Double {
        val sin = sin(lat * Math.PI / 180)
        val radX2 = ln((1 + sin) / (1 - sin)) / 2
        return radX2.coerceAtMost(Math.PI).coerceAtLeast(-Math.PI) / 2
    }

    /**
     * Method is used to compute the appropriate zoom level.
     *
     * @return The computed zoom value.
     */
    private fun zoom(
        mapPx: Int,
        worldPx: Int,
        fraction: Double
    ): Double {
        return ln(mapPx / worldPx / fraction) / LN2
    }

    /**
     * Enum used for polygon pattern
     */
    enum class PolygonPattern {
        DASH, SOLID
    }
}