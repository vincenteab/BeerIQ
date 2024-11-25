package com.example.beeriq.ui.activity

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beeriq.R
import com.example.beeriq.ui.FriendsList.FirebaseRepo
import com.example.beeriq.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class AddActivityActivity : AppCompatActivity() {

    private lateinit var etBeer: EditText
    private lateinit var etType: EditText
    private lateinit var etComment: EditText
    private lateinit var btnAddActivity: Button
    private val firebaseRepo: FirebaseRepo by lazy {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        FirebaseRepo(sharedPreferences)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_activity)

        etBeer = findViewById(R.id.etBeer)
        etType = findViewById(R.id.etType)
        etComment = findViewById(R.id.etComment)
        btnAddActivity = findViewById(R.id.btnAddActivity)

        // Handle the button click to add an activity
        btnAddActivity.setOnClickListener {
            val beer = etBeer.text.toString().trim()
            val type = etType.text.toString().trim()
            val comment = etComment.text.toString().trim()

            if (beer.isNotEmpty() && type.isNotEmpty() && comment.isNotEmpty()) {
                val activity = Activity(
                    date = getCurrentDate(),
                    Beer = beer,
                    type = type,
                    comment = comment
                )

                // Call the addActivity method from FirebaseRepo
                firebaseRepo.addActivity(activity) { success ->
                    if (success) {
                        // Show success message
                        Toast.makeText(this, "Activity added successfully!", Toast.LENGTH_SHORT).show()
                    } else {
                        // Show failure message
                        Toast.makeText(this, "Failed to add activity.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                // Show a toast or error message for empty fields
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    private fun getCurrentDate(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date())
    }

}
