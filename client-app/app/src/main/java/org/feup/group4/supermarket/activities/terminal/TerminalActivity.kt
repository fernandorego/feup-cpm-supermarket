package org.feup.group4.supermarket.activities.terminal

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
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.LoginActivity
import org.feup.group4.supermarket.databinding.ActivityClientMainBinding
import org.feup.group4.supermarket.databinding.ActivityTerminalMainBinding
import org.feup.group4.supermarket.model.User
import org.feup.group4.supermarket.service.UserService

class TerminalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTerminalMainBinding
    companion object {
        var user: User = User("","", null, null, null, null, null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = Gson().fromJson(intent.getStringExtra("JSON_USER"), User::class.java)
        binding = ActivityTerminalMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        setupActionBarWithNavController(
            navHostFragment.navController, AppBarConfiguration(
                setOf(R.id.navigation_products, R.id.navigation_checkout)
            )
        )
        binding.navView.setupWithNavController(navHostFragment.navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }
}