package org.feup.group4.supermarket

import android.widget.Toast
import com.google.gson.Gson
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class Credentials(
    val email: String,
    val password: String
)

class Token(val access_token: String)

fun loginService(act: LoginActivity, baseAddress: String, port: Int, email: String, password: String) {
    val urlRoute = "/getToken"
    var urlConnection: HttpURLConnection? = null

    act.startSpinner()

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
        outputStream.writeBytes(Gson().toJson(Credentials(email,password)))
        outputStream.flush()
        outputStream.close()

        val code = urlConnection.responseCode
        act.stopSpinner()

        if (code == 200) {
            act.setToken(urlConnection.inputStream.reader().readText())
        } else {
            act.displayToast("Response Code: $code" + "\nError: ${urlConnection.errorStream.reader().readText()}",
                Toast.LENGTH_SHORT)
        }
    } catch (e: java.lang.Exception) {
        println(e.toString())
    } finally {
        urlConnection?.disconnect()
    }
}