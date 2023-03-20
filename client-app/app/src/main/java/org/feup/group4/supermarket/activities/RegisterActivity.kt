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
import org.feup.group4.supermarket.models.Token
import org.feup.group4.supermarket.registerService
import org.feup.group4.supermarket.services.http.HttpInterface
import kotlin.concurrent.thread

class RegisterActivity : AppCompatActivity(), HttpInterface {
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
                registerService(this, resources.getString(R.string.server_ip), resources.getString(R.string.server_port),
                    nameTextView.text.toString(), emailTextView.text.toString(), passwordTextView.text.toString())
            }
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