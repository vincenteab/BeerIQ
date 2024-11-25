package com.example.beeriq.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.Activity
import com.example.beeriq.R
import com.example.beeriq.ui.FriendsList.FirebaseRepo
import androidx.core.content.ContentProviderCompat.requireContext


class ActivityActivity : AppCompatActivity() {

    private lateinit var activityAdapter: ActivityAdapter
    private val firebaseRepo: FirebaseRepo by lazy {
        val sharedPreferences = getSharedPreferences("UserData", MODE_PRIVATE)
        FirebaseRepo(sharedPreferences)
    }
    private val activityList = mutableListOf<ActivityItem>() // Mutable list to hold activities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activty)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Friend Activity"
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        activityAdapter = ActivityAdapter(activityList) // Initialize adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = activityAdapter

        fetchActivitiesFromFirebase() // Fetch data from Firebase
    }

    private fun fetchActivitiesFromFirebase() {
        firebaseRepo.fetchActivities { activities ->
            if (activities != null) {
                activityList.clear() // Clear existing data
                activityList.addAll(activities) // Add new data
                activityAdapter.notifyDataSetChanged() // Notify adapter to update
            } else {
                println("Error: Failed to fetch activities.")
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

