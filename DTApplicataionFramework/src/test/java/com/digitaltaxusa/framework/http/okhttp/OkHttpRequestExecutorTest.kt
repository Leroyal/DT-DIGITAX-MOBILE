package com.digitaltaxusa.framework.http.okhttp

import com.digitaltaxusa.framework.http.listeners.HttpResponseCallback
import com.digitaltaxusa.framework.http.okhttp.internal.RequestCleanupSpec
import com.digitaltaxusa.framework.http.okhttp.internal.RequestState
import com.digitaltaxusa.framework.http.request.HttpMethod
import com.digitaltaxusa.framework.http.request.HttpRequest
import com.digitaltaxusa.framework.http.request.RequestPayload
import com.digitaltaxusa.framework.http.response.ErrorItem
import com.digitaltaxusa.framework.http.response.ResponseItem
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.AfterClass
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private const val EMPTY_JSON_OBJECT = "{}"

private const val DEFAULT_URL = "http://test.com/"
private const val DEFAULT_URL_WITH_PARAMS = "$DEFAULT_URL?id=1&username=John"
private const val DEFAULT_ERROR_HTTP_STATUS_CODE = 400
private const val DEFAULT_MOCK_RESPONSE_DELAY_IN_SECONDS = 5L

private val DEFAULT_URL_QUERY_PARAM_PAYLOAD =
    RequestPayload.UrlQueryParameters(mapOf("id" to "1", "username" to "John"))

private const val MOCK_RESPONSE_PATH_SUCCESS_EMPTY_JSON_OBJECT = "/success/empty_json"
private const val MOCK_RESPONSE_PATH_SUCCESS_EMPTY_BODY_WITH_DELAY =
    "/success/empty_body?delayed=true"
private const val MOCK_RESPONSE_PATH_FAILURE_HTTP_ERROR = "/failure?error=http"
private const val MOCK_RESPONSE_PATH_FAILURE_GENERIC_ERROR = "/failure?error=generic"

private val MEDIA_TYPE_JSON = RequestPayload.CONTENT_TYPE_APPLICATION_JSON.toMediaTypeOrNull()

private val REQUEST_BODY_EMPTY_JSON_OBJECT = EMPTY_JSON_OBJECT.toRequestBody(MEDIA_TYPE_JSON)
private val REQUEST_PAYLOAD_STRING_JSON = RequestPayload.StringRequestPayload(
    RequestPayload.CONTENT_TYPE_APPLICATION_JSON,
    EMPTY_JSON_OBJECT
)

/**
 * Tests to verify validity of [OkHttpRequestExecutor]. Verification can be broken down into:
 * - Building the HTTP request URL: [OkHttpRequestExecutor.buildHttpUrl]
 * - Building the request payload/body: [OkHttpRequestExecutor.buildRequestBody]
 * - Building the HTTP request: [OkHttpRequestExecutor.buildRequest]
 * - Verifying callback behavior: [HttpResponseCallback]
 */
class OkHttpRequestExecutorTest {

    private val okHttpClient = OkHttpClient.Builder().build()
    private val okHttpRequestExecutor = OkHttpRequestExecutor(okHttpClient, null)

    companion object {

        val mockServer = MockWebServer()

        // custom dispatcher for mock web server
        // the advantage of a dispatcher is that we get to map url paths to specific responses
        private val mockRequestDispatcher = TestDispatcher()

        @JvmStatic
        @BeforeClass
        fun init() {
            mockServer.start()
            mockServer.dispatcher = mockRequestDispatcher
        }

        @JvmStatic
        @AfterClass
        fun tearDown() {
            mockServer.shutdown()
        }
    }

    @Test
    fun `verify that a valid request object is built for a HTTP GET request without additional query parameters and http headers`() {

        // give + when
        val request = okHttpRequestExecutor.buildRequest(
            url = DEFAULT_URL,
            httpMethod = HttpMethod.GET,
            httpHeaders = emptyMap(),
            requestPayload = RequestPayload.EmptyRequestPayload
        )

        // then
        Assert.assertEquals(DEFAULT_URL, request.url.toString())
        Assert.assertEquals(HttpMethod.GET.name, request.method)
        Assert.assertTrue(request.headers.size == 0)
        Assert.assertEquals(EMPTY_REQUEST.contentType(), request.body?.contentType())
    }

