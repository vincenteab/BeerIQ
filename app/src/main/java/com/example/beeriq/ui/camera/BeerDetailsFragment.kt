package com.example.beeriq.ui.camera

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.slider.Slider
import java.math.RoundingMode

class BeerDetailsFragment : Fragment(R.layout.fragment_beer_details) {
    private lateinit var imageView: ShapeableImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val beerName: TextView = view.findViewById(R.id.beerName)
        val brewery: TextView = view.findViewById(R.id.brewery)
        val abv: TextView = view.findViewById(R.id.abv)
        val ratingBar: RatingBar = view.findViewById(R.id.rating)
        val ratingValue: TextView = view.findViewById(R.id.rating_value)
        val ratingNum: TextView = view.findViewById(R.id.rating_num)
        val brewersNotes: TextView = view.findViewById(R.id.brewers_notes)
        val style: TextView = view.findViewById(R.id.style)
        val ibu: TextView = view.findViewById(R.id.ibu)
        val aromaSlider: Slider = view.findViewById(R.id.aroma_slider)
        val appearanceSlider: Slider = view.findViewById(R.id.appearance_slider)
        val palateSlider: Slider = view.findViewById(R.id.palate_slider)
        val tasteSlider: Slider = view.findViewById(R.id.taste_slider)
        val numReviews: TextView = view.findViewById(R.id.num_reviews)
        val hoppyText: TextView = view.findViewById(R.id.hoppy_text)
        val fruitsText: TextView = view.findViewById(R.id.fruits_text)
        val bitterText: TextView = view.findViewById(R.id.bitter_text)
        val moreButton: FrameLayout = view.findViewById(R.id.more_button)
        val moreButtonText: TextView = view.findViewById(R.id.more_button_text)
        val hiddenContent: LinearLayout = view.findViewById(R.id.hidden_content)
        val alcoholText: TextView = view.findViewById(R.id.alcohol_text)
        val spicesText: TextView = view.findViewById(R.id.spices_text)
        val maltyText: TextView = view.findViewById(R.id.malty_text)
        val sweetText: TextView = view.findViewById(R.id.sweet_text)
        val saltyText: TextView = view.findViewById(R.id.salty_text)
        val astringencyText: TextView = view.findViewById(R.id.astringency_text)
        val sourText: TextView = view.findViewById(R.id.sour_text)

        val beer = arguments?.getSerializable("beer_object") as? Beer
        val byteArray = arguments?.getByteArray("bitmap") as ByteArray
        byteArray.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            imageView = view.findViewById(R.id.beer_image)
            imageView.setImageBitmap(bitmap)
        }
        if (beer != null) {
            brewery.text = beer.brewery
            beerName.text = beer.name
            abv.text = "ABV: " + beer.abv.toString() + "%"
            val formattedRating = beer.reviewOverall.toBigDecimal().setScale(1, RoundingMode.HALF_UP).toFloat()
            ratingBar.stepSize = 0.01f
            ratingBar.rating = formattedRating
            ratingValue.text = formattedRating.toString()
            ratingNum.text = beer.numOfReviews.toString() + " ratings"

            var notes = beer.description.removePrefix("Notes:").removeSuffix("\\t")
            if (notes.isEmpty()) { notes = "None"}
            brewersNotes.text = notes
            style.text = beer.style
            ibu.text = beer.minIBU.toString() + " - " + beer.maxIBU

            aromaSlider.value = beer.reviewAroma.toFloat()
            appearanceSlider.value = beer.reviewAppearance.toFloat()
            palateSlider.value = beer.reviewPalate.toFloat()
            tasteSlider.value = beer.reviewTaste.toFloat()
            numReviews.text = "Based on " + beer.numOfReviews + " reviews"

            hoppyText.text = beer.hoppy.toString() + " mentions of hoppy notes"
            fruitsText.text = beer.fruits.toString() + " mentions of fruity notes"
            bitterText.text = beer.bitter.toString() + " mentions of bitter notes"
            alcoholText.text = beer.alcohol.toString() + " mentions of alcohol notes"
            spicesText.text = beer.spices.toString() + " mentions of spicy notes"
            maltyText.text = beer.malty.toString() + " mentions of malty notes"
            sweetText.text = beer.sweet.toString() + " mentions of sweet notes"
            saltyText.text = beer.salty.toString() + " mentions of salty notes"
            astringencyText.text = beer.astringency.toString() + " mentions of astringent notes"
            sourText.text = beer.sour.toString() + " mentions of sour notes"

            moreButton.setOnClickListener {
                if (hiddenContent.visibility == View.GONE) {
                    hiddenContent.visibility = View.VISIBLE
                    moreButtonText.text = "Show less"
                } else {
                    hiddenContent.visibility = View.GONE
                    moreButtonText.text = "Show More"
                }
            }
        }
    }
}