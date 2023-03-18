package org.feup.group4.supermarket

import com.google.gson.Gson
import java.net.HttpURLConnection
import java.net.URL

class User (
    val email: String,
    val name: String,
    val user_img: String
)

fun getUser(act: LaunchActivity, baseAddress: String, port: Int, token: String) {
    val urlRoute = "/user/getUser"
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
            act.setUser(Gson().fromJson(urlConnection.inputStream.reader().readText(), User::class.java))
        } else {
            act.handleInvalidToken()
        }
    } catch (e: java.lang.Exception) {
        println(e.toString())
        act.handleInvalidToken()
    } finally {
        urlConnection?.disconnect()
    }
}