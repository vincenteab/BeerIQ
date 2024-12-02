package com.example.beeriq.ui.showMyBeers

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.Settings.Global.putInt
import android.provider.Settings.Global.putString
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer
import com.example.beeriq.ui.userprofile.Save
import com.google.android.material.imageview.ShapeableImageView

class MyBeersRecyclerAdapter(private val savedBeers: List<Save>, private val context: Context) :
    RecyclerView.Adapter<MyBeersRecyclerAdapter.SavedBeerViewHolder>() {

    class SavedBeerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val date: TextView = itemView.findViewById(R.id.dateCreated)
        val title: TextView = itemView.findViewById(R.id.cardTitle)
        val image: ImageView = itemView.findViewById(R.id.cardImage)
        val description: TextView = itemView.findViewById(R.id.cardDescription)
        val profileImageView: ShapeableImageView = itemView.findViewById(R.id.profile_image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedBeerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_saved_beer, parent, false)
        return SavedBeerViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedBeerViewHolder, position: Int) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val profilePicBase64 = sharedPreferences.getString("profilePic", "")
        val beer = savedBeers[position]

        holder.username.text = beer.username
        holder.date.text = beer.date
        holder.title.text = beer.brewery
        holder.description.text = beer.description.removePrefix("Notes:").trimEnd('\t').trim()

        if (!profilePicBase64.isNullOrEmpty()) {
            // Decode Base64 string to Bitmap
            val bitmap = decodeBase64ToBitmap(profilePicBase64)

            if (bitmap != null) {
                // Set the decoded Bitmap to the ImageView
                holder.profileImageView.setImageBitmap(bitmap)
            } else {
                println("Debug: Failed to decode profile picture.")
            }
        } else {
            println("Debug: No profile picture found. Using default.")
            // Optionally, set a default image
            holder.profileImageView.setImageResource(R.drawable.ic_launcher_foreground)
        }

        // Decode Base64 and set the image
        val bitmap = decodeBase64ToBitmap(beer.image)
        if (bitmap != null) {
            holder.image.setImageBitmap(bitmap)
        }

        // Handle click event to navigate to BeerDetailsFragment
        holder.itemView.setOnClickListener {
            val bundle = Bundle().apply {
                val byteArray = Base64.decode(beer.image, Base64.DEFAULT)
                putByteArray("bitmap", byteArray)
                putSerializable("beer_object", Beer(
                    name = beer.brewery, // If the brewery name corresponds to the beer name
                    style = beer.style,
                    brewery = beer.brewery,
                    beerFullName = beer.beerFullName,
                    description = beer.description,
                    abv = beer.abv,
                    minIBU = beer.minIBU,
                    maxIBU = beer.maxIBU,
                    astringency = beer.astringency,
                    body = beer.body,
                    alcohol = beer.alcohol,
                    bitter = beer.bitter,
                    sweet = beer.sweet,
                    sour = beer.sour,
                    salty = beer.salty,
                    fruits = beer.fruits,
                    hoppy = beer.hoppy,
                    spices = beer.spices,
                    malty = beer.malty,
                    reviewAroma = beer.reviewAroma,
                    reviewAppearance = beer.reviewAppearance,
                    reviewPalate = beer.reviewPalate,
                    reviewTaste = beer.reviewTaste,
                    reviewOverall = beer.reviewOverall,
                    numOfReviews = beer.numOfReviews
                ))
            }

            // Use Navigation Component to navigate
            Navigation.findNavController(holder.itemView)
                .navigate(R.id.action_showMyBeersFragment_to_navigation_beer_details, bundle)
        }
    }


    override fun getItemCount(): Int = savedBeers.size

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
