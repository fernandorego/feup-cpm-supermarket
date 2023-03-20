package org.feup.group4.supermarket.services.http

import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpServices(private val baseAddress: String, private val port: Int) {
    fun get(act: HttpInterface, urlRoute: String, token: String) {
        act.onPreExecute()
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("http://$baseAddress:$port$urlRoute")
            urlConnection = url.openConnection() as HttpURLConnection
            val bearerAuth = "Bearer $token"
            with(urlConnection) {
                this.doInput = true
                this.setRequestProperty("Authorization", bearerAuth)
                this.useCaches = false
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

    fun post(act: HttpInterface, urlRoute:String, json: String) {
        act.onPreExecute()
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL("http://$baseAddress:$port$urlRoute")
            urlConnection = url.openConnection() as HttpURLConnection
            with(urlConnection) {
                this.doOutput = true
                this.doInput = true
                this.requestMethod = "POST"
                this.setRequestProperty("Content-Type", "application/json")
                this.useCaches = false
            }

            val outputStream = DataOutputStream(urlConnection.outputStream)
            outputStream.writeBytes(json)
            outputStream.flush()
            outputStream.close()

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