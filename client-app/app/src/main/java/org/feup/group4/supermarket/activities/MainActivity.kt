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
                UserService(this@MainActivity, ::afterHttpRequest).getUser()
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
        UserService(this, null).saveUser(user)
        if (user.is_admin == true) {
            startActivity(
                Intent(this, TerminalActivity::class.java).putExtra("JSON_USER", json)
            )
        } else {
            startActivity(
                Intent(this, ClientActivity::class.java).putExtra("JSON_USER", json)
                    .setAction(intent.action)
            )
        }
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                "nickname: ${user.nickname}\nname: ${user.name}\nuser_img: ${user.user_img}\n" +
                        "is_admin: ${user.is_admin}\naccumulated_value: ${user.accumulated_value}\n",
                Toast.LENGTH_SHORT
            ).show()
        }
        return
    }
}