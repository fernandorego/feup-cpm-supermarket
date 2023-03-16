package org.feup.group4.supermarket

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
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
                registerService(this, "192.168.0.4", 8000, nameTextView.text.toString(), emailTextView.text.toString(), passwordTextView.text.toString())
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