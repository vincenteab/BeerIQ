package com.example.beeriq.ui.showMyBeers

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.ui.userprofile.Save

class MyBeersRecyclerAdapter(private val savedBeers: List<Save>) :
    RecyclerView.Adapter<MyBeersRecyclerAdapter.SavedBeerViewHolder>() {

    class SavedBeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.dateCreated)
        val title: TextView = itemView.findViewById(R.id.cardTitle)
        val image: ImageView = itemView.findViewById(R.id.cardImage)
        val description: TextView = itemView.findViewById(R.id.cardDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedBeerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_beer, parent, false) // Use the provided XML
        return SavedBeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedBeerViewHolder, position: Int) {
        val beer = savedBeers[position]

        holder.username.text = beer.username
        holder.date.text = beer.date
        holder.title.text = beer.brewery
        holder.description.text = beer.description.removePrefix("Notes:").trimEnd('\t').trim()

        // Decode Base64 and set the image
        val bitmap = decodeBase64ToBitmap(beer.image)
        if (bitmap != null) {
            holder.image.setImageBitmap(bitmap)
        }

        // Handle click event to show dialog
        holder.itemView.setOnClickListener {
            val dialog = BeerDetailsDialogFragment.newInstance(
                beer.brewery,
                beer.description,
                beer.abv,
                beer.style,
                beer.minIBU,
                beer.maxIBU,
                beer.reviewAroma,
                beer.reviewAppearance,
                beer.reviewPalate,
                beer.reviewTaste,
                bitmap
            )
            dialog.show((holder.itemView.context as AppCompatActivity).supportFragmentManager, "BeerDetailsDialog")
        }
    }

    override fun getItemCount(): Int {
        return savedBeers.size
    }

    // Helper function to decode Base64 string to Bitmap
    private fun decodeBase64ToBitmap(base64Str: String): Bitmap? {
        return try {
            val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


