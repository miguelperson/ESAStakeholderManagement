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
import org.json.JSONException
import org.json.JSONObject
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
            .url("https://sandbattery.info/test")
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

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json) // format of the JSON

        val request = Request.Builder() // adding the info to the json
            .url("https://sandbattery.info/login")
            .post(body)
            .build()

        // Make asynchronous network call
        client.newCall(request).enqueue(object : Callback { // this part explicitly sends the JSON to the webserver
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@MainActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) { // explicitly recieving the server response
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        Log.d("LoginResponse", responseBody) // Log the full response
                        try {
                            val jsonResponse = JSONObject(responseBody)
                            val userEmail = jsonResponse.getString("email")
                            handleSuccessfulLogin(userEmail)
                        } catch (e: JSONException) {
                            Log.e("LoginError", "Error parsing JSON: ${e.message}")
                            Toast.makeText(this@MainActivity, "Login Failed: Invalid response format", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("LoginError", "Response not successful or body null: $responseBody")
                        Toast.makeText(this@MainActivity, "Login Failed: $responseBody", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        })
    }

    // Handle successful login and navigate to dashboard
    private fun handleSuccessfulLogin(email: String) {
        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

        // Navigate to DashboardActivity
        val intent = Intent(this, landingPage::class.java)
        intent.putExtra("userEmail",email)
        startActivity(intent)
        finish() // Optionally call finish to remove MainActivity from the back stack
    }
}
