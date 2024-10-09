package com.example.esatestapp3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.NestedScrollView
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class addingBatteryPAge : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_adding_battery_page)
        window.navigationBarColor = android.graphics.Color.BLACK

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val homebutton = findViewById<Button>(R.id.button5) // return to main landing page
        val profileButton = findViewById<Button>(R.id.profile3) // go to profile page
        val userEmail =
            intent.getStringExtra("userEmail") // recieve the email from the home landing page
        val addBattery =
            findViewById<Button>(R.id.addBattery3) // going to give new functionality to this button
        val TDESName = findViewById<EditText>(R.id.TDESName)
        val TDESid = findViewById<EditText>(R.id.TDESid)
        Log.d("email of User", "the email is: "+userEmail.toString())

        addBattery.setOnClickListener {
            val batName = TDESName.text.toString()
            val batID = TDESid.text.toString()
            if (batID.isEmpty() || batName.isEmpty()) {
                Toast.makeText(this, "please fill in all information needed", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Log.d("main test", "email is: "+userEmail.toString())
                addBattery(batName, batID, userEmail.toString())
            }
        }
        homebutton.setOnClickListener {
            val intent = Intent(this, landingPage::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
            finish()
        }
        profileButton.setOnClickListener {
            val intent = Intent(this, profilePage::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
            finish()
        }

    }

    private fun addBattery(batName: String, batID: String, userEmail: String) {
        val client = OkHttpClient()
        // Log.d("Second email test", "email is still: "+userEmail)
        val json = """
            {
                "name": "$batName",
                "batteryID": "$batID",
                "user":"$userEmail"
            }
        """.trimIndent()

        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        // Replace with your backend login endpoint
        val request = Request.Builder()
            .url("https://sandbattery.info/registerBattery")  // Use this for Android emulator testing
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@addingBatteryPAge, "Network Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onResponse(
                call: Call,
                response: Response
            ) { // explicitly recieving the server response
                val responseBody = response.body?.string()

                runOnUiThread {
                    if (response.isSuccessful && responseBody != null) {
                        Log.d("BatteryRegistration", responseBody) // Log the full response
                        try {
                            val jsonResponse = JSONObject(responseBody)
//                            val userEmail = jsonResponse.getString("email")
                            Toast.makeText( // comment
                                this@addingBatteryPAge,
                                "Battery Successfully Registered",
                                Toast.LENGTH_SHORT
                            ).show() // temporary place holder

                        } catch (e: JSONException) {
                            Log.e("LoginError", "Error parsing JSON: ${e.message}")
                            Toast.makeText(
                                this@addingBatteryPAge,
                                "Login Failed: Invalid response format",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Log.d("LoginError", "Response not successful or body null: $responseBody")
                        Toast.makeText(
                            this@addingBatteryPAge,
                            "Login Failed: $responseBody",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
        val intent = Intent(this, landingPage::class.java)
        intent.putExtra("userEmail", userEmail)
        startActivity(intent)
        finish()
    }
}