package org.feup.group4.supermarket.service

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import org.feup.group4.supermarket.R
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

typealias AfterRequest = (statusCode: Int, body: String?) -> Unit

enum class HttpRequestMethod {
    GET, POST, PUT, DELETE
}

open class HttpService protected constructor(
    protected val context: Context,
    private val afterRequest: AfterRequest? = null
) {
    companion object {
        const val tokenStore = "keystore"
        const val tokenStoreKey = "access_token"
    }

    fun getToken(): String? {
        val sharedPreferences =
            context.getSharedPreferences(tokenStore, AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.getString(tokenStoreKey, null)
    }

    protected fun get(urlRoute: String) = request(HttpRequestMethod.GET, urlRoute)
    protected fun post(urlRoute: String, json: String) =
        request(HttpRequestMethod.POST, urlRoute, body = json)

    protected fun put(urlRoute: String, json: String) = request(HttpRequestMethod.PUT, urlRoute, body = json)
    protected fun delete(urlRoute: String, objectId: Int) =
        request(HttpRequestMethod.DELETE, "$urlRoute/$objectId")

    private fun request(requestMethod: HttpRequestMethod, urlRoute: String, body: String? = null) {
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
            }

            if (requestMethod == HttpRequestMethod.POST || requestMethod == HttpRequestMethod.PUT) {
                val outputStream = DataOutputStream(urlConnection.outputStream)
                outputStream.writeBytes(body)
                outputStream.flush()
                outputStream.close()
            }

            val code = urlConnection.responseCode
            if (code == 200) {
                afterRequest?.let { it(code, urlConnection.inputStream.reader().readText()) }
            } else {
                afterRequest?.let { it(code, urlConnection.errorStream.reader().readText()) }
            }
        } catch (e: java.lang.Exception) {
            println(e.toString())
            afterRequest?.let { it(500, e.toString()) }
        } finally {
            urlConnection.disconnect()
        }
    }
}