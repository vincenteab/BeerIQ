package com.example.beeriq.ui.beerSearch

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer
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
    private lateinit var categoryText: TextView
    private lateinit var gridLayout: GridLayout
    private lateinit var beerAdapter: BeerSearchAdapter
    private lateinit var repository: BeerRepository

    private lateinit var lagerCard: MaterialCardView
    private lateinit var ipaCard: MaterialCardView
    private lateinit var paleAleCard: MaterialCardView
    private lateinit var pilsnerCard: MaterialCardView
    private lateinit var porterCard: MaterialCardView
    private lateinit var stoutCard: MaterialCardView
    private lateinit var wildAleCard: MaterialCardView
    private lateinit var wheatBeerCard: MaterialCardView
    private lateinit var strongAleCard: MaterialCardView
    private lateinit var pumpkinBeerCard: MaterialCardView
    private lateinit var happoshuCard: MaterialCardView
    private lateinit var winterWarmer: MaterialCardView


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
        gridLayout = view.findViewById(R.id.category_grid)
        categoryText = view.findViewById(R.id.category_text)
        beerRecyclerView = view.findViewById(R.id.beerRecyclerView)

        lagerCard = view.findViewById(R.id.lager_card)
        lagerCard.setOnClickListener {
            viewModel.searchStyle("Lager")
        }
        ipaCard = view.findViewById(R.id.ipa_card)
        ipaCard.setOnClickListener {
            viewModel.searchStyle("IPA")
        }
        paleAleCard = view.findViewById(R.id.paleAle_card)
        paleAleCard.setOnClickListener {
            viewModel.searchStyle("Pale Ale")
        }
        pilsnerCard = view.findViewById(R.id.pilsner_card)
        pilsnerCard.setOnClickListener {
            viewModel.searchStyle("Pilsner")
        }
        porterCard = view.findViewById(R.id.porter_card)
        porterCard.setOnClickListener {
            viewModel.searchStyle("Porter")
        }
        stoutCard = view.findViewById(R.id.stout_card)
        stoutCard.setOnClickListener {
            viewModel.searchStyle("Stout")
        }
        wildAleCard = view.findViewById(R.id.wild_ale_card)
        wildAleCard.setOnClickListener {
            viewModel.searchStyle("Wild Ale")
        }
        wheatBeerCard = view.findViewById(R.id.wheat_beer_card)
        wheatBeerCard.setOnClickListener {
            viewModel.searchStyle("Wheat Beer")
        }
        strongAleCard = view.findViewById(R.id.strong_ale_card)
        strongAleCard.setOnClickListener {
            viewModel.searchStyle("Strong Ale")
        }
        pumpkinBeerCard = view.findViewById(R.id.pumpkin_beer_card)
        pumpkinBeerCard.setOnClickListener {
            viewModel.searchStyle("Pumpkin Beer")
        }
        happoshuCard = view.findViewById(R.id.happoshu_card)
        happoshuCard.setOnClickListener {
            viewModel.searchStyle("Happoshu")
        }
        winterWarmer = view.findViewById(R.id.winter_warmer_card)
        winterWarmer.setOnClickListener {
            viewModel.searchStyle("Winter Warmer")
        }

        // Set up ViewModel
        val dao = BeerDatabase.getInstance(requireContext()).beerDatabaseDao
        repository = BeerRepository(dao)
        val factory = BeerCategoriesViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[BeerCategoriesViewModel::class.java]

        // Set up RecyclerView
        beerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        beerAdapter = BeerSearchAdapter(emptyList()) { selectedBeer ->
            navigateToBeerDetails(selectedBeer, ByteArray(0))
        }
        beerRecyclerView.adapter = beerAdapter

        // Observe search results
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            if (results.isNotEmpty()) {
                showSearchResults()
                beerAdapter.updateData(results)
            } else {
                showGridLayout()
            }
        }
        viewModel.styleResults.observe(viewLifecycleOwner) { beers ->
            if (beers.isNotEmpty()) {
                viewModel.clear()
                val bundle = Bundle().apply {
                    putSerializable("beer_list", ArrayList(beers))
                    putByteArray("bitmap", ByteArray(0))
                }
                Log.d("testing", "${beers}")
                findNavController().navigate(R.id.style_results, bundle)
            }
        }

        searchView.editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchBeers(query) // Update search results in ViewModel
                } else {
                     // Clear results when query is empty
                    showGridLayout()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        searchView.editText.setOnEditorActionListener { _, _, _ ->
            searchView.hide()
            true
        }

    }

    private fun navigateToBeerDetails(beer: Beer, byteArray: ByteArray) {
        val bundle = Bundle().apply {
            putSerializable("beer_object", beer)
            putByteArray("bitmap", byteArray)
        }
        findNavController().navigate(R.id.navigation_beer_details, bundle)
    }

    private fun showSearchResults() {
        gridLayout.visibility = View.GONE
        categoryText.visibility = View.GONE
        beerRecyclerView.visibility = View.VISIBLE
    }

    private fun showGridLayout() {
        gridLayout.visibility = View.VISIBLE
        categoryText.visibility = View.VISIBLE
        beerRecyclerView.visibility = View.GONE
    }
}
