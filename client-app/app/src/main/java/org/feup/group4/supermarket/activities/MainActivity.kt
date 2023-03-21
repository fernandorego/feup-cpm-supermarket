package org.feup.group4.supermarket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.UserService
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val token = UserService(this, null).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        thread(start = true) {
            UserService(this, ::afterHttpRequest).getUser()
        }

        setContentView(R.layout.activity_main)
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
            return;
        }

        this.user = Gson().fromJson(json, User::class.java)
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                "email: ${this.user.email}\nname: ${this.user.name}\nuser_img: ${this.user.user_img}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}