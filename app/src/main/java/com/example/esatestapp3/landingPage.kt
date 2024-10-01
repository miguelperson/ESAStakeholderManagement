package com.example.esatestapp3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esatestapp3.ui.RegisterActivity
import okhttp3.OkHttpClient

class landingPage : AppCompatActivity() {
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        toggleHeating.setOnClickListener{
//        }
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
}