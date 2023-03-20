package org.feup.group4.supermarket.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.getUser
import org.feup.group4.supermarket.models.User
import org.feup.group4.supermarket.services.http.HttpInterface
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), HttpInterface {
    private lateinit var user:User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("MyToken", MODE_PRIVATE)
        val token = sharedPreferences.getString("my_token", null)
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            return
        }

        thread(start = true) {
            getUser(this, resources.getString(R.string.server_ip), 8000, token)
        }

        setContentView(R.layout.activity_main)
    }

    override fun onPreExecute() {
        return
    }

    override fun onPostExecute(json:String) {
        this.user = Gson().fromJson(json, User::class.java)
        runOnUiThread {
            Toast.makeText(applicationContext,"email: ${this.user.email}\nname: ${this.user.name}\nuser_img: ${this.user.user_img}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onErrorExecute(code:Int, msg:String) {
        startActivity(Intent(this, LoginActivity::class.java))
        runOnUiThread {
            Toast.makeText(applicationContext,"code: $code\nmessage: $msg", Toast.LENGTH_SHORT).show()
        }
    }
}