package com.example.beeriq.ui.beerSearch

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer
import com.example.beeriq.ui.camera.BeerSuggestionAdapter

class StyleResultFragment: Fragment(R.layout.fragment_style_results) {
    private lateinit var beerRecyclerView: RecyclerView
    private lateinit var beerSuggestionAdapter: BeerSuggestionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack(R.id.navigation_search, false)
        }

        beerRecyclerView = view.findViewById(R.id.beerRecyclerView)
        beerRecyclerView.setPadding(0, 0, 0, 0)
        beerRecyclerView.clipToPadding = false

        val beerList = arguments?.getSerializable("beer_list") as List<Beer>
        val byteArray = arguments?.getByteArray("bitmap") as ByteArray

        beerSuggestionAdapter = BeerSuggestionAdapter(beerList) { beer ->
            navigateToBeerDetails(beer, byteArray)
        }
        beerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        beerRecyclerView.adapter = beerSuggestionAdapter

    }

    private fun navigateToBeerDetails(beer: Beer, byteArray: ByteArray) {
        val bundle = Bundle().apply {
            putSerializable("beer_object", beer)
            putByteArray("bitmap", byteArray)
        }
        findNavController().navigate(R.id.navigation_beer_details, bundle)
    }

    fun onBackPressed() {
        findNavController().popBackStack(R.id.navigation_search, false)  // Pop back to SearchFragment
    }

}