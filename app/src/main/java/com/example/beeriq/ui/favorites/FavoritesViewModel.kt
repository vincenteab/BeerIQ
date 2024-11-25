package com.example.beeriq.ui.favorites

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.beeriq.model.Beer
import com.google.firebase.database.*

class FavoritesViewModel : ViewModel() {

    private val _favoriteBeers = MutableLiveData<List<Beer>>()
    val favoriteBeers: LiveData<List<Beer>> get() = _favoriteBeers

    private val database: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("favorite_beers")

    init {
        fetchFavoriteBeers()
    }

    private fun fetchFavoriteBeers() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val beers = mutableListOf<Beer>()
                for (beerSnapshot in snapshot.children) {
                    val beer = beerSnapshot.getValue(Beer::class.java)
                    beer?.let { beers.add(it) }
                }
                _favoriteBeers.value = beers
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}