    @Test
    fun `verify that a valid request object is built for a HTTP GET request with additional query parameters and http headers`() {

        // given + when
        val request = okHttpRequestExecutor.buildRequest(
            url = DEFAULT_URL,
            httpMethod = HttpMethod.GET,
            httpHeaders = mapOf("Content-Type" to "application/json"),
            requestPayload = DEFAULT_URL_QUERY_PARAM_PAYLOAD
        )

        // then
        Assert.assertEquals(DEFAULT_URL_WITH_PARAMS, request.url.toString())
        Assert.assertTrue(request.headers.size == 1)
        Assert.assertEquals("application/json", request.headers["Content-Type"])
        Assert.assertEquals(HttpMethod.GET.name, request.method)
        Assert.assertEquals(EMPTY_REQUEST.contentType(), request.body?.contentType())
    }

    @Test
    fun `verify that a valid request object is built for a HTTP POST request with an empty json string payload`() {

        // given + when
        val request = okHttpRequestExecutor.buildRequest(
            url = DEFAULT_URL,
            httpMethod = HttpMethod.POST,
            httpHeaders = emptyMap(),
            requestPayload = REQUEST_PAYLOAD_STRING_JSON
        )

        // then
        Assert.assertEquals(DEFAULT_URL, request.url.toString())
        Assert.assertEquals(HttpMethod.POST.name, request.method)
        Assert.assertTrue(request.headers.size == 0)
        Assert.assertEquals(REQUEST_BODY_EMPTY_JSON_OBJECT.contentType(),
            request.body?.contentType())
    }

    @Test
    fun `verify that the http url is null when the url is empty`() {

        // given + when
        val actualHttpUrl = okHttpRequestExecutor.buildHttpUrl("", null)

        // then
        Assert.assertNull(actualHttpUrl)
    }

    @Test
    fun `verify that the http url is null when the url is invalid`() {

        // given + when
        val actualHttpUrl = okHttpRequestExecutor.buildHttpUrl("test", null)

        // then
        Assert.assertNull(actualHttpUrl)
    }

    @Test
    fun `verify that the base http url is returned when a request payload is a json string payload`() {

        // given + when
        val actualHttpUrl =
            okHttpRequestExecutor.buildHttpUrl(DEFAULT_URL, REQUEST_PAYLOAD_STRING_JSON)

        // then
        Assert.assertNotNull(actualHttpUrl)
        Assert.assertEquals(DEFAULT_URL, actualHttpUrl.toString())
    }

    @Test
    fun `verify that the base http url is returned when a request payload is empty`() {

        // given + when
        val actualHttpUrl =
            okHttpRequestExecutor.buildHttpUrl(DEFAULT_URL, RequestPayload.EmptyRequestPayload)

        // then
        Assert.assertNotNull(actualHttpUrl)
        Assert.assertEquals(DEFAULT_URL, actualHttpUrl.toString())
    }

    @Test
    fun `verify that the base http url is returned when a request payload is null`() {

        // given + when
        val actualHttpUrl = okHttpRequestExecutor.buildHttpUrl(DEFAULT_URL, null)

        // then
        Assert.assertNotNull(actualHttpUrl)
        Assert.assertEquals(DEFAULT_URL, actualHttpUrl.toString())
    }

    @Test
    fun `verify that the base http url is returned when a request payload with empty query parameters`() {

        // given + when
        val actualHttpUrl = okHttpRequestExecutor.buildHttpUrl(
            DEFAULT_URL,
            RequestPayload.UrlQueryParameters(emptyMap())
        )

        // then
        Assert.assertNotNull(actualHttpUrl)
        Assert.assertEquals(DEFAULT_URL, actualHttpUrl.toString())
    }

    @Test
    fun `verify that a valid http url is returned when a request payload has query parameters`() {

        // given + when
        val actualHttpUrl =
            okHttpRequestExecutor.buildHttpUrl(DEFAULT_URL, DEFAULT_URL_QUERY_PARAM_PAYLOAD)

        // then
        Assert.assertNotNull(actualHttpUrl)
        Assert.assertEquals(DEFAULT_URL_WITH_PARAMS, actualHttpUrl.toString())
    }

    @Test
    fun `verify that a valid response body is returned when a request payload is valid`() {

        // given + when
        val actualRequestBody = okHttpRequestExecutor.buildRequestBody(REQUEST_PAYLOAD_STRING_JSON)

        // then
        Assert.assertNotNull(actualRequestBody)
        Assert.assertEquals(REQUEST_BODY_EMPTY_JSON_OBJECT.contentType(),
            actualRequestBody?.contentType())
        Assert.assertEquals(REQUEST_BODY_EMPTY_JSON_OBJECT.contentLength(),
            actualRequestBody?.contentLength())
    }

    @Test
    fun `verify that a null body is returned when request payload type is null`() {

        // given + when
        val requestBody = okHttpRequestExecutor.buildRequestBody(null)

        // then
        Assert.assertNull(requestBody)
    }

    @Test
    fun `verify that an empty request body is returned when request payload type is empty`() {

        // given + when
        val requestBody = okHttpRequestExecutor.buildRequestBody(RequestPayload.EmptyRequestPayload)

        // then
        Assert.assertNotNull(requestBody)
        Assert.assertEquals(EMPTY_REQUEST, requestBody)
    }

    @Test
    fun `verify that OkHttpRequestExecutor supports multiple api calls`() {
        runBlocking {
            val deferredCalls = mutableListOf<Deferred<ResponseItem>>()
            coroutineScope {
                repeat(10) { deferredCalls.add(async(Dispatchers.Default) { getDelayedResponseItem() }) }
                deferredCalls.awaitAll().forEach { responseItem ->
                    Assert.assertNotNull(responseItem)
                    MatcherAssert.assertThat(
                        responseItem,
                        CoreMatchers.instanceOf(ResponseItem.StringResponseItem::class.java)
                    )
                }
            }
        }
    }

    private suspend fun getDelayedResponseItem() =
        suspendCancellableCoroutine<ResponseItem> { continuation ->
            val emptyBodyResponseUrl =
                mockServer.url(MOCK_RESPONSE_PATH_SUCCESS_EMPTY_BODY_WITH_DELAY).toString()
            val httRequest = HttpRequest(url = emptyBodyResponseUrl, httpMethod = HttpMethod.GET)
            okHttpRequestExecutor.execute(
                httRequest,
                ContinuationHttpResponseItem(continuation)
            )
        }
}

/**
 * For testing purposes we need to await till our callback(s) receive responses from the [MockWebServer].
 * This implementation uses the [CountDownLatch] to [CountDownLatch.countDown] and resume execution
 * for the test.
 *
 * @property latch [CountDownLatch] that notifies call-site to resume execution
 */
open class CountdownLatchedHttpResponseCallback(private val latch: CountDownLatch) :
    HttpResponseCallback {
    override fun onSuccess(response: ResponseItem) {
        latch.countDown()
    }

    override fun onFailure(error: ErrorItem) {
        latch.countDown()
    }

    override fun onCancelled() {
        latch.countDown()
    }
}

/**
 * For testing purposes we need to await till our callback(s) receive responses from the [MockWebServer]. This implementation uses the
 * [CountDownLatch] to [CountDownLatch.countDown] and resume execution for the test.
 *
 * @property latch [CountDownLatch] that notifies call-site to resume execution
 */
open class CountdownLatchedRequestCleanup(private val latch: CountDownLatch) :
    RequestCleanupSpec() {

    override fun onStateChanged(state: RequestState) {
        super.onStateChanged(state)
        latch.countDown()
    }
}

/**
 * Convert [HttpResponseCallback] to a [CancellableContinuation] compatible call to test multiple API calls.
 */
class ContinuationHttpResponseItem(private val continuation: CancellableContinuation<ResponseItem>) :
    HttpResponseCallback {
    override fun onSuccess(response: ResponseItem) {
        continuation.resume(response)
    }

    override fun onFailure(error: ErrorItem) {
        continuation.resumeWithException(error.exception)
    }

    override fun onCancelled() {
        continuation.cancel()
    }
}

/**
 * Custom handler for mock server requests. The test will map a [RecordedRequest.path] to an expected [MockResponse].
 */
class TestDispatcher : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path) {
            MOCK_RESPONSE_PATH_SUCCESS_EMPTY_BODY_WITH_DELAY -> MockResponse().apply {
                setBody(EMPTY_JSON_OBJECT)
                setBodyDelay(DEFAULT_MOCK_RESPONSE_DELAY_IN_SECONDS, TimeUnit.SECONDS)
            }
            MOCK_RESPONSE_PATH_SUCCESS_EMPTY_JSON_OBJECT -> MockResponse().apply {
                setBody(
                    EMPTY_JSON_OBJECT
                )
            }
            MOCK_RESPONSE_PATH_FAILURE_HTTP_ERROR -> MockResponse().apply {
                setResponseCode(
                    DEFAULT_ERROR_HTTP_STATUS_CODE
                )
            }
            MOCK_RESPONSE_PATH_FAILURE_GENERIC_ERROR -> MockResponse().apply {
                val invalidStatusLine = "$DEFAULT_ERROR_HTTP_STATUS_CODE"
                status = invalidStatusLine
            }
            else -> return MockResponse() // default 200 response
        }
    }
}