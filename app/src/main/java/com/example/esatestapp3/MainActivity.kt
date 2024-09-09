package com.example.esatestapp3

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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

        loginButton.setOnClickListener{ // when the login button is clicked
            print("login button pressed")
            val email = emailEditText.text.toString() // getting the text from the email text field
            val password = passwordEditText.text.toString();
            if(!email.contains("@")){
                Toast.makeText(this,"invalid email address", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }

            val emailVerify = email.split("@").toTypedArray()

            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"please fill all needed fields", Toast.LENGTH_SHORT).show()
            }else{
                if(emailVerify[1] != "esa-solar.com"){
                    Toast.makeText(this,"this is not a company email", Toast.LENGTH_SHORT).show()
                }else{
//                    Toast.makeText(this,"logged in", Toast.LENGTH_SHORT).show()
                    verifyLogin(email,password) // calling email verification function
                }
            }
        }
    }

}

private fun verifyLogin(email: String, password: String) {
    println("test output")
}