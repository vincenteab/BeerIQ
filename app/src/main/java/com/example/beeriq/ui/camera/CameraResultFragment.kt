package com.example.beeriq.ui.camera

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer

class CameraResultFragment: Fragment(R.layout.fragment_camera_result) {
    private lateinit var beerRecyclerView: RecyclerView
    private lateinit var beerSuggestionAdapter: BeerSuggestionAdapter
    private lateinit var cameraViewModel: CameraViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        beerRecyclerView = view.findViewById(R.id.beerRecyclerView)
        beerRecyclerView.setPadding(0, 0, 0, 0)
        beerRecyclerView.clipToPadding = false
        val spacing = resources.getDimensionPixelSize(R.dimen.suggestion_card)
        beerRecyclerView.addItemDecoration(ItemSpacingDecoration(spacing))

        val beerList = arguments?.getSerializable("beer_object") as List<Beer>

        beerSuggestionAdapter = BeerSuggestionAdapter(beerList)
        beerRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        beerRecyclerView.adapter = beerSuggestionAdapter
    }
}

class ItemSpacingDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.top = space // Add space above each item (for top padding effect)
        outRect.bottom = space // Add space below each item (for bottom padding effect)
    }
}