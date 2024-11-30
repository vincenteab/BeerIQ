package com.example.beeriq.tools

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import com.example.beeriq.ui.beerCategories.BeerCategory
import com.opencsv.CSVReader
import java.io.InputStreamReader

object Util {
    fun checkPermissions(activity: Context) {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                0
            )
        }
    }

    fun getBitmap(context: Context, imgUri: Uri): Bitmap {
        var bitmap = BitmapFactory.decodeStream(context.contentResolver.openInputStream(imgUri))
        val matrix = Matrix()
        matrix.setRotate(90f)
        var ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        return ret
    }

    fun parseBeerCategories(context: Context): List<BeerCategory> {
        val beerCategories = mutableListOf<BeerCategory>()

        try {
            val inputStream = context.assets.open("categorized_beers.csv") // Use the CSV file from assets
            val reader = CSVReader(InputStreamReader(inputStream))

            val categoryMap = mutableMapOf<String, MutableList<String>>()

            // Read all rows from the CSV file
            var line: Array<String>?
            while (reader.readNext().also { line = it } != null) {
                // Make sure you handle the correct indexes here (adjust based on your CSV structure)
                val beerName = line!![0].trim() // Beer Name (could have commas within it)
                val category = line!![1].trim() // Category

                // Add the beer to the appropriate category
                categoryMap.getOrPut(category) { mutableListOf() }.add(beerName)
            }

            // Convert map to list of BeerCategory objects
            for ((category, beers) in categoryMap) {
                beerCategories.add(BeerCategory(category, beers))
            }

        } catch (e: Exception) {
            Log.e("CSV", "Error reading CSV file", e)
        }

        return beerCategories
    }
}