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

class landingPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_landing_page)

        val userEmail = intent.getStringExtra("userEmail") // getting the email from the server response to the login page

        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        val profileButton = findViewById<Button>(R.id.profile) // will go to a profile page
//        val logoutButton = findViewById<Button>(R.id.logoutButton) // logout button value is created
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

//        logoutButton.setOnClickListener{ // logs out user
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
    }
}