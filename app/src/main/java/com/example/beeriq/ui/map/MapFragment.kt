package com.example.beeriq.ui.map

import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.beeriq.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.beeriq.data.local.breweryDatabase.Brewery
import com.example.beeriq.data.local.breweryDatabase.BreweryDatabase
import com.example.beeriq.data.local.breweryDatabase.BreweryDatabaseDao
import com.example.beeriq.data.local.breweryDatabase.BreweryRepository
import com.example.beeriq.data.local.breweryDatabase.BreweryViewModel
import com.example.beeriq.data.local.breweryDatabase.BreweryViewModelFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions: MarkerOptions
    private val markersMap = mutableMapOf<String, Marker>()

    private lateinit var database: BreweryDatabase
    private lateinit var dao: BreweryDatabaseDao
    private lateinit var repository: BreweryRepository
    private lateinit var factory: BreweryViewModelFactory
    private lateinit var viewModel: BreweryViewModel

    private lateinit var breweries: List<Brewery>

    private val PERMISSION_REQUEST_CODE = 0
    private var mapCentered = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database = BreweryDatabase.getInstance(requireContext())
        dao = database.breweryDatabaseDao
        repository = BreweryRepository(database.breweryDatabaseDao)
        factory = BreweryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(BreweryViewModel::class.java)

        viewModel.allBreweries.observe(requireActivity(), Observer { it ->
            breweries = it
            Log.d("DEBUG: Number of breweries", (it.size).toString())

            val breweryNames = breweries.map { it.name }
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, breweryNames)
            val searchView = view.findViewById<SearchView>(R.id.searchView)
            val searchAutoComplete = searchView.findViewById<androidx.appcompat.widget.SearchView.SearchAutoComplete>(androidx.appcompat.R.id.search_src_text)
            searchAutoComplete.setAdapter(adapter)

            // Handle search query text submission
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { searchForBrewery(it) }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return false
                }
            })

            // Set the suggestion adapter
            searchAutoComplete.setAdapter(adapter)

            // Set a listener for autocomplete selection
            searchAutoComplete.setOnItemClickListener { parent, _, position, _ ->
                val selectedBreweryName = parent.getItemAtPosition(position) as String
                searchForBrewery(selectedBreweryName)
                searchView.setQuery(selectedBreweryName, false)  // Update the search query with the selected item
            }

            generateMarkers()
        })

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        markerOptions = MarkerOptions()

        checkPermission()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (this::locationManager.isInitialized) {
            locationManager.removeUpdates(this)
        }
    }

    private fun initLocationManager() {
        try {
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(requireContext(), "Enable GPS to show location", Toast.LENGTH_SHORT).show()
                return
            }

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                onLocationChanged(location)
            }

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
        }
        catch (e: SecurityException) {
            Log.e("SecurityException", "", e)
        }
    }

    override fun onLocationChanged(location: Location) {
        println("DEBUG: onLocationChanged(): (${location.latitude}, ${location.longitude})")
        val lat = location.latitude
        val lng = location.longitude
        val latLng = LatLng(lat, lng)
        if (!mapCentered) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13f)
            mMap.animateCamera(cameraUpdate)
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("You are here!")
            )
            mapCentered = true
        }
    }

    private fun generateMarkers() {
        for (brewery in breweries) {
            val position = LatLng(brewery.latitude, brewery.longitude)
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(brewery.name)
                    .snippet(brewery.address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )
            // Store marker by name so it can be indexed by the search bar
            marker?.let {
                markersMap[brewery.name] = it
            }
        }

        mMap.setOnMarkerClickListener { clickedMarker ->
            val clickedBrewery = breweries.find { it.name.equals(clickedMarker.title, ignoreCase = true) }
            clickedBrewery?.let {
                val intent = Intent(requireContext(), BreweryDetails::class.java).apply {
                    putExtra("brewery_name", it.name)
                    putExtra("brewery_address", it.address)
                    putExtra("brewery_description_title", it.descriptionTitle)
                    putExtra("brewery_description_body", it.descriptionBody)
                }
                startActivity(intent)
            }
            false // Return false to allow the info window to appear
        }
    }

    private fun searchForBrewery(query: String) {
        // Find matching brewery by name
        val brewery = breweries.find { it.name.contains(query, ignoreCase = true) }

        // If a match is found, center the map on the brewery's marker
        brewery?.let {
            val position = LatLng(it.latitude, it.longitude)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
            markersMap[it.name]?.showInfoWindow()
        } ?: run {
            // If no match, show a message or clear existing markers
            Toast.makeText(requireContext(), "Brewery not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        } else {
            initLocationManager()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initLocationManager()
            }
        }
    }
}