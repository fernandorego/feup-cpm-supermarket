package org.feup.group4.supermarket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.AuthService

class AccountActivity : AppCompatActivity() {
    private val logoutBtn: Button by lazy { findViewById(R.id.btn_logout) }
    private val user: User = MainActivity.user

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val accountNameTv: TextView = findViewById(R.id.account_name)
        accountNameTv.text = getString(R.string.string_format, this.user.name)

        val accountNickTv: TextView = findViewById(R.id.account_nick)
        accountNickTv.text = getString(R.string.string_format, this.user.nickname)
    }

    override fun onStart() {
        super.onStart()
        logoutBtn.setOnClickListener {
            AuthService(this, null).setToken(null)
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}