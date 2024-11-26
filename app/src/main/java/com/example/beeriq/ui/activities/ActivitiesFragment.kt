package com.example.beeriq.ui.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.beeriq.databinding.FragmentActivitiesBinding

class ActivitiesFragment : Fragment() {
    private lateinit var binding: FragmentActivitiesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentActivitiesBinding.inflate(inflater, container, false)



        return binding.root
    }
}