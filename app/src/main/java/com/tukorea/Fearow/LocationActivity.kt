package com.tukorea.Fearow

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter
    private lateinit var locationList: ArrayList<Pair<String, Pair<Double, Double>>> // Pair<위치 이름, Pair<위도, 경도>>
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        recyclerView = findViewById(R.id.recyclerViewLocation)
        recyclerView.layoutManager = LinearLayoutManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // 사용자 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        // 행정구역 이름과 중심 좌표 불러오기
        loadLocations()

        // 현재 위치 가져오기
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    updateLocationList(location)
                } else {
                    Log.d("LocationActivity", "현재 위치를 가져오지 못했습니다.")
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
        adapter = LocationAdapter(ArrayList()) { location ->
            onLocationSelected(location.first)
        }
        recyclerView.adapter = adapter
    }

    private fun updateLocationList(userLocation: Location) {
        val updatedLocationList = ArrayList<Pair<String, Double>>()
        for ((admNm, coords) in locationList) {
            val centerPointLocation = Location("centerPoint")
            centerPointLocation.latitude = coords.first
            centerPointLocation.longitude = coords.second

            val distance = userLocation.distanceTo(centerPointLocation)
            updatedLocationList.add(Pair(admNm, distance.toDouble()))
        }
        updatedLocationList.sortBy { it.second }

        adapter = LocationAdapter(updatedLocationList) { location ->
            onLocationSelected(location.first)
        }
        recyclerView.adapter = adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            updateLocationList(location)
                        }
                    }
            }
        }
    }

    private fun onLocationSelected(location: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedLocation", location)
        saveSelectedLocation(location) // 사용자가 선택한 행정구역 이름 저장
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
}
