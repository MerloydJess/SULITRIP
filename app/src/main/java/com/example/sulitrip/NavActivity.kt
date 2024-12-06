package com.example.sulitrip

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.navigateUp
import com.google.firebase.auth.FirebaseAuth

class NavActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is logged in
        if (FirebaseAuth.getInstance().currentUser == null) {
            // Redirect to LoginActivity if not authenticated
            startActivity(Intent(this, LoginActivity::class.java))
            finish() // Close this activity
            return
        }

        // Set the layout containing the NavHostFragment
        setContentView(R.layout.activity_nav)

        // Setup NavController and AppBarConfiguration
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)

        // Connect ActionBar with NavController
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        // Handle back navigation with NavController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
