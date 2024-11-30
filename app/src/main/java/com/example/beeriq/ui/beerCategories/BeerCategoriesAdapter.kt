package com.example.beeriq.ui.beerCategories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class BeerCategoriesAdapter(
    private val beerCategories: List<BeerCategory>,
    private val itemClickListener: (BeerCategory) -> Unit
) : RecyclerView.Adapter<BeerCategoriesAdapter.BeerCategoryViewHolder>() {

    inner class BeerCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: BeerCategory) {
            categoryName.text = category.name
            itemView.setOnClickListener {
                itemClickListener(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeerCategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_beer_category, parent, false)
        return BeerCategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: BeerCategoryViewHolder, position: Int) {
        holder.bind(beerCategories[position])
    }

    override fun getItemCount(): Int {
        return beerCategories.size
    }
}
