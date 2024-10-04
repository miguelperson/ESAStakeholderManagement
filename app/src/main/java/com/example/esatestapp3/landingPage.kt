package com.example.esatestapp3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esatestapp3.ui.RegisterActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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

class landingPage : AppCompatActivity() {
    private val client = OkHttpClient()
    private var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing_page)



        val userEmail = intent.getStringExtra("userEmail") // getting the email from the server response to the login page

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val profileButton = findViewById<Button>(R.id.profile) // will go to a profile page
        val addBattery = findViewById<Button>(R.id.addBattery) // page to add batteries
        val toggleCharging = findViewById<Button>(R.id.toggleCharging)
        val toggleHeating = findViewById<Button>(R.id.toggleHeating)
        val batteryTitle = findViewById<TextView>(R.id.batteryName)
        val ambientTempView = findViewById<TextView>(R.id.ambientTemp)
        val internalTempView = findViewById<TextView>(R.id.internalTemp)
        val setRoomTemp = findViewById<TextView>(R.id.setRoomTemp)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startBatteryCheckCoroutine(userEmail.toString(), batteryTitle, internalTempView, ambientTempView, setRoomTemp)

        toggleHeating.setOnClickListener{
            toggleHeat(userEmail.toString())
        }

        toggleCharging.setOnClickListener{
            toggleCharge(userEmail.toString())
        }

        profileButton.setOnClickListener{
            val intent = Intent(this, profilePage::class.java)
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

    private fun toggleCharge(userEmail: String){
        val client = OkHttpClient()
        val json = """
            {
                "user":"$userEmail"
            }
        """.trimIndent()
        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        val request = Request.Builder()
            .url("https://sandbattery.info/appChargingToggle")  // Use this for Android emulator testing
            .post(body)
            .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException){
                runOnUiThread{
                    Toast.makeText(this@landingPage, "Network Error", Toast.LENGTH_SHORT).show()
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
                                this@landingPage,
                                "toggle successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: JSONException){
                            Toast.makeText(
                                this@landingPage,
                                "invalid response format",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else{
                        Toast.makeText(
                            this@landingPage,
                            "no battery was toggled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
    }

    private fun toggleHeat(userEmail: String){
        val client = OkHttpClient()
        val json = """
            {
                "user":"$userEmail"
            }
        """.trimIndent()
        val body = RequestBody.create("application/json; charset=utf-8".toMediaType(), json)

        val request = Request.Builder()
            .url("https://sandbattery.info/appHeatToggle")  // Use this for Android emulator testing
            .post(body)
            .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException){
                runOnUiThread{
                    Toast.makeText(this@landingPage, "Network Error", Toast.LENGTH_SHORT).show()
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
                            val status = jsonResponse.getString("message") // status holds battery heating status
                            Log.d("charging status response", status)

                            Toast.makeText(
                                this@landingPage,
                                "toggle successful",
                                Toast.LENGTH_SHORT
                            ).show()
                        } catch (e: JSONException){
                            Toast.makeText(
                                this@landingPage,
                                "invalid response format",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else{
                        Toast.makeText(
                            this@landingPage,
                            "no battery was toggled",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        })
    }

    private fun startBatteryCheckCoroutine(userEmail: String, batteryTitle: TextView, internalTempView: TextView, ambientTempView: TextView, setRoomTemp: TextView) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                checkBatteryStatus(userEmail, batteryTitle, internalTempView, ambientTempView, setRoomTemp)
                delay(5000) // Delay for 5 seconds
            }
        }
    }

    private fun checkBatteryStatus(userEmail: String, batteryTitle: TextView, internalTempView: TextView, ambientTempView: TextView, setRoomTemp: TextView) {
        val request = Request.Builder().url("https://sandbattery.info/batteryStatus?email=$userEmail").get().build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkError", "Test connection failed: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                response.body?.let { responseBody ->
                    val json = JSONObject(responseBody.string())
                    val batteryName = if (json.has("batteryName")) json.getString("batteryName") else "Unknown"
                    val internalTemp = if(json.has("currentInternalTemp")) json.getString("currentInternalTemp") else "No Temp."
                    val ambientTemp = if(json.has("currentRoomTemp")) json.getString("currentRoomTemp") else "No Temp."
                    val roomTemp = if(json.has("setRoomTemp")) json.getString("setRoomTemp") else "no set temp found"

                    runOnUiThread {
                        batteryTitle.text = batteryName
                        internalTempView.text = "Internal Temp: $internalTemp°C"
                        ambientTempView.text = "Ambient Temp: $ambientTemp°C"
                        setRoomTemp.text = "Room Temp Set To: $roomTemp°C"
                    }
                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel() // Stop the coroutine when the activity is destroyed
    }
}