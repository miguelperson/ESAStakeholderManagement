package com.example.esatestapp3

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets

        }

        val emailEditText = findViewById<EditText>(R.id.editTextTextEmailAddress2) // getting the email input
        val passwordEditText = findViewById<EditText>(R.id.editTextTextPassword) // getting the password
        val loginButton = findViewById<Button>(R.id.button2)

        loginButton.setOnClickListener{
            val email = emailEditText.text.toString() // getting the text from the email text field
        }
    }

}