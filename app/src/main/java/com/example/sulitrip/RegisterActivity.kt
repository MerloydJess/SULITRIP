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

class RegisterActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        setContent {
            RegisterScreen(
                onRegisterSuccess = { goToLoginActivity() },
                registerUser = { email, password, onRegisterSuccess ->
                    registerUser(email, password, onRegisterSuccess)
                }
            )
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Close register activity
    }

    private fun registerUser(email: String, password: String, onRegisterSuccess: () -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    showToast("Registration successful!")
                    onRegisterSuccess()  // Call the success function after registration
                } else {
                    val exception = task.exception
                    showToast("Registration failed: ${exception?.message}")
                }
            }
    }
}

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, registerUser: (String, String, () -> Unit) -> Unit) {
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
        Text(
            "Create an Account",
            style = MaterialTheme.typography.headlineLarge // Corrected to use headlineLarge for title
        )
        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.1f)),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    innerTextField()
                    if (emailState.value.isEmpty()) {
                        Text("Email", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) // Corrected to bodyMedium
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Gray.copy(alpha = 0.1f)),
            decorationBox = { innerTextField ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    innerTextField()
                    if (passwordState.value.isEmpty()) {
                        Text("Password", style = MaterialTheme.typography.bodyMedium, color = Color.Gray) // Corrected to bodyMedium
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                isLoading.value = true
                // Register user
                registerUser(emailState.value, passwordState.value, onRegisterSuccess)
                isLoading.value = false
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = !isLoading.value
        ) {
            Text("Register")
        }

        if (isLoading.value) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRegisterScreen() {
    RegisterScreen(onRegisterSuccess = {}, registerUser = { _, _, _ -> })
}
