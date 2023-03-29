package org.feup.group4.supermarket.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Token
import org.feup.group4.supermarket.service.AuthService
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity() {
    private val emailTextView: TextView by lazy { findViewById(R.id.login_input_email) }
    private val passwordTextView: TextView by lazy { findViewById(R.id.login_input_password) }

    private val loginBtn: Button by lazy { findViewById(R.id.btn_login) }
    private val registerBtn: TextView by lazy { findViewById(R.id.btn_register) }
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
                AuthService(this, ::afterLoginHttpRequest).login(emailTextView.text.toString(), passwordTextView.text.toString())
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        passwordTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loginBtn.callOnClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
    }

    private fun afterLoginHttpRequest(statusCode: Int, json: String?) {
        runOnUiThread {
            spinner.visibility = View.INVISIBLE
            if (statusCode != 200) {
                Toast.makeText(applicationContext, "code: $statusCode\nmessage: $json", Toast.LENGTH_SHORT).show()
            }
        }
        if (statusCode != 200) {
            return
        }

        AuthService(this, null).setToken(Gson().fromJson(json, Token::class.java).access_token)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}