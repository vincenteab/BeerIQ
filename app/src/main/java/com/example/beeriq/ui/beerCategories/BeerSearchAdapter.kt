package com.example.beeriq.ui.beerCategories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer

class BeerSearchAdapter(
    private var beerList: List<Beer>,
) : RecyclerView.Adapter<BeerSearchAdapter.BeerViewHolder>() {

    // ViewHolder to hold item views
    class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerName: TextView = itemView.findViewById(R.id.beer_name)
        val brewery: TextView = itemView.findViewById(R.id.brewery)
        val abv: TextView = itemView.findViewById(R.id.abv)
    }

    // Inflate the item layout and return the ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.query_beer, parent, false)
        return BeerViewHolder(itemView)
    }

    // Bind data to the views in the ViewHolder
    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = beerList[position]
        holder.beerName.text = beer.name
        holder.brewery.text = beer.brewery
        holder.abv.text = beer.abv.toString()
    }

    // Return the total number of items in the list
    override fun getItemCount() = beerList.size

    fun updateData(newBeerList: List<Beer>) {
        beerList = newBeerList
        notifyDataSetChanged() // Notify RecyclerView to refresh
    }
}
