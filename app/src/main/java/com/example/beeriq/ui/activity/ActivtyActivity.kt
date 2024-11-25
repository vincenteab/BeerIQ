package com.example.beeriq.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class ActivityActivity : AppCompatActivity() {

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

        val activityList = listOf(
            ActivityItem(
                "Vincente", "Nov 22, 2024",
                R.drawable.baseline_person_24, // Ensure this drawable exists
                R.drawable.ic_dashboard_black_24dp, // Ensure this drawable exists
                "Guinness", "Stout", "I like this one!"
            ),
            ActivityItem(
                "Daniel", "Nov 21, 2024",
                R.drawable.baseline_person_24, // Ensure this drawable exists
                R.drawable.ic_dashboard_black_24dp, // Ensure this drawable exists
                "Modelo", "Lager", "So refreshing!"
            )
        )

        val adapter = ActivityAdapter(activityList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
