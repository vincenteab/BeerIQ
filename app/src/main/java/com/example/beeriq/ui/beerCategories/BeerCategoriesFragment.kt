package com.example.beeriq.ui.beerCategories

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.tools.Util.parseBeerCategories
import com.example.beeriq.ui.beerList.BeerListActivity

class BeerCategoriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beerCategoriesAdapter: BeerCategoriesAdapter
    private lateinit var beerCategories: List<BeerCategory>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Get beer categories from the CSV file
        beerCategories = parseBeerCategories(requireContext())

        recyclerView = view.findViewById(R.id.beerGridView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Set up the adapter
        beerCategoriesAdapter = BeerCategoriesAdapter(beerCategories) { selectedCategory ->
            // Handle category click (e.g., open BeerListActivity for the selected category)
            openBeerListActivity(selectedCategory)
        }

        recyclerView.adapter = beerCategoriesAdapter

        return view
    }

    private fun openBeerListActivity(category: BeerCategory) {
        val intent = Intent(requireContext(), BeerListActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category.name)
        startActivity(intent)
    }
}


