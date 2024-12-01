package com.example.beeriq.ui.MyPosts

import android.content.Context
import android.os.Bundle
import android.os.Looper
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.R

class MyPostsFragment : Fragment() {

    private lateinit var username: String
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.recycler_my_posts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)

        // Initialize UI components
        recyclerView = view.findViewById(R.id.my_posts_recyclerView)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Show ProgressBar and hide RecyclerView initially
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE

        loadData(progressBar)

    }

    private fun loadData(progressBar: ProgressBar) {
        val sharedPreferences = requireContext().getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val firebaseRepo = FirebaseRepo(sharedPreferences)

        username = sharedPreferences.getString("username", "").toString()
        println("Debug: Current username -> $username")

        if (username.isEmpty()) {
            println("Debug: Username is empty. Cannot fetch posts.")
            return
        }

        // Fetch posts for the user
        firebaseRepo.fetchMyPosts(username)

        // Observe posts LiveData
        firebaseRepo.myPostsList.observe(viewLifecycleOwner) { userPosts ->
            Handler(Looper.getMainLooper()).postDelayed({
                if (userPosts != null && userPosts.isNotEmpty()) {
                    recyclerView.adapter = MypostsAdapter(userPosts)
                    recyclerView.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                } else {
                    println("Debug: No posts found or userPosts is null.")
                    progressBar.visibility = View.GONE
                }
            }, 1000)
        }
    }



}
