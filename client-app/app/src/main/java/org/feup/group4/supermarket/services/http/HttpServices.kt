package org.feup.group4.supermarket.services.http

import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

enum class HTTPRequest {
    GET, POST, PUT, DELETE
}

class HttpServices(private val act: HttpInterface, private val baseAddress: String, private val port: String) {
    fun get(urlRoute: String, token: String) {
        val bearerToken = "Bearer $token"
        request(HTTPRequest.GET, urlRoute, bearerToken)
    }

    fun post(urlRoute:String, json: String, token: String? = null) {
        var bearerToken:String? = null
        if (token.isNullOrEmpty()) {
            bearerToken = "Bearer $token"
        }
        request(HTTPRequest.POST, urlRoute, bearerToken, body = json)
    }

    fun put(urlRoute:String, json: String, token: String? = null) {
        var bearerToken:String? = null
        if (token.isNullOrEmpty()) {
            bearerToken = "Bearer $token"
        }
        request(HTTPRequest.PUT, urlRoute, bearerToken, body = json)
    }

    fun delete(urlRoute: String, token: String, objId: Int) {
        val bearerToken = "Bearer $token"
        request(HTTPRequest.DELETE, "$urlRoute/$objId", bearerToken)
    }

    private fun request(requestMethod: HTTPRequest, urlRoute: String, bearerToken:String?, body:String?=null) {
        act.onPreExecute()
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("http://$baseAddress:$port$urlRoute")
            urlConnection = url.openConnection() as HttpURLConnection
            with(urlConnection) {
                if (!bearerToken.isNullOrEmpty()) {
                    this.setRequestProperty("Authorization", bearerToken)
                }

                this.requestMethod = requestMethod.toString()
                this.useCaches = false

                when(requestMethod) {
                    HTTPRequest.GET -> { this.doInput = true }
                    HTTPRequest.DELETE -> { this.setRequestProperty("Content-Type", "application/json") }
                    else -> {
                        this.doOutput = true
                        this.doInput = true
                        this.setRequestProperty("Content-Type", "application/json")
                    }
                }
            }

            if (requestMethod == HTTPRequest.POST || requestMethod == HTTPRequest.PUT) {
                val outputStream = DataOutputStream(urlConnection.outputStream)
                outputStream.writeBytes(body)
                outputStream.flush()
                outputStream.close()
            }

            val code = urlConnection.responseCode
            if (code == 200) {
                act.onPostExecute(urlConnection.inputStream.reader().readText())
            } else {
                act.onErrorExecute(code, urlConnection.errorStream.reader().readText())
            }
        } catch (e: java.lang.Exception) {
            println(e.toString())
            act.onErrorExecute(500, e.toString())
        } finally {
            urlConnection?.disconnect()
        }
    }
}