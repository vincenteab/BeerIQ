package com.example.beeriq.ui.camera

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.example.beeriq.data.local.beerDatabase.Beer

class BeerDetailsFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val beer = arguments?.getSerializable("beer_object") as? Beer
        if (beer != null) {
            Log.d("testing", "DETAILS: ${beer.beerFullName}")
        }
    }

}