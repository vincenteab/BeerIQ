package com.example.beeriq.ui.beerCategories


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.tools.Util.parseBeerCategories
import com.example.beeriq.ui.beerCategories.BeerCategoriesAdapter
import com.example.beeriq.ui.beerCategories.BeerCategory

class BeerCategoriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beerCategoriesAdapter: BeerCategoriesAdapter
    private lateinit var searchView: SearchView
    private var beerCategories: List<BeerCategory> = emptyList()
    private var filteredBeerCategories: List<BeerCategory> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Parse beer categories
        beerCategories = parseBeerCategories(requireContext())
        filteredBeerCategories = beerCategories

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.beerGridView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        beerCategoriesAdapter = BeerCategoriesAdapter(filteredBeerCategories) { selectedCategory ->
            openBeerListActivity(selectedCategory)
        }
        recyclerView.adapter = beerCategoriesAdapter

        // Setup SearchView
        searchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterCategories(newText)
                return true
            }
        })

        return view
    }


    private fun toggleSearchViewVisibility(view: View) {
        if (!::searchView.isInitialized) {
            searchView = SearchView(requireContext())
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    filterCategories(newText)
                    return true
                }
            })

            // Optionally, add SearchView to a layout dynamically
            val toolbar = view.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            toolbar.addView(searchView)
        }
        searchView.visibility =
            if (searchView.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun filterCategories(query: String?) {
        val filteredList = query?.let {
            beerCategories.filter { category ->
                category.name.contains(it, ignoreCase = true)
            }
        } ?: beerCategories

        filteredBeerCategories = filteredList
        beerCategoriesAdapter = BeerCategoriesAdapter(filteredBeerCategories) { selectedCategory ->
            openBeerListActivity(selectedCategory)
        }
        recyclerView.adapter = beerCategoriesAdapter
    }

    private fun openBeerListActivity(category: BeerCategory) {
        // Implement activity navigation logic here
    }
}
