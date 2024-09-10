package com.example.esatestapp3.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.esatestapp3.MainActivity
import com.example.esatestapp3.R

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

        } // end of event listener
    }
}