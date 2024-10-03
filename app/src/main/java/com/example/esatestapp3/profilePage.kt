package com.example.esatestapp3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val userEmail = intent.getStringExtra("userEmail") // recieve the email from the home landing page
        val userEmailTextView = findViewById<TextView>(R.id.userEmailTextView)
        userEmailTextView.text = userEmail ?: "No Email Provided"

        val logOut = findViewById<Button>(R.id.logOut)
        val homeButton = findViewById<Button>(R.id.homeButton)
        val addBattery = findViewById<Button>(R.id.addBattery2)
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


}