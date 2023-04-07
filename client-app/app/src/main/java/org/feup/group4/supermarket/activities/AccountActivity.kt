package org.feup.group4.supermarket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.service.AuthService

class AccountActivity : AppCompatActivity() {
    private val logoutBtn: Button by lazy { findViewById(R.id.btn_logout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
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