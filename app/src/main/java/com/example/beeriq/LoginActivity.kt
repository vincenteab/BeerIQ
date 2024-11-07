package com.example.beeriq

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.example.beeriq.databinding.ActivityLoginBinding
import com.example.beeriq.databinding.ActivityMainBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {


    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)

        firebaseRef = FirebaseDatabase.getInstance().getReference("test")

        binding.createAccountButton.setOnClickListener{
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            firebaseRef.child("username").setValue(username)
            firebaseRef.child("password").setValue(password)
                .addOnCompleteListener{
                    Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
                }
        }
    }
}