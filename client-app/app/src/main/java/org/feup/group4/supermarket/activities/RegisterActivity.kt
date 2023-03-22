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
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.Token
import org.feup.group4.supermarket.service.AuthService
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity() {
    private val nameTextView: TextView by lazy { findViewById(R.id.register_input_name) }
    private val emailTextView: TextView by lazy { findViewById(R.id.register_input_email) }
    private val passwordTextView: TextView by lazy { findViewById(R.id.register_input_password) }

    private val registerBtn: Button by lazy { findViewById(R.id.btn_register) }
    private val spinner: ProgressBar by lazy { findViewById(R.id.register_spinner) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        spinner.visibility = View.INVISIBLE
        spinner.isIndeterminate = true
    }

    override fun onStart() {
        super.onStart()
        registerBtn.setOnClickListener {
            thread(start = true) {
                runOnUiThread {
                    spinner.visibility = View.VISIBLE
                }
                AuthService(this, ::afterRegisterHttpRequest).register(
                    nameTextView.text.toString(),
                    emailTextView.text.toString(),
                    passwordTextView.text.toString()
                )
            }
        }
    }

    private fun afterRegisterHttpRequest(statusCode: Int, json: String?) {
        runOnUiThread {
            spinner.visibility = View.INVISIBLE
            if (statusCode != 200) {
                Toast.makeText(
                    applicationContext,
                    "code: $statusCode\nmessage: $json",
                    Toast.LENGTH_SHORT
                ).show()
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