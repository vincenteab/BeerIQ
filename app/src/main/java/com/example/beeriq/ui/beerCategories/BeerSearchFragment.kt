package com.example.beeriq.ui.beerCategories

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.BeerDatabase
import com.example.beeriq.data.local.beerDatabase.BeerRepository
import com.google.android.material.card.MaterialCardView
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView

class BeerSearchFragment : Fragment() {

    private lateinit var viewModel: BeerCategoriesViewModel
    private lateinit var beerRecyclerView: RecyclerView
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    private lateinit var searchButton: Button
    private lateinit var beerAdapter: BeerSearchAdapter
    private lateinit var repository: BeerRepository

    private lateinit var lagerCard: MaterialCardView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        searchBar = view.findViewById(R.id.search_bar)
        searchView = view.findViewById(R.id.search_view)
        searchButton = view.findViewById(R.id.searchButton)
        beerRecyclerView = view.findViewById(R.id.beerRecyclerView)

        lagerCard = view.findViewById(R.id.lager_card)

        // Set up ViewModel
        val dao = BeerDatabase.getInstance(requireContext()).beerDatabaseDao
        repository = BeerRepository(dao)
        val factory = BeerCategoriesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BeerCategoriesViewModel::class.java]

        // Set up RecyclerView
        beerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        beerAdapter = BeerSearchAdapter(emptyList()) // Initialize with empty list
        beerRecyclerView.adapter = beerAdapter

        // Observe search results
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            beerAdapter.updateData(results)
        }

        viewModel.styleResults.observe(viewLifecycleOwner) { results ->
            Log.d("testing", "${results}")
        }

        lagerCard.setOnClickListener {
            viewModel.searchStyle("Lager")
        }

        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchBeers(query) // Update search results in ViewModel
                } else {
                     // Clear results when query is empty
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        searchView.editText.setOnEditorActionListener { _, _, _ ->
            searchView.hide()
            true
        }

    }
}
