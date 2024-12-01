package com.example.beeriq.ui.MyPosts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class FavoritesActivity : AppCompatActivity() {

    private lateinit var adapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_my_posts)

        // Find RecyclerView by ID
        val recyclerView = findViewById<RecyclerView>(R.id.favorites_recycler_view)

        // Set up RecyclerView
        adapter = FavoritesAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Sample data to test
        val sampleBeers = listOf(
            Beer("1", "Beer 1"),
            Beer("2", "Beer 2"),
            Beer("3", "Beer 3")
        )

        // Submit list to adapter
        adapter.submitList(sampleBeers)
    }
}
