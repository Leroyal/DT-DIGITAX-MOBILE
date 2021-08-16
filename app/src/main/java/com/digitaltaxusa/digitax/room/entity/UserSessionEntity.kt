package com.digitaltaxusa.digitax.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

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
    @ColumnInfo(name = "device_id")
    var deviceId: String? = null
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
}