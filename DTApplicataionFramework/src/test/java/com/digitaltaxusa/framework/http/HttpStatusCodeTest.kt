package com.digitaltaxusa.framework.http

import com.digitaltaxusa.framework.http.response.HttpStatusCode
import org.junit.Assert
import org.junit.Test

const val MESSAGE_UNKNOWN_STATUS_CODE = "Unknown Status Code"

class HttpStatusCodeTest {

    @Test
    fun `verify that 200 status code is a successful HTTP request`() {
        Assert.assertTrue(HttpStatusCode.fromStatusCode(200).isSuccessful)
    }

    @Test
    fun `verify that 300 status code is a failed HTTP request`() {
        Assert.assertFalse(HttpStatusCode.fromStatusCode(300).isSuccessful)
    }

    @Test
    fun `verify that the same status codes are equal`() {
        Assert.assertTrue(HttpStatusCode.fromStatusCode(200) == HttpStatusCode.fromStatusCode(200))
    }

    @Test
    fun `verify that different status codes are not equal`() {
        Assert.assertFalse(HttpStatusCode.fromStatusCode(200) == HttpStatusCode.fromStatusCode(300))
    }

    @Test
    fun `verify that a know 200 status code gets correctly mapped`() {
        val successfulStatusCode = HttpStatusCode.fromStatusCode(200)
        Assert.assertTrue(successfulStatusCode.message == "OK")
        Assert.assertTrue(successfulStatusCode.code == 200)
    }

    @Test
    fun `verify that unknown status code gets mapped as an unknown error`() {
        val invalidStatusCode = HttpStatusCode.fromStatusCode(600)
        Assert.assertTrue(invalidStatusCode.code == 600)
        Assert.assertTrue(invalidStatusCode.message == MESSAGE_UNKNOWN_STATUS_CODE)
    }
}