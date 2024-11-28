package com.example.beeriq.ui.camera

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.beeriq.R

class CameraNoResultFragment : Fragment(R.layout.fragment_camera_no_result) {
    private lateinit var tryAgainButton: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tryAgainButton = view.findViewById(R.id.try_again_button)
        tryAgainButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}