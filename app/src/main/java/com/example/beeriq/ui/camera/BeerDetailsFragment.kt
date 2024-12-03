package com.example.beeriq.ui.camera

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.beeriq.FirebaseRepo
import com.example.beeriq.R
import com.example.beeriq.data.local.beerDatabase.Beer
import com.example.beeriq.ui.activities.Post
import com.example.beeriq.ui.userprofile.Save
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.slider.Slider
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Calendar

class BeerDetailsFragment : Fragment(R.layout.fragment_beer_details) {

    class PostDialog : DialogFragment(), DialogInterface.OnClickListener {
        private lateinit var username: TextView
        private lateinit var date: TextView
        private lateinit var beerImage: ImageView
        private lateinit var beerName: TextView
        private lateinit var style: TextView
        private lateinit var descriptionText: EditText
        private lateinit var cancelButton: Button
        private lateinit var postButton: Button
        private lateinit var bitmap: Bitmap

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(requireActivity())
            setStyle(STYLE_NO_FRAME, R.style.CustomDialog)
            val view = requireActivity().layoutInflater.inflate(R.layout.fragment_post_dialog, null)
            lateinit var dialog: Dialog
            builder.setView(view)
            dialog = builder.create()

            val sharedPreferences = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)
            val calendar = Calendar.getInstance().time
            val currentDate: String = SimpleDateFormat("MMM d, yyy").format(calendar)

            val beer = arguments?.getSerializable("beer_object") as Beer
            val byteArray = arguments?.getByteArray("bitmap") as ByteArray
            lifecycleScope.launch {
                byteArray.let {
                    bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                    beerImage = view.findViewById(R.id.beer_image)
                    beerImage.setImageBitmap(bitmap)
                }
            }
            username = view.findViewById(R.id.username)
            username.text = sharedPreferences.getString("username", "")
            date = view.findViewById(R.id.date)
            date.text = currentDate
            beerImage = view.findViewById(R.id.beer_image)
            beerName = view.findViewById(R.id.beer_name)
            beerName.text = beer.beerFullName
            style = view.findViewById(R.id.style)
            style.text = beer.style
            descriptionText = view.findViewById(R.id.description)

            cancelButton = view.findViewById(R.id.cancel_button)
            cancelButton.setOnClickListener {
                dismiss()
            }

            postButton = view.findViewById(R.id.post_button)
            postButton.setOnClickListener {
                val description = descriptionText.text.toString()

                lifecycleScope.launch {
                    val repo = FirebaseRepo(sharedPreferences)
                    val post = Post(
                        username = sharedPreferences.getString("username", "").toString(),
                        date = currentDate,
                        image = bitmapToBase64(bitmap),
                        beername = beer.beerFullName,
                        subtitle = beer.style,
                        comment = description
                    )
                    repo.addPost(post)
                }
                Toast.makeText(requireContext(), "Posted", Toast.LENGTH_SHORT).show()
                dismiss()
            }
            return dialog
        }

        fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): String {
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(format, quality, byteArrayOutputStream) // Compress the bitmap
            val byteArray = byteArrayOutputStream.toByteArray() // Convert to ByteArray
            return Base64.encodeToString(byteArray, Base64.DEFAULT) // Convert to Base64 string
        }

        override fun onStart() {
            super.onStart()
            dialog?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        override fun onClick(dialog: DialogInterface?, which: Int) {
        }
    }

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
        var bitmap: Bitmap? = null

        val saveButton: Button = view.findViewById(R.id.save_button)
        val postButton: Button = view.findViewById(R.id.post_button)

        val sharedPreferences = requireContext().getSharedPreferences("UserData", MODE_PRIVATE)

        val beer = arguments?.getSerializable("beer_object") as? Beer
        val byteArray = arguments?.getByteArray("bitmap") as ByteArray
        if (byteArray != null && byteArray.isNotEmpty()) {
            byteArray.let {
                bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
                imageView = view.findViewById(R.id.beer_image)
                imageView.setImageBitmap(bitmap)
            }
        } else {
            saveButton.visibility = View.GONE
            postButton.visibility = View.GONE
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

            saveButton.setOnClickListener {
                val calendar = Calendar.getInstance().time
                val currentDate: String = SimpleDateFormat("MMM d, yyy").format(calendar)

                lifecycleScope.launch {
                    val repo = FirebaseRepo(sharedPreferences)
                    val save = Save(
                        username = sharedPreferences.getString("username", "").toString(),
                        image = bitmapToBase64(bitmap!!),
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
                        numOfReviews = beer.numOfReviews,
                        date = currentDate
                    )
                    repo.addSave(save)
                }
            }

            postButton.setOnClickListener {
                val postDialog = PostDialog()
                val bundle = Bundle()
                bundle.putSerializable("beer_object", beer)
                bundle.putByteArray("bitmap", byteArray)
                postDialog.arguments = bundle
                postDialog.show(requireActivity().supportFragmentManager, "PostTag")
            }

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

    fun bitmapToBase64(bitmap: Bitmap, format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG, quality: Int = 100): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(format, quality, byteArrayOutputStream) // Compress the bitmap
        val byteArray = byteArrayOutputStream.toByteArray() // Convert to ByteArray
        return Base64.encodeToString(byteArray, Base64.DEFAULT) // Convert to Base64 string
    }
}