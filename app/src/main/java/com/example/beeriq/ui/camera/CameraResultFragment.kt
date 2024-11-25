package com.example.beeriq.ui.camera

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer

class CameraResultFragment: Fragment(R.layout.fragment_camera_result) {
    private lateinit var beerName: TextView
    private lateinit var brewery: TextView
    private lateinit var abv: TextView


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        beerName = view.findViewById(R.id.beerName)
        brewery = view.findViewById(R.id.brewery)
        abv = view.findViewById(R.id.abv)

        val beer = arguments?.getSerializable("beer_object") as? Beer

        brewery.text = beer?.brewery
        beerName.text = beer?.name
        abv.text = "ABV: " + beer?.abv.toString()

    }
}