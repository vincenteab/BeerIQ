package com.example.beeriq.ui.showMyBeers

import android.content.Context
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.R

class ShowMyBeersFragment: AppCompatActivity() {

    private lateinit var username:String

    private lateinit var recyclerView: RecyclerView
    private lateinit var backBtn: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.showmybeers)

        recyclerView = findViewById(R.id.recyclerView)
        backBtn = findViewById(R.id.backButton)

        recyclerView.layoutManager = LinearLayoutManager(this)

        loadData()

        backBtn.setOnClickListener{
            finish()
        }
    }

    private fun loadData() {
        val sharedPreferences = this.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val firebaseRepo = FirebaseRepo(this.getSharedPreferences("UserDetails", Context.MODE_PRIVATE))

        username = sharedPreferences.getString("username", "").toString()
        println("TEST MY BEERS:  $username")

        firebaseRepo.fetchSaves(username)
        firebaseRepo.savedBeersList.observe(this) { savedBeers ->
            savedBeers?.let {
                recyclerView.adapter = MyBeersRecyclerAdapter(it)
            } ?: println("No saved beers found")
        }
    }

}