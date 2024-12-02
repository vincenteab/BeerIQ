package com.example.beeriq.ui.beerList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer // Import the Beer class from the database package

class BeerListAdapter(private var beerList: List<Beer>) : RecyclerView.Adapter<BeerListAdapter.BeerViewHolder>() {

    class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerName: TextView = itemView.findViewById(R.id.beerName)
        val breweryName: TextView = itemView.findViewById((R.id.brewery))
        val abv: TextView = itemView.findViewById((R.id.abv))
        val review: TextView = itemView.findViewById((R.id.review))

        fun bind(beer: Beer) {
            beerName.text = beer.name
            breweryName.text = beer.brewery
            abv.text = "ABV: ${beer.abv}%"  // Example: Show ABV with a '%' sign
            review.text = String.format("Review: %.1f / 5",beer.reviewOverall)  // Displaying review overall score
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beer, parent, false)
        return BeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = beerList[position]
        holder.bind(beer)
    }

    override fun getItemCount(): Int {
        return beerList.size
    }

    // New method to update data
    fun updateData(newBeerList: List<Beer>) {
        beerList = newBeerList
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }
}

