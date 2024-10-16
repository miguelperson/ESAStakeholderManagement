package com.example.esatestapp3

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColor
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

class landingPage : AppCompatActivity() { // first full feature version 10/9/2024 Miguel B.
    private val client = OkHttpClient()
    private var job: Job? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing_page)


        val userEmail = intent.getStringExtra("userEmail") // getting the email from the server response to the login page

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.BLACK

        val profileButton = findViewById<Button>(R.id.profile) // will go to a profile page
        val addBattery = findViewById<Button>(R.id.addBattery) // page to add batteries
        val toggleCharging = findViewById<Button>(R.id.toggleCharging)
        val toggleHeating = findViewById<Button>(R.id.toggleHeating)
        val batteryTitle = findViewById<TextView>(R.id.batteryName)
        val ambientTempView = findViewById<TextView>(R.id.ambientTemp)
        val internalTempView = findViewById<TextView>(R.id.internalTemp)
        val setRoomTemp = findViewById<TextView>(R.id.setRoomTemp)
        val chargingSchedule = findViewById<TextView>(R.id.chargingSchedule)
        val heatingSchedule = findViewById<TextView>(R.id.heatingSchedule)



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        startBatteryCheckCoroutine(userEmail.toString(), batteryTitle, internalTempView, ambientTempView, setRoomTemp, chargingSchedule, heatingSchedule)

        toggleHeating.setOnClickListener{
            toggleHeat(userEmail.toString(), toggleHeating)
        }

        toggleCharging.setOnClickListener{
            toggleCharge(userEmail.toString(), toggleCharging)
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

    private fun toggleCharge(userEmail: String, toggleCharging: TextView){
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
                            if (status.toBoolean()) {
                                Toast.makeText(
                                    this@landingPage,
                                    "Charging Enabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                                toggleCharging.setBackgroundColor(Color.parseColor("#90A959"))
                            } else {
                                Toast.makeText(
                                    this@landingPage,
                                    "Charging Disabled",
                                    Toast.LENGTH_SHORT
                                ).show()
                                toggleCharging.setBackgroundColor(Color.parseColor("#E9B872"))
                            }

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

    private fun toggleHeat(userEmail: String, toggleHeating: TextView){
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
                            Log.d("heating status response", status)

                            if(status.toBoolean()){
                                Toast.makeText(
                                    this@landingPage,
                                    "Heating On",
                                    Toast.LENGTH_SHORT
                                ).show()
                                toggleHeating.setBackgroundColor(Color.parseColor("#A63D40"))
                            } else{
                                Toast.makeText(
                                    this@landingPage,
                                    "Heating Off",
                                    Toast.LENGTH_SHORT
                                ).show()
                                toggleHeating.setBackgroundColor(Color.parseColor("#E9B872"))
                            }


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

    private fun startBatteryCheckCoroutine(userEmail: String, batteryTitle: TextView, internalTempView: TextView, ambientTempView: TextView, setRoomTemp: TextView, chargingSchedule:TextView, heatingSchedule:TextView) {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                checkBatteryStatus(userEmail, batteryTitle, internalTempView, ambientTempView, setRoomTemp, chargingSchedule, heatingSchedule)
                delay(5000) // Delay for 5 seconds
            }
        }
    }

    private fun checkBatteryStatus(userEmail: String, batteryTitle: TextView, internalTempView: TextView, ambientTempView: TextView, setRoomTemp: TextView, chargingSchedule: TextView, heatingSchedule: TextView) {
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
                    val startChargingHour = if(json.has("startChargingHour")) json.getString("startChargingHour") else "00"
                    val endChargingHour = if(json.has("endChargingHour")) json.getString("endChargingHour") else "00"
                    val startHeatingHour = if(json.has("startHeatingHour")) json.getString("startHeatingHour") else "00"
                    val endHeatingHour = if(json.has("endHeatingHour")) json.getString("endHeatingHour") else "00"
                    val startChargingMinute = if(json.has("startChargingMinute")) json.getString("startChargingMinute") else "0"
                    val stopHeatingMinute = if(json.has("stopHeatingMinute")) json.getString("stopHeatingMinute") else "0"
                    val startHeatingMinute = if(json.has("startHeatingMinute")) json.getString("startHeatingMinute") else "0"
                    val stopChargingminute = if(json.has("stopChargingminute")) json.getString("stopChargingminute") else "0"

                    runOnUiThread {
                        batteryTitle.text = batteryName
                        internalTempView.text = "Internal Temp: $internalTemp°C"
                        ambientTempView.text = "Ambient Temp: $ambientTemp°C"
                        setRoomTemp.text = "Room Temp Set To: $roomTemp°C"
                        chargingSchedule.text = "Charging Schedule Set To: $startChargingHour:${String.format("%02d", startChargingMinute.toInt())} - $endChargingHour:${String.format("%02d", stopChargingminute.toInt())}"
                        heatingSchedule.text = "Heating Schedule Set To: $startHeatingHour:${String.format("%02d", startHeatingMinute.toInt())} - $endHeatingHour:${String.format("%02d", stopHeatingMinute.toInt())}"
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