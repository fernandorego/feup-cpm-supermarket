package org.feup.group4.supermarket.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.repository.ReceiptsRepository
import org.feup.group4.supermarket.repository.ProductsRepository
import org.feup.group4.supermarket.service.AuthService

class AccountActivity : AppCompatActivity() {
    private val logoutBtn: Button by lazy { findViewById(R.id.btn_logout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        val sharedPreferences = this.getSharedPreferences("user", MODE_PRIVATE)
        val userName = sharedPreferences.getString("name", "")
        val userNick = sharedPreferences.getString("nickname", "")

        val accountNameTv: TextView = findViewById(R.id.account_name)
        val accountNickTv: TextView = findViewById(R.id.account_nick)

        accountNameTv.text = getString(R.string.string_format, userName)
        accountNickTv.text = getString(R.string.string_format, userNick)
    }

    override fun onStart() {
        super.onStart()
        logoutBtn.setOnClickListener {
            AuthService(this, null).setToken("")
            ProductsRepository.getInstance(this).deleteDatabase()
            ReceiptsRepository.getInstance(this).deleteDatabase()
            startActivity(Intent(this, MainActivity::class.java))
            finishAffinity()
        }
    }
}