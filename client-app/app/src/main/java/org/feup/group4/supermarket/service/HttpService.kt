package org.feup.group4.supermarket.service

import android.content.Context
import android.util.Log
import org.feup.group4.supermarket.R
import java.io.DataOutputStream
import java.net.ConnectException
import java.net.HttpURLConnection
import java.net.URL

typealias AfterRequest = (statusCode: Int, body: String?) -> Unit

enum class HttpRequestMethod {
    GET, POST, PUT, DELETE
}

open class HttpService internal constructor(
    protected val context: Context,
    private val afterRequest: AfterRequest? = null,
    private val afterConnectException: (() -> Unit)? = null
) {
    protected val sharedPreferencesService =
        SharedPreferencesService(context, SharedPreferencesService.Companion.KeyStore.AUTH.value)

    companion object {
        const val tokenStoreKey = "access_token"
    }

    fun getToken(): String? {
        return sharedPreferencesService.sharedPreferences.getString(tokenStoreKey, null)
    }

    internal fun get(urlRoute: String, headers: Map<String, String>? = null) =
        request(HttpRequestMethod.GET, urlRoute, headers = headers)

    internal fun post(urlRoute: String, json: String, headers: Map<String, String>? = null) =
        request(HttpRequestMethod.POST, urlRoute, body = json, headers = headers)

    internal fun put(urlRoute: String, json: String, headers: Map<String, String>? = null) =
        request(HttpRequestMethod.PUT, urlRoute, body = json, headers = headers)

    internal fun delete(urlRoute: String, headers: Map<String, String>? = null) =
        request(HttpRequestMethod.DELETE, urlRoute, headers = headers)

    private fun request(
        requestMethod: HttpRequestMethod,
        urlRoute: String,
        body: String? = null,
        headers: Map<String, String>? = null
    ) {
        val baseAddress = context.resources.getString(R.string.server_ip)
        val port = context.resources.getString(R.string.server_port)
        val url = URL("http://$baseAddress:$port$urlRoute")
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            with(urlConnection) {
                val token = getToken()
                if (!token.isNullOrEmpty()) {
                    this.setRequestProperty("Authorization", "Bearer $token")
                }

                this.requestMethod = requestMethod.toString()
                this.useCaches = false

                when (requestMethod) {
                    HttpRequestMethod.GET -> this.doInput = true
                    HttpRequestMethod.DELETE -> this.setRequestProperty(
                        "Content-Type",
                        "application/json"
                    )
                    else -> {
                        this.doOutput = true
                        this.doInput = true
                        this.setRequestProperty("Content-Type", "application/json")
                    }
                }

                headers?.forEach { (key, value) ->
                    this.setRequestProperty(key, value)
                }
            }

            if (requestMethod == HttpRequestMethod.POST || requestMethod == HttpRequestMethod.PUT) {
                val outputStream = DataOutputStream(urlConnection.outputStream)
                outputStream.writeBytes(body)
                outputStream.flush()
                outputStream.close()
            }

            val code = urlConnection.responseCode
            if (code in 200..299) {
                afterRequest?.let { it(code, urlConnection.inputStream.reader().readText()) }
            } else {
                afterRequest?.let { it(code, urlConnection.errorStream.reader().readText()) }
            }
        } catch (e: ConnectException) {
            Log.w("HttpService", e.toString())
            if (afterConnectException != null) {
                afterConnectException.invoke()
            } else {
                afterRequest?.let { it(500, e.toString()) }
            }
        } catch (e: java.lang.Exception) {
            Log.w("HttpService", e.toString())
            afterRequest?.let { it(500, e.toString()) }
        } finally {
            urlConnection.disconnect()
        }
    }
}