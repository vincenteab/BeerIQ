package com.example.beeriq.ui.beerList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class BeerListAdapter(private val beerList: List<Beer>) : RecyclerView.Adapter<BeerListAdapter.BeerViewHolder>() {

    class BeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerName: TextView = itemView.findViewById(R.id.beerName)

        fun bind(beer: Beer) {
            beerName.text = beer.name
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
}
