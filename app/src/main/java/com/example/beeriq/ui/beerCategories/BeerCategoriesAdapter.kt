package com.example.beeriq.ui.beerCategories

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class BeerCategoriesAdapter(
    private val context: Context,
    private val beerList: List<BeerCategories>,
    private val itemClickListener: (BeerCategories) -> Unit  // Lambda for handling item click
) : RecyclerView.Adapter<BeerCategoriesAdapter.BeerCategoryViewHolder>() {

    // ViewHolder class for each item in the RecyclerView
    class BeerCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val beerImage: ImageView = itemView.findViewById(R.id.beerImage)
        val beerType: TextView = itemView.findViewById(R.id.beerType)

        // Binding the data to the item view
        fun bind(beerCategory: BeerCategories, clickListener: (BeerCategories) -> Unit) {
            beerImage.setImageResource(beerCategory.imageResId) // Set the image
            beerType.text = beerCategory.beerType // Set the beer type

            // Handle click
            itemView.setOnClickListener {
                clickListener(beerCategory)  // Pass the clicked item to the click listener
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerCategoryViewHolder {
        val itemView = LayoutInflater.from(context)
            .inflate(R.layout.beer_grid_item, parent, false)  // Inflate the item layout
        return BeerCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BeerCategoryViewHolder, position: Int) {
        val beerCategory = beerList[position]
        holder.bind(beerCategory, itemClickListener)  // Bind the data to the view
    }

    override fun getItemCount(): Int {
        return beerList.size  // Return the size of the list
    }
}
