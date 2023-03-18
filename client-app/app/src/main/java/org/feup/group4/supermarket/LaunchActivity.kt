package org.feup.group4.supermarket

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.thread


class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyToken", MODE_PRIVATE)
        val token = sharedPreferences.getString("my_token", null)
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else {
            thread(start = true) {
                getUser(this, resources.getString(R.string.server_ip), 8000, token)
            }

            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }

    fun setUser(user: User) {
        runOnUiThread {
            Toast.makeText(applicationContext,"email: ${user.email}\nname: ${user.name}\nuser_img: ${user.user_img}", Toast.LENGTH_SHORT).show()
        }
    }

    fun handleInvalidToken() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}