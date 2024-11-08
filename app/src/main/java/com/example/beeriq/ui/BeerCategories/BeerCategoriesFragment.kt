package com.example.beeriq.ui.BeerCategories

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.ui.BeerList.BeerListActivity

class BeerCategoriesFragment : Fragment() {

    private val viewModel: BeerCategoriesViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var beerCategoriesAdapter: BeerCategoriesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_beer_categories, container, false)

        // Find the RecyclerView by ID
        recyclerView = view.findViewById(R.id.beerGridView)

        // Set up the RecyclerView with a GridLayoutManager for a 2-column layout
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        // Create a list of BeerCategory items (replace with actual image resources and types)
        val beerList = listOf(
            BeerCategories(R.drawable.beer_image_ipa, "IPA"),
            BeerCategories(R.drawable.beer_image_stout, "Stout"),
            BeerCategories(R.drawable.beer_image_lager, "Lager"),
            BeerCategories(R.drawable.beer_image_pilsner, "Pilsner")
        )

        // Set up the adapter with the list and a click listener
        beerCategoriesAdapter = BeerCategoriesAdapter(requireContext(), beerList) { selectedBeer ->
            openBeerListActivity(selectedBeer)  // Call this function instead of Toast
        }

        // Attach the adapter to the RecyclerView
        recyclerView.adapter = beerCategoriesAdapter

        return view
    }

    // Open BeerListActivity with category name
    private fun openBeerListActivity(beerCategory: BeerCategories) {
        val intent = Intent(requireContext(), BeerListActivity::class.java)
        intent.putExtra("CATEGORY_NAME", beerCategory.beerType)  // Pass the category name
        startActivity(intent)  // Launch the BeerListActivity
    }
}

