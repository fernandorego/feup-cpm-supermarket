package org.feup.group4.supermarket.activities.terminal

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.gson.Gson
import org.feup.group4.supermarket.R
import org.feup.group4.supermarket.activities.AccountActivity
import org.feup.group4.supermarket.databinding.ActivityTerminalMainBinding
import org.feup.group4.supermarket.model.User

class TerminalActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTerminalMainBinding
    companion object {
        var user: User = User("","", null, null, null, null, null, null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayShowTitleEnabled(true)

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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.account_details -> {
            val intent = Intent(this, AccountActivity::class.java)
            intent.putExtra("FROM", "TERMINAL")
            startActivity(intent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}