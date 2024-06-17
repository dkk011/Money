package com.tukorea.Fearow

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter
    private lateinit var locationList: ArrayList<Pair<String, Pair<Double, Double>>> // Pair<위치 이름, Pair<위도, 경도>>
    private lateinit var updatedLocationList: List<Pair<String, Double>> // Pair<위치 이름, 거리>
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var searchBar: EditText
    private lateinit var updateLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        searchBar = findViewById(R.id.searchBar)
        recyclerView = findViewById(R.id.recyclerViewLocation)
        recyclerView.layoutManager = LinearLayoutManager(this)
        updateLocationButton = findViewById(R.id.updateLocationButton)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize adapter
        adapter = LocationAdapter(ArrayList()) { location ->
            onLocationSelected(location.first)
        }
        recyclerView.adapter = adapter

        // Check and request location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            getLastLocation()
        }

        // Load location data
        loadLocations()

        // Set up search bar listener
        searchBar.addTextChangedListener {
            val query = it.toString()
            filterLocations(query)
        }

        updateLocationButton.setOnClickListener{
            Toast.makeText(this, "위치 새로고침", Toast.LENGTH_SHORT).show()
            getLastLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    updateLocationList(it)
                } ?: run {
                    Log.d("LocationActivity", "현재 위치를 가져오지 못했습니다.")
                }
            }
        }
    }

    private fun loadLocations() {
        locationList = ArrayList()
        val sharedPref = getSharedPreferences("CenterPointPref", MODE_PRIVATE)
        val names = sharedPref.getStringSet("locationNames", setOf()) ?: setOf()
        for (name in names) {
            val latitude = sharedPref.getFloat("${name}_latitude", 0.0f).toDouble()
            val longitude = sharedPref.getFloat("${name}_longitude", 0.0f).toDouble()
            locationList.add(Pair(name, Pair(latitude, longitude)))
        }
        adapter.updateData(locationList.map { Pair(it.first, 0.0) })
    }

    private fun updateLocationList(userLocation: Location) {
        updatedLocationList = locationList.map { (name, coords) ->
            val centerPointLocation = Location("centerPoint").apply {
                latitude = coords.first
                longitude = coords.second
            }
            val distance = userLocation.distanceTo(centerPointLocation).toDouble()
            Pair(name, distance)
        }.sortedBy { it.second }

        adapter.updateData(updatedLocationList)
    }

    private fun filterLocations(query: String) {
        val filteredList = updatedLocationList.filter { it.first.contains(query, ignoreCase = true) }
        adapter.updateData(filteredList.map { Pair(it.first, 0.0) })
    }

    private fun onLocationSelected(location: String) {
        val sharedPref = getSharedPreferences("SelectedLocationPref", Context.MODE_PRIVATE)
        val selectedLocations = getSelectedLocations(sharedPref).toMutableList()
        selectedLocations.add(location)

        if (selectedLocations.size > 4) {
            selectedLocations.removeAt(0) // 가장 오래된 위치 제거
        }
        saveSelectedLocation(location)
        saveSelectedLocations(sharedPref, selectedLocations)

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("selectedLocation", location)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        finish()
    }

    private fun saveSelectedLocation(location: String) {
        val sharedPref = getSharedPreferences("SelectedLocationPref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("selectedLocation", location)
            apply()
        }
    }

    private fun getSelectedLocations(sharedPref: SharedPreferences): List<String> {
        val locations = mutableListOf<String>()
        for (i in 0 until 4) {
            val location = sharedPref.getString("selectedLocation_$i", null)
            location?.let { locations.add(it) }
        }
        return locations
    }

    private fun saveSelectedLocations(sharedPref: SharedPreferences, locations: List<String>) {
        with(sharedPref.edit()) {
            for (i in locations.indices) {
                putString("selectedLocation_$i", locations[i])
            }
            apply()
        }
    }
}
