package org.feup.group4.supermarket.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.UserService


@SuppressLint("CustomSplashScreen")
class LaunchActivity : Activity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = UserService(this, null).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            UserService(this, ::afterHttpRequest).getUser()
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

        this.user = Gson().fromJson(json, User::class.java)
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                "email: ${this.user.email}\nname: ${this.user.name}\nuser_img: ${this.user.user_img}\n" +
                        "is_admin: ${this.user.is_admin}",
                Toast.LENGTH_SHORT
            ).show()
        }

        val mainIntent = Intent(this, MainActivity::class.java)
        if ("org.feup.group4.supermarket.receipts" == intent.action) {
            mainIntent.action = "org.feup.group4.supermarket.receipts"
        }
        startActivity(mainIntent)
        finish()
    }
}