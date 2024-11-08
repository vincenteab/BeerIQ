package com.example.beeriq.ui.BeerList

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class BeerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beer_list)

        // Get the category name passed from the previous Activity
        val categoryName = intent.getStringExtra("CATEGORY_NAME")

        // Set up RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewBeerList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Use the category name to filter the beers, e.g., you can have a list of beers based on category
        val beerList = getBeersByCategory(categoryName)

        beerListAdapter = BeerListAdapter(beerList)
        recyclerView.adapter = beerListAdapter

        val backButton: Button = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            // Navigate back to BeerCategoriesFragment
            onBackPressed()
        }
    }

    // Fetch beers by category (dummy data for now)
    private fun getBeersByCategory(category: String?): List<Beer> {
        // Here, you would fetch beers based on category
        return listOf(
            Beer("Beer 1", category),
            Beer("Beer 2", category)
        )
    }
}

