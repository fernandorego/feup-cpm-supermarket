package org.feup.group4.supermarket.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.databinding.ActivityMainBinding
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.UserService
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var user: User
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val token = UserService(this, null).getToken()
        if (token == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            thread(start = true) {
                UserService(this, ::afterHttpRequest).getUser()
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_receipts)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (intent.action == "org.feup.group4.supermarket.receipts") {
            navView.selectedItemId = R.id.navigation_receipts
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun afterHttpRequest(statusCode: Int, json: String?) {
        if (statusCode != 200) {
            runOnUiThread {
                Toast.makeText(
                    applicationContext,
                    "code: $statusCode\nmessage: $json",
                    Toast.LENGTH_SHORT
                ).show()
            }
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        this.user = Gson().fromJson(json, User::class.java)
        runOnUiThread {
            Toast.makeText(
                applicationContext,
                "email: ${this.user.email}\nname: ${this.user.name}\nuser_img: ${this.user.user_img}\n" +
                        "is_admin: ${this.user.is_admin}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}