package com.example.beeriq.ui.beerList

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer
import com.example.beeriq.data.local.beerDatabase.BeerDatabase
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import com.example.beeriq.data.local.beerDatabase.BeerViewModel
import com.example.beeriq.data.local.beerDatabase.BeerViewModelFactory

class BeerListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beerListAdapter: BeerListAdapter
    private lateinit var beerViewModel: BeerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beer_list)

        // Get the category name passed from the previous Activity
        val categoryName = intent.getStringExtra("CATEGORY_NAME") ?: ""

        // Set up RecyclerView and Adapter
        recyclerView = findViewById(R.id.recyclerViewBeerList)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize ViewModel and Repository
        val database = BeerDatabase.getInstance(applicationContext)
        val repository = BeerRepository(database.beerDatabaseDao)
        val viewModelFactory = BeerViewModelFactory(repository)
        beerViewModel = ViewModelProvider(this, viewModelFactory).get(BeerViewModel::class.java)

        // Observe LiveData from ViewModel and update RecyclerView
        beerViewModel.getBeersByStyle(categoryName).observe(this, { beers ->
            beerListAdapter = BeerListAdapter(beers) // Pass List<Beer> from database
            recyclerView.adapter = beerListAdapter
        })

        // Handle back button click
        val backButton: Button = findViewById(R.id.btnBack)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }
}


