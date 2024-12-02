package com.example.beeriq.ui.userprofile.myBeers

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.beeriq.R
import com.google.android.material.slider.Slider

class BeerDetailsDialogFragment : DialogFragment() {

    private lateinit var beerName: String
    private lateinit var beerDescription: String
    private lateinit var ABV: String
    private lateinit var style: String
    private lateinit var IBU: String
    private var beerImageBitmap: Bitmap? = null
    private var reviewAroma: Double = 0.000
    private var reviewAppearance: Double = 0.000
    private var reviewPalate: Double = 0.000
    private var reviewTaste: Double = 0.000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
        arguments?.let {
            beerName = it.getString(ARG_NAME, "")
            beerDescription = it.getString(ARG_DESCRIPTION, "")
            ABV = "ABV: " + it.getDouble(ARG_ABV, 0.0).toString() + "%"
            style = it.getString(ARG_STYLE, "")
            beerImageBitmap = it.getParcelable(ARG_IMAGE_BITMAP)
            IBU = it.getInt(ARG_MIN_IBU, 0).toString() + "-" + it.getInt(ARG_MAX_IBU, 0).toString()
            reviewAroma = it.getDouble(ARG_AROMA, 0.000)
            reviewAppearance = it.getDouble(ARG_APEARANCE, 0.000)
            reviewPalate = it.getDouble(ARG_PALATE, 0.000)
            reviewTaste = it.getDouble(ARG_TASTE, 0.000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_beer_details, container, false)

        val nameTextView: TextView = view.findViewById(R.id.dialogBeerName)
        val descriptionTextView: TextView = view.findViewById(R.id.dialogBeerDescription)
        val imageView: ImageView = view.findViewById(R.id.dialogBeerImage)
        val ABVTextView: TextView = view.findViewById(R.id.abv)
        val IBUTextView: TextView = view.findViewById(R.id.ibu)
        val styleTextView: TextView = view.findViewById(R.id.style)
        val reviewAromaTextview: Slider = view.findViewById(R.id.aroma_slider)
        val reviewAppearanceTextview: Slider = view.findViewById(R.id.appearance_slider)
        val reviewPalateTextview: Slider = view.findViewById(R.id.palate_slider)
        val reviewTasteTextview: Slider = view.findViewById(R.id.taste_slider)

        // Set the details
        nameTextView.text = beerName
        descriptionTextView.text = beerDescription
        ABVTextView.text = ABV
        IBUTextView.text = IBU
        styleTextView.text = style
        reviewAromaTextview.value = reviewAroma.toFloat()
        reviewAppearanceTextview.value = reviewAppearance.toFloat()
        reviewPalateTextview.value = reviewPalate.toFloat()
        reviewTasteTextview.value = reviewTaste.toFloat()
        beerImageBitmap?.let {
            imageView.setImageBitmap(it)
        } ?: run {
            imageView.setImageResource(R.drawable.beer_icon) // Fallback image
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        // Get the current dialog window
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.95).toInt(), // 90% of screen width
            (resources.displayMetrics.heightPixels * 0.60).toInt() // Adjust height automatically
        )
    }

    companion object {
        private const val ARG_ABV = "abv"
        private const val ARG_DESCRIPTION = "description"
        private const val ARG_NAME = "name"
        private const val ARG_IMAGE_BITMAP = "imageBitmap"
        private const val ARG_STYLE = "style"
        private const val ARG_MIN_IBU = "min"
        private const val ARG_MAX_IBU = "max"
        private const val ARG_AROMA = "max"
        private const val ARG_APEARANCE = "max"
        private const val ARG_PALATE = "max"
        private const val ARG_TASTE = "max"

        fun newInstance(
            beerName: String,
            beerDescription: String,
            ABV: Double,
            style: String,
            minIBU: Int,
            maxIBU: Int,
            reviewAroma: Double,
            reviewAppearance: Double,
            reviewPalate: Double,
            reviewTaste: Double,
            beerImageBitmap: Bitmap?
        ): BeerDetailsDialogFragment {
            val fragment = BeerDetailsDialogFragment()
            val args = Bundle()
            args.putDouble(ARG_ABV, ABV)
            args.putString(ARG_NAME, beerName)
            args.putString(ARG_STYLE, style)
            args.putString(ARG_DESCRIPTION, beerDescription)
            args.putInt(ARG_MIN_IBU, minIBU)
            args.putInt(ARG_MAX_IBU, maxIBU)
            args.putDouble(ARG_AROMA, reviewAroma)
            args.putDouble(ARG_APEARANCE, reviewAppearance)
            args.putDouble(ARG_PALATE, reviewPalate)
            args.putDouble(ARG_TASTE, reviewTaste)
            args.putParcelable(ARG_IMAGE_BITMAP, beerImageBitmap) // Pass Bitmap as Parcelable
            fragment.arguments = args
            return fragment
        }
    }
}

