package com.example.beeriq.ui.beerCategories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R

class BeerCategoriesAdapter(
    private val beerCategories: List<String>,
    private val itemClickListener: (String) -> Unit
) : RecyclerView.Adapter<BeerCategoriesAdapter.BeerCategoryViewHolder>() {

    inner class BeerCategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)

        fun bind(category: String) {
            categoryName.text = category
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

