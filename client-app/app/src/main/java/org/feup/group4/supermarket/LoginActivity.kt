package org.feup.group4.supermarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler.Callback
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import java.net.HttpURLConnection
import java.util.concurrent.Executors
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val emailTextView: TextView by lazy { findViewById(R.id.input_email) }
    private val passwordTextView: TextView by lazy { findViewById(R.id.input_password) }

    private val loginBtn: Button by lazy { findViewById(R.id.btn_login) }
    private val spinner: ProgressBar by lazy { findViewById(R.id.login_spinner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        spinner.visibility = View.INVISIBLE
        spinner.isIndeterminate = true
    }

    override fun onStart() {
        super.onStart()
        loginBtn.setOnClickListener {
            thread(start = true) {
                loginService(this, "192.168.0.4", 8000, emailTextView.text.toString(), passwordTextView.text.toString())
            }
        }
    }

    fun setToken(json: String) {
        val token = Gson().fromJson(json, Token::class.java)
        with(getSharedPreferences("MyToken", MODE_PRIVATE).edit()) {
            putString("my_token", token.access_token)
            apply()
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

    fun displayToast(msg: String, toastLenght: Int) {
        runOnUiThread {
            Toast.makeText(applicationContext,msg, toastLenght).show()
        }
    }

    fun startSpinner() {
        runOnUiThread {
            spinner.visibility = View.VISIBLE
        }
    }

    fun stopSpinner() {
        runOnUiThread {
            spinner.visibility = View.INVISIBLE
        }
    }
}