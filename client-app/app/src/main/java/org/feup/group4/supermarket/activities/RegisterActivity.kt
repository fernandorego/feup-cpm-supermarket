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

class RegisterActivity : AppCompatActivity() {
    private val nameTextView: TextView by lazy { findViewById(R.id.register_input_name) }
    private val nicknameTextView: TextView by lazy { findViewById(R.id.register_input_nickname) }
    private val passwordTextView: TextView by lazy { findViewById(R.id.register_input_password) }
    private val cardTextView: TextView by lazy { findViewById(R.id.register_input_card) }
    private val cardCvvTextView: TextView by lazy { findViewById(R.id.register_input_card_ccv) }
    private val cardMonthTextView: TextView by lazy { findViewById(R.id.register_input_card_month) }
    private val cardYearTextView: TextView by lazy { findViewById(R.id.register_input_card_year) }

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
                    nicknameTextView.text.toString(),
                    passwordTextView.text.toString(),
                    cardTextView.text.toString(),
                    cardCvvTextView.text.toString(),
                    cardMonthTextView.text.toString() + "/" + cardYearTextView.text.toString()
                )
            }
        }

        cardYearTextView.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerBtn.callOnClick()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
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