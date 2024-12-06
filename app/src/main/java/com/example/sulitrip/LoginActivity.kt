package com.example.sulitrip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                // Initialize FirebaseAuth
                auth = FirebaseAuth.getInstance()

                // Set Compose content
                setContent {
            LoginScreen(
                onRegisterClicked = { goToRegisterActivity() },
                loginUser = { email, password -> loginUser(email, password) }
            )
        }
    }

    /**
     * Displays a toast message.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    /**
     * Navigates to MapActivity after successful login.
     */
    private fun goToMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity to prevent going back
    }

    /**
     * Navigates to RegisterActivity for account creation.
     */
    private fun goToRegisterActivity() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    /**
     * Logs in the user using Firebase Authentication.
     */
    private fun loginUser(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            showToast("Please fill out all fields.")
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    goToMapActivity()
                } else {
                    // Login failed
                    showToast("Authentication failed: ${task.exception?.message}")
                }
            }
    }
}

@Composable
fun LoginScreen(onRegisterClicked: () -> Unit, loginUser: (String, String) -> Unit) {
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Title Text
        Text(
            "Welcome to SULITRIP",
            style = MaterialTheme.typography.headlineLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Email Text Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            BasicTextField(
                value = emailState.value,
                onValueChange = { emailState.value = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        innerTextField()
                        if (emailState.value.isEmpty()) {
                            Text(
                                "Email",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Password Text Field
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.1f))
        ) {
            BasicTextField(
                value = passwordState.value,
                onValueChange = { passwordState.value = it },
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        innerTextField()
                        if (passwordState.value.isEmpty()) {
                            Text(
                                "Password",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                isLoading.value = true
                loginUser(emailState.value, passwordState.value) // Trigger login logic
                isLoading.value = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = !isLoading.value
        ) {
            Text("Login")
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        TextButton(
            onClick = { onRegisterClicked() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Don't have an account? Register")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(
        onRegisterClicked = {},
        loginUser = { _, _ -> } // Mock login logic for preview
    )
}
