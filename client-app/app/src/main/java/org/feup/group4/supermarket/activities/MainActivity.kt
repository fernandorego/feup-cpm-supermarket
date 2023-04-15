package org.feup.group4.supermarket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.feup.group4.supermarket.activities.client.ClientActivity
import org.feup.group4.supermarket.activities.terminal.TerminalActivity
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.UserService

class MainActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (UserService(this, null).getToken().isNullOrEmpty()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            runBlocking(Dispatchers.Default) {
                UserService(
                    this@MainActivity,
                    ::afterHttpRequest,
                    ::afterConnectException
                ).getUserFromServer()
            }
            finish()
        }
    }

    private fun afterHttpRequest(statusCode: Int, json: String?) {
        if (statusCode != 200) {
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "code: $statusCode\nmessage: $json",
                    Toast.LENGTH_SHORT
                ).show()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        user = Gson().fromJson(json, User::class.java)
        UserService(this, null, null).saveUser(user)
        dispatchActivity()
    }

    private fun afterConnectException() {
        user = UserService(this, null, null).getUserFromStorage()
        dispatchActivity()
    }

    private fun dispatchActivity() {
        if (user.is_admin == true) {
            startActivity(
                Intent(this, TerminalActivity::class.java).putExtra(
                    "JSON_USER",
                    Gson().toJson(user)
                )
            )
        } else {
            startActivity(
                Intent(this, ClientActivity::class.java).putExtra("JSON_USER", Gson().toJson(user))
                    .setAction(intent.action)
            )
        }
    }
}