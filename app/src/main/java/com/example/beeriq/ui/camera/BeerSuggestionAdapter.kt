package com.example.beeriq.ui.camera

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer

class BeerSuggestionAdapter(
    private val beerList: List<Beer>,
    private val onItemClick: (Beer) -> Unit
) : RecyclerView.Adapter<BeerSuggestionAdapter.BeerViewHolder>() {

    inner class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerName: TextView = itemView.findViewById(R.id.beerName)
        val brewery: TextView = itemView.findViewById(R.id.brewery)
        val abv: TextView = itemView.findViewById(R.id.abv)
        fun bind(beer: Beer) {
            itemView.setOnClickListener {
                onItemClick(beer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, ViewType: Int): BeerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_beer_suggestion, parent, false)
        return BeerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BeerViewHolder, position: Int) {
        val beer = beerList[position]
        holder.beerName.text = beer.beerFullName
        holder.brewery.text = beer.brewery
        holder.abv.text = "ABV: ${beer.abv}"
        holder.bind(beer)
    }

    override fun getItemCount() = beerList.size
}