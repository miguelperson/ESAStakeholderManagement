package com.example.esatestapp3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class profilePage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_page)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.BLACK

        val userEmail = intent.getStringExtra("userEmail") // recieve the email from the home landing page
        val userEmailTextView = findViewById<TextView>(R.id.userEmailTextView)
        userEmailTextView.text = userEmail ?: "No Email Provided"

        val logOut = findViewById<Button>(R.id.logOut)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val addBattery = findViewById<Button>(R.id.addBattery2)
        val chargeHourStart = findViewById<NumberPicker>(R.id.chargeHourStart)
        val chargeMinuteStart = findViewById<NumberPicker>(R.id.chargeMinuteStart)
        val chargeHourEnd = findViewById<NumberPicker>(R.id.chargeHourEnd)
        val chargeMinuteEnd = findViewById<NumberPicker>(R.id.chargeEndMinute)
        val heatingStartHour = findViewById<NumberPicker>(R.id.heatingStartHour)
        val heatingStartMinute = findViewById<NumberPicker>(R.id.heatingStartMinute)
        val heatingEndHour = findViewById<NumberPicker>(R.id.heatingEndHour)
        val heatingEndMinute = findViewById<NumberPicker>(R.id.heatingEndMinute)
        val saveSchedule = findViewById<Button>(R.id.saveSchedule)

        chargeHourStart.minValue = 0 // charge start
        chargeHourStart.maxValue = 24
        chargeMinuteStart.minValue = 0
        chargeMinuteStart.maxValue = 59

        chargeHourEnd.minValue = 0 // charge end
        chargeHourEnd.maxValue = 24
        chargeMinuteStart.minValue = 0
        chargeMinuteEnd.maxValue = 59

        heatingStartHour.minValue = 0 // heat start
        heatingStartHour.maxValue = 24
        heatingStartMinute.minValue = 0
        heatingStartMinute.maxValue = 59

        heatingEndHour.minValue = 0 // heat end
        heatingEndHour.maxValue = 24
        heatingEndMinute.minValue = 0
        heatingEndMinute.maxValue = 59


        saveSchedule.setOnClickListener {
            val chargeStartHourValue = chargeHourStart.value
            val chargeStartMinuteValue = chargeMinuteStart.value
            val chargeEndHourValue = chargeHourEnd.value
            val chargeEndMinuteValue = chargeMinuteEnd.value

            val heatingStartHourValue = heatingStartHour.value
            val heatingStartMinuteValue = heatingStartMinute.value
            val heatingEndHourValue = heatingEndHour.value
            val heatingEndMinuteValue = heatingEndMinute.value

            // Log.d("userInputs", "charge val"+chargeStartHourValue)

            saveScheduleFunction(
                userEmail.toString(),
                chargeStartHourValue,
                chargeStartMinuteValue,
                chargeEndHourValue,
                chargeEndMinuteValue,
                heatingStartHourValue,
                heatingStartMinuteValue,
                heatingEndHourValue,
                heatingEndMinuteValue
            )
        }

        logOut.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java) // not passing any email to the main page
            startActivity(intent)
            finish()
        }

        homeButton.setOnClickListener{
            val intent = Intent(this, landingPage::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
            finish()
        }

        addBattery.setOnClickListener{
            val intent = Intent(this, addingBatteryPAge::class.java)
            intent.putExtra("userEmail", userEmail)
            startActivity(intent)
            finish()
        }

    }

    private fun saveScheduleFunction(
        userEmail: String,
        chargeStartHour: Int,
        chargeStartMinute: Int,
        chargeEndHour: Int,
        chargeEndMinute: Int,
        heatingStartHour: Int,
        heatingStartMinute: Int,
        heatingEndHour: Int,
        heatingEndMinute: Int
    ){

        val client = OkHttpClient()
        val json = """
            {
                "user":"$userEmail",
                "chargeStartHour": $chargeStartHour,
                "chargeStartMinute": $chargeStartMinute,
                "chargeEndHour": $chargeEndHour,
                "chargeEndMinute": $chargeEndMinute,
                "heatingStartHour": $heatingStartHour,
                "heatingStartMinute": $heatingStartMinute,
                "heatingEndHour": $heatingEndHour,
                "heatingEndMinute": $heatingEndMinute
            }
        """.trimIndent()
        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        val request = Request.Builder()
            .url("https://sandbattery.info/appScheduleCreator")  // Use this for Android emulator testing
            .post(body)
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException){
                runOnUiThread{
                    Toast.makeText(this@profilePage, "Network Error", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(
                call: Call,
                response: Response
            ){
                val responseBody = response.body?.string()

                runOnUiThread{
                    if(response.isSuccessful && responseBody != null){
                        try{
                            val jsonResponse = JSONObject(responseBody)
                            val status = jsonResponse.getString("message") // if status == true then battery set to charge, if status == false then battery not charging

                            Log.d("charging status response", status)
                            Toast.makeText(
                                this@profilePage,
                                "Schedule Saved",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: JSONException){
                            Toast.makeText(
                                this@profilePage,
                                "invalid response format",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else{
                        Toast.makeText(
                            this@profilePage,
                            "no battery was toggled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
    }


}