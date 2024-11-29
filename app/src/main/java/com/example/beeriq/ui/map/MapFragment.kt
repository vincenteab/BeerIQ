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
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
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
import com.google.android.gms.maps.model.LatLng

class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {
    private lateinit var mMap: GoogleMap
    private lateinit var locationManager: LocationManager
    private lateinit var markerOptions: MarkerOptions

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
                return
            }

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
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
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            mMap.animateCamera(cameraUpdate)
            markerOptions.position(latLng)
            mMap.addMarker(markerOptions)
            mapCentered = true
        }
    }

    private fun generateMarkers() {
        for (brewery in breweries) {
            val position = LatLng(brewery.latitude, brewery.longitude)
            val markerOptions = MarkerOptions()
                .position(position)
                .title(brewery.name)
                .snippet(brewery.address)

            mMap.addMarker(markerOptions)
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