package com.example.beeriq.data.local.beerDatabase

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.io.InputStreamReader

class ImportCsv : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        importCsv(applicationContext)
    }
    fun importCsv(context: Context) {

        val beerViewModel: BeerViewModel
        val database = BeerDatabase.getInstance(context)
        val repository = BeerRepository(database.beerDatabaseDao)
        val viewModelFactory = BeerViewModelFactory(repository)
        beerViewModel = ViewModelProvider(this, viewModelFactory).get(BeerViewModel::class.java)

        val inputStream = assets.open("beer_data.csv")
        val beerList = parseCsvFile(inputStream)
        var count = 1
        for (beer in beerList) {
            //Log.d("testing", "${count}, ${beer.name}")
            beerViewModel.insert(beer)
            count++
        }
    }

    fun parseCsvFile(inputStream: InputStream): List<Beer> {
        val beers = mutableListOf<Beer>()
        val csvParser = CSVParser(
            InputStreamReader(inputStream),
            CSVFormat.DEFAULT.withFirstRecordAsHeader())

        var count = 1
        for (record in csvParser) {
            try {
                val beer = Beer(
                    name = record.get("Name"),
                    style = record.get("Style"),
                    brewery = record.get("Brewery"),
                    beerFullName = record.get("Beer Name (Full)"),
                    description = record.get("Description"),
                    abv = record.get("ABV").toDouble(),
                    minIBU = record.get("Min IBU").toInt(),
                    maxIBU = record.get("Max IBU").toInt(),
                    astringency = record.get("Astringency").toInt(),
                    body = record.get("Body").toInt(),
                    alcohol = record.get("Alcohol").toInt(),
                    bitter = record.get("Bitter").toInt(),
                    sweet = record.get("Sweet").toInt(),
                    sour = record.get("Sour").toInt(),
                    salty = record.get("Salty").toInt(),
                    fruits = record.get("Fruits").toInt(),
                    hoppy = record.get("Hoppy").toInt(),
                    spices = record.get("Spices").toInt(),
                    malty = record.get("Malty").toInt(),
                    reviewAroma = record.get("review_aroma").toDouble(),
                    reviewAppearance = record.get("review_appearance").toDouble(),
                    reviewPalate = record.get("review_palate").toDouble(),
                    reviewTaste = record.get("review_taste").toDouble(),
                    reviewOverall = record.get("review_overall").toDouble(),
                    numOfReviews = record.get("number_of_reviews").toInt(),
                )
                //Log.d("testing", "$count, ${beer.name}, ${beer.malty}")
                count++
                beers.add(beer)
            } catch (e: Exception) {
                Log.e("CSVError", "Error parsing record at line $count", e)
            }
        }
        return beers
    }
}

//put these lines in mainActivity() to start import
//val intent = Intent(this, ImportCsv::class.java)
//this.startActivity(intent)