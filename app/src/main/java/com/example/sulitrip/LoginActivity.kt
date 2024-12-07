package com.example.sulitrip

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
        val trimmedEmail = email.trim()

        // Custom email validation
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        if (!trimmedEmail.matches(Regex(emailRegex))) {
            showToast("Invalid email format.")
            return
        }

        if (password.isBlank()) {
            showToast("Password cannot be empty.")
            return
        }

        auth.signInWithEmailAndPassword(trimmedEmail, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Login successful
                    showToast("Login successful!")
                    goToMapActivity()
                } else {
                    // Login failed
                    val errorMessage = task.exception?.message ?: "Login failed."
                    showToast(errorMessage)
                }
            }
    }

}

annotation class RegisterActivity

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

        // Email Outlined Text Field
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Password Outlined Text Field
        OutlinedTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Login Button
        Button(
            onClick = {
                isLoading.value = true
                loginUser(emailState.value, passwordState.value)
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
