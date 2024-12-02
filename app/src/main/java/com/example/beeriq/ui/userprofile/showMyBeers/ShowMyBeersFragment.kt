package com.example.beeriq.ui.userprofile.showMyBeers

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.R

class ShowMyBeersFragment : Fragment() {

    private lateinit var username: String
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.showmybeers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        // Initialize UI components
        recyclerView = view.findViewById(R.id.recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Show ProgressBar and hide RecyclerView initially
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        loadData(progressBar)

    }

    private fun loadData(progressBar: ProgressBar) {
        val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val firebaseRepo = FirebaseRepo(requireContext().getSharedPreferences("UserDetails", Context.MODE_PRIVATE))

        username = sharedPreferences.getString("username", "").toString()

        firebaseRepo.fetchSaves(username)

        firebaseRepo.savedBeersList.observe(viewLifecycleOwner) { savedBeers ->

            if (!isAdded) {
                println("Debug: Fragment is not attached. Skipping UI update.")
                return@observe
            }

            Handler(Looper.getMainLooper()).postDelayed({
                if (savedBeers != null) {
                    recyclerView.adapter = MyBeersRecyclerAdapter(savedBeers, requireContext())
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    println("No saved beers found")
                    progressBar.visibility = View.GONE
                }
            }, 1500) // 500ms delay for smooth transition
        }
    }
}
