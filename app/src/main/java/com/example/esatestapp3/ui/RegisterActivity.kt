package com.example.esatestapp3.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esatestapp3.DashboardActivity
import com.example.esatestapp3.MainActivity
import com.example.esatestapp3.R
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val email = findViewById<EditText>(R.id.registerEmailText) // get email from registration page
        val password = findViewById<EditText>(R.id.editTextTextPassword2) // get password
        val regButton = findViewById<Button>(R.id.button3) // button thing
        val backButton = findViewById<Button>(R.id.button4) // back to log in

        backButton.setOnClickListener{ // going back to log in page
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        regButton.setOnClickListener{
            val email = email.text.toString();
            val password = password.text.toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Send data to backend for verification
                registerUser(email, password)
            }

        } // end of event listener

    }

    private fun registerUser(email: String, password: String){
        val client = OkHttpClient()
        val json = """
            {
                "email": "$email",
                "password": "$password"
            }
        """.trimIndent()

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)
        val request = Request.Builder()
            .url("https://sandbattery.info/register")  // register end point juts needs email and password
            .post(body)
            .build()

        // Make asynchronous network call
        client.newCall(request).enqueue(object : Callback { // sends json information to the API endpoint
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@RegisterActivity, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body // Get the body safely

                if (responseBody != null) {
                    val responseString = responseBody.string() // Convert it to string

                    runOnUiThread {
                        if (response.isSuccessful) {
                            // Handle successful login
                            handleRegistration()
                        } else {
                            // Handle failed login
                            Toast.makeText(this@RegisterActivity, "Login Failed: $responseString", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // Handle the case where the body is null
                    runOnUiThread {
                        Toast.makeText(this@RegisterActivity, "Login Failed: Empty response", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    // Handle successful login and navigate to dashboard
    private fun handleRegistration() {
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show()

        // Navigate to DashboardActivity
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // Optionally call finish to remove MainActivity from the back stack
    }
}