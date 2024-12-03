package com.example.beeriq.ui.map

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.beeriq.R

class BreweryDetails : AppCompatActivity() {
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.brewery_details)

        // Get the data passed via the Intent
        val breweryName = intent.getStringExtra("brewery_name") ?: "Unknown"
        val breweryAddress = intent.getStringExtra("brewery_address") ?: "Unknown Address"
        val breweryDescriptionTitle = intent.getStringExtra("brewery_description_title") ?: "No Title"
        val breweryDescriptionBody = intent.getStringExtra("brewery_description_body") ?: "No Description"

        // Find the TextViews and set the text
        val nameTextView: TextView = findViewById(R.id.bName)
        val addressTextView: TextView = findViewById(R.id.bAddress)
        val descriptionTitleTextView: TextView = findViewById(R.id.bDescriptionTitle)
        val descriptionBodyTextView: TextView = findViewById(R.id.bDescriptionBody)

        nameTextView.text = breweryName
        addressTextView.text = breweryAddress
        descriptionTitleTextView.text = breweryDescriptionTitle
        descriptionBodyTextView.text = breweryDescriptionBody

        backButton = findViewById(R.id.back)
        backButton.setOnClickListener() {
            finish()
        }
    }
}
