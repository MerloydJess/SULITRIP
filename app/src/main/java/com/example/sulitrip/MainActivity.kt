package com.example.sulitrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called")

        // Initialize Firebase (optional, but good to include)
        FirebaseApp.initializeApp(this)

        checkUserAuthentication()
    }

    /**
     * Checks if the user is logged in and navigates accordingly.
     */
    private fun checkUserAuthentication() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.d("MainActivity", "No user logged in. Redirecting to LoginActivity.")
            navigateToLogin()
        } else {
            Log.d("MainActivity", "User logged in: ${currentUser.email}. Redirecting to MapActivity.")
            navigateToMap()
        }
    }

    /**
     * Navigates to LoginActivity and finishes the current activity.
     */
    private fun navigateToLogin() {
        Log.d("MainActivity", "Navigating to LoginActivity.")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity
    }

    /**
     * Navigates to MapActivity and finishes the current activity.
     */
    private fun navigateToMap() {
        Log.d("MainActivity", "Navigating to MapActivity.")
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity", "onResume called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity", "onDestroy called")
    }
}
