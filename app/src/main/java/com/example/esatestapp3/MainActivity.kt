package com.example.esatestapp3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.esatestapp3.ui.RegisterActivity
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        // Access email, password, and login button
        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2)
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword)
        val loginButton = findViewById<Button>(R.id.button2)
        val registerButton = findViewById<Button>(R.id.button) // registration button

        registerButton.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Send data to backend for verification
                verifyLogin(email, password)
            }
        }
    }

    private fun testFunction(email: String, password: String){
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/test")
            .get()
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkError", "Test connection failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                Log.d("TestConnection", "Response: $responseBody")
            }
        })

    }

    // Function to verify login
    private fun verifyLogin(email: String, password: String) {
        val client = OkHttpClient()

        // Create JSON object with email and password
        val json = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        // Replace with your backend login endpoint
        val request = Request.Builder()
            .url("http://10.0.2.2:3000/login")  // Use this for Android emulator testing
            .post(body)
            .build()

        // Make asynchronous network call
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body // Get the body safely

                if (responseBody != null) {
                    val responseString = responseBody.string() // Convert it to string

                    runOnUiThread {
                        if (response.isSuccessful) {
                            // Handle successful login
                            handleSuccessfulLogin()
                        } else {
                            // Handle failed login
                            Toast.makeText(this@MainActivity, "Login Failed: $responseString", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle the case where the body is null
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, "Login Failed: Empty response", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // Handle successful login and navigate to dashboard
    private fun handleSuccessfulLogin() {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

        // Navigate to DashboardActivity
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish() // Optionally call finish to remove MainActivity from the back stack
    }
}
