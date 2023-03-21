package org.feup.group4.supermarket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import org.feup.group4.supermarket.*
import org.feup.group4.supermarket.model.Token
import org.feup.group4.supermarket.service.AuthService
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val emailTextView: TextView by lazy { findViewById(R.id.login_input_email) }
    private val passwordTextView: TextView by lazy { findViewById(R.id.login_input_password) }

    private val loginBtn: Button by lazy { findViewById(R.id.btn_login) }
    private val registerBtn: Button by lazy { findViewById(R.id.btn_register) }
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
            spinner.visibility = View.VISIBLE
            thread(start = true) {
                AuthService(this, ::afterHttpRequest).login(emailTextView.text.toString(), passwordTextView.text.toString())
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun afterHttpRequest(statusCode: Int, json: String?) {
        runOnUiThread {
            spinner.visibility = View.INVISIBLE
            if (statusCode != 200) {
                Toast.makeText(applicationContext, "code: $statusCode\nmessage: $json", Toast.LENGTH_SHORT).show()
            }
        }
        if (statusCode != 200) {
            return
        }

        val token = Gson().fromJson(json, Token::class.java)
        AuthService(this, null).setToken(token.access_token)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}