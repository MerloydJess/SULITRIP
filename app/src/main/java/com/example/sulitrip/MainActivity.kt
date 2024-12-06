package com.example.sulitrip

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() } // Lazy initialization

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity
    }

    /**
     * Navigates to MapActivity and finishes the current activity.
     */
    private fun navigateToMap() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish() // Close MainActivity
    }
}
