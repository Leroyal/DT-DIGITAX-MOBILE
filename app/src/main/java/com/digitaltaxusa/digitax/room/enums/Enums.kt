package com.digitaltaxusa.digitax.room.enums

class Enums {

    /**
     * Enum is used to categorize the type of trip e.g. business, personal, unclassified.
     *
     * @property type String The type of trip e.g. business, personal, unclassified.
     * @constructor
     */
    enum class TripType(private val type: String) {
        BUSINESS("business"),
        PERSONAL("personal"),
        UNCLASSIFIED("unclassified");

        override fun toString(): String {
            return type
        }
    }

    /**
     * Enum is used to categorize how users login e.g. biometric or email.
     *
     * @property type String How users login e.g. biometric or email.
     * @constructor
     */
    enum class LoginType(private val type: String) {
        BIOMETRIC("biometric"),
        EMAIL("email");

        override fun toString(): String {
            return type
        }
    }

    /**
     * Enum is used to dictate Room database operations to perform.
     */
    enum class DatabaseOperation {
        INSERT,
        UPDATE,
        DELETE,
        DELETE_ALL
    }
}