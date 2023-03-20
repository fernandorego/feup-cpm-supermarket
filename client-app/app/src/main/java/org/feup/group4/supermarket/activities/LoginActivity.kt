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
import org.feup.group4.supermarket.models.Token
import org.feup.group4.supermarket.services.http.HttpInterface
import kotlin.concurrent.thread

class LoginActivity : AppCompatActivity(), HttpInterface {
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
            thread(start = true) {
                loginService(this, resources.getString(R.string.server_ip), resources.getString(R.string.server_port),
                    emailTextView.text.toString(), passwordTextView.text.toString())
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun onPreExecute() {
        runOnUiThread {
            spinner.visibility = View.VISIBLE
        }
    }

    override fun onPostExecute(json: String) {
        runOnUiThread {
            spinner.visibility = View.INVISIBLE
        }

        val token = Gson().fromJson(json, Token::class.java)
        with(getSharedPreferences("MyToken", MODE_PRIVATE).edit()) {
            putString("my_token", token.access_token)
            apply()
        }
        startActivity(Intent(this, MainActivity::class.java))
    }

    override fun onErrorExecute(code: Int, msg: String) {
        runOnUiThread {
            spinner.visibility = View.INVISIBLE
            Toast.makeText(applicationContext,"code: $code\nmessage: $msg", Toast.LENGTH_SHORT).show()
        }
    }
}