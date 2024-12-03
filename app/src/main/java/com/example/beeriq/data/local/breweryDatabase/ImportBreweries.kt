package com.example.beeriq.data.local.breweryDatabase

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.InputStream
import java.io.InputStreamReader

class ImportBreweries : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        importBreweries(applicationContext)
    }

    fun importBreweries(context: Context) {

        val breweryViewModel: BreweryViewModel
        val database = BreweryDatabase.getInstance(context)
        val repository = BreweryRepository(database.breweryDatabaseDao)
        val viewModelFactory = BreweryViewModelFactory(repository)
        breweryViewModel = ViewModelProvider(this, viewModelFactory).get(BreweryViewModel::class.java)

        val inputStream = assets.open("brewery_data.csv")
        val breweryList = parseCsvFile(inputStream)
        var count = 1
        for (brewery in breweryList) {
            breweryViewModel.insert(brewery)
            count++
        }
    }

    fun parseCsvFile(inputStream: InputStream): List<Brewery> {
        val breweries = mutableListOf<Brewery>()
        val csvParser = CSVParser(
            InputStreamReader(inputStream),
            CSVFormat.DEFAULT.withFirstRecordAsHeader())

        var count = 1
        for (record in csvParser) {
            try {
                val brewery = Brewery(
                    name = record.get("Name"),
                    address = record.get("Address"),
                    latitude = record.get("Latitude").toDouble(),
                    longitude = record.get("Longitude").toDouble(),
                    descriptionTitle = record.get("DescriptionTitle"),
                    descriptionBody = record.get("DescriptionBody")
                )
                count++
                breweries.add(brewery)
            } catch (e: Exception) {
                Log.e("CSVError", "Error parsing record at line $count", e)
            }
        }
        return breweries
    }
}