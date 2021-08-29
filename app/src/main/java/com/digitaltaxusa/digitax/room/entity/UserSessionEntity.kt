package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity for tracking user session information.
 *
 * @property uid Int Marks a field in an Entity as the primary key.
 * @property firstName String? User first name. This information syncs to backend/web.
 * @property lastName String? User last name. This information syncs to backend/web.
 * @property name String? User full name e.g. first, middle, last. This information syncs
 * to backend/web.
 * @property username String? User username for authentication.
 * @property emailAddress String? User email for authentication.
 * @property phoneNumber String? User phone number. This information syncs to backend/web.
 * @property homeAddress String? User home address. This information syncs to backend/web.
 * @property workAddress String? User work address. This information syncs to backend/web.
 * @property isEmailVerified Boolean? True if the email is verified, otherwise false.
 * @property isPhoneVerified Boolean? True if the phone number is verified, otherwise false.
 * @property loginType String? The login type enumeration e.g. biometric or email.
 * @property accessToken String? The session token.
 * @property accessTokenExpiration Long? The expiration for the session token.
 * @property deviceId String? The device unique identifier.
 */
@Entity(tableName = "user_session_table")
class UserSessionEntity {
    @PrimaryKey
    var uid = 1

    @ColumnInfo(name = "first_name")
    var firstName: String? = null

    @ColumnInfo(name = "last_name")
    var lastName: String? = null

    @ColumnInfo(name = "name")
    var name: String? = null

    @ColumnInfo(name = "username")
    var username: String? = null

    @ColumnInfo(name = "email_address")
    var emailAddress: String? = null

    @ColumnInfo(name = "phone_number")
    var phoneNumber: String? = null

    @ColumnInfo(name = "home_address")
    var homeAddress: String? = null

    @ColumnInfo(name = "work_address")
    var workAddress: String? = null

    @ColumnInfo(name = "is_email_verified")
    var isEmailVerified: Boolean? = null

    @ColumnInfo(name = "is_phone_verified")
    var isPhoneVerified: Boolean? = null

    // [LoginType] enumeration e.g. biometric or email
    @ColumnInfo(name = "login_type")
    var loginType: String? = null

    // session token
    @ColumnInfo(name = "access_token")
    var accessToken: String? = null

    @ColumnInfo(name = "access_token_expiration")
    var accessTokenExpiration: Long? = null // milliseconds

    @ColumnInfo(name = "device_id")
    var deviceId: String? = null
}