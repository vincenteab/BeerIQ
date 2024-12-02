package com.example.beeriq.ui.beerList

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.BeerDatabase
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import com.example.beeriq.data.local.beerDatabase.BeerViewModel
import com.example.beeriq.data.local.beerDatabase.BeerViewModelFactory

class BeerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private lateinit var beerViewModel: BeerViewModel
    private lateinit var searchView: SearchView
    private lateinit var typeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beer_list)

        // Get the category name passed from the previous Activity
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: "Unknown"
        Log.d("Inputted Category", "Category: $categoryName")

        // Set up the beer type text
        typeText = findViewById(R.id.beerType)
        typeText.text = categoryName

        // Set up RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewBeerList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize ViewModel and Repository
        val database = BeerDatabase.getInstance(applicationContext)
        val repository = BeerRepository(database.beerDatabaseDao)
        val viewModelFactory = BeerViewModelFactory(repository)
        beerViewModel = ViewModelProvider(this, viewModelFactory).get(BeerViewModel::class.java)

        // Initialize Adapter with an empty list initially
        beerListAdapter = BeerListAdapter(emptyList())
        recyclerView.adapter = beerListAdapter

        // Observe LiveData from ViewModel and update RecyclerView
        beerViewModel.getBeersByStyle(categoryName).observe(this, { beers ->
            beerListAdapter.updateData(beers)
        })

        // Handle back button click
        val backButton: ImageButton = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // Set up SearchView
        searchView = findViewById(R.id.searchBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBeers(categoryName, newText)
                return true
            }
        })
    }

    private fun filterBeers(categoryName: String, query: String?) {
        // Fetch beers by category
        beerViewModel.getBeersByStyle(categoryName).observe(this, { beers ->
            // Filter the beers based on the query
            val filteredBeers = query?.let {
                beers.filter { beer ->
                    beer.name.contains(it, ignoreCase = true) || beer.description.contains(it, ignoreCase = true)
                }
            } ?: beers  // If no query, show all beers

            beerListAdapter.updateData(filteredBeers)
        })
    }
}
