package com.example.beeriq.ui.beerCategories


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.BeerDatabase
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import com.example.beeriq.data.local.beerDatabase.BeerViewModel
import com.example.beeriq.data.local.beerDatabase.BeerViewModelFactory
import com.example.beeriq.tools.Util.parseBeerCategories
import com.example.beeriq.ui.beerCategories.BeerCategoriesAdapter
import com.example.beeriq.ui.beerCategories.BeerCategory
import com.example.beeriq.ui.beerList.BeerListActivity

class BeerCategoriesFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var beerCategoriesAdapter: BeerCategoriesAdapter
    private lateinit var searchView: SearchView
    private lateinit var beerViewModel: BeerViewModel
    private var beerCategories: List<String> = emptyList()
    private var filteredBeerCategories: List<String> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // Get the BeerDatabase instance and create the BeerRepository
        val beerDatabase = BeerDatabase.getInstance(requireContext())
        val beerRepository = BeerRepository(beerDatabase.beerDatabaseDao)

        // Create the ViewModel using the BeerViewModelFactory
        val factory = BeerViewModelFactory(beerRepository)
        beerViewModel = ViewModelProvider(this, factory).get(BeerViewModel::class.java)

        // Observe the beer categories LiveData
        beerViewModel.beerCategories.observe(viewLifecycleOwner, Observer { categories ->
            beerCategories = categories
            filteredBeerCategories = beerCategories
            beerCategoriesAdapter = BeerCategoriesAdapter(filteredBeerCategories) { selectedCategory ->
                openBeerListActivity(selectedCategory)
            }
            recyclerView.adapter = beerCategoriesAdapter
        })

        // Fetch beer categories from the database
        beerViewModel.fetchCategories()

        // RecyclerView setup
        recyclerView = view.findViewById(R.id.beerGridView)
        recyclerView.layoutManager = LinearLayoutManager(context)

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

    private fun filterCategories(query: String?) {
        val filteredList = query?.let {
            beerCategories.filter { category ->
                category.contains(it, ignoreCase = true)
            }
        } ?: beerCategories

        filteredBeerCategories = filteredList
        beerCategoriesAdapter = BeerCategoriesAdapter(filteredBeerCategories) { selectedCategory ->
            openBeerListActivity(selectedCategory)
        }
        recyclerView.adapter = beerCategoriesAdapter
    }

    private fun openBeerListActivity(category: String) {
        val intent = Intent(requireContext(), BeerListActivity::class.java)
        intent.putExtra("CATEGORY_NAME", category) // Pass the category name
        startActivity(intent)
    }
}


