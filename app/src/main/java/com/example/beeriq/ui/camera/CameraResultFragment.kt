package com.example.beeriq.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer

class CameraResultFragment: Fragment(R.layout.fragment_camera_result) {
    private lateinit var beerRecyclerView: RecyclerView
    private lateinit var beerSuggestionAdapter: BeerSuggestionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beerRecyclerView = view.findViewById(R.id.beerRecyclerView)
        beerRecyclerView.setPadding(0, 0, 0, 0)
        beerRecyclerView.clipToPadding = false

        val beerList = arguments?.getSerializable("beer_object") as List<Beer>
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
}
