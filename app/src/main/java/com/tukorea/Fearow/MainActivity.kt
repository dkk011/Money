package com.tukorea.Fearow

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.JsonParser
import com.tukorea.Fearow.databinding.ActivityMainBinding
import java.io.InputStreamReader

data class Coordinates(val latitude: Double, val longitude: Double, val locationName: String)

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var selectedLocation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val coordinates = extractCoordinatesFromJson()
        val centerPoints = calculateCenterPoints(coordinates)

        // SharedPreferences에서 저장된 선택된 위치 정보를 불러옴
        selectedLocation = getSavedSelectedLocation()

        // 중심점과 해당 행정구역 이름을 SharedPreferences에 저장
        saveCenterPoints(centerPoints)

        // 초기 프래그먼트 설정
        replaceFragment(HomeFragment.newInstance(selectedLocation))

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment.newInstance(selectedLocation))
                R.id.nav_near_me -> replaceFragment(NearMeFragment())
                R.id.nav_exchange_rate -> replaceFragment(ExchangeRateFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun extractCoordinatesFromJson(): List<Coordinates> {
        val inputStream = resources.openRawResource(R.raw.siheung_boundary)
        val jsonReader = InputStreamReader(inputStream)
        val jsonElement = JsonParser.parseReader(jsonReader)
        val jsonObject = jsonElement.asJsonObject
        val features = jsonObject.getAsJsonArray("features")

        val coordinatesList = mutableListOf<Coordinates>()
        for (feature in features) {
            val geometry = feature.asJsonObject.getAsJsonObject("geometry")
            val coordinates = geometry.getAsJsonArray("coordinates")

            val properties = feature.asJsonObject.getAsJsonObject("properties")
            val locationName = properties.get("adm_nm").asString

            coordinates.forEach {
                val multiPolygon = it.asJsonArray
                multiPolygon.forEach { polygon ->
                    val coordinatePairs = polygon.asJsonArray
                    coordinatePairs.forEach { pair ->
                        val longitude = pair.asJsonArray[0].asDouble
                        val latitude = pair.asJsonArray[1].asDouble
                        coordinatesList.add(Coordinates(latitude, longitude, locationName))
                    }
                }
            }
        }
        return coordinatesList
    }

    private fun calculateCenterPoints(coordinates: List<Coordinates>): Map<String, Coordinates> {
        val centerPoints = mutableMapOf<String, Coordinates>()
        val groupedCoordinates = coordinates.groupBy { it.locationName }

        for ((name, coords) in groupedCoordinates) {
            var sumLatitude = 0.0
            var sumLongitude = 0.0
            val totalPoints = coords.size

            for (coord in coords) {
                sumLatitude += coord.latitude
                sumLongitude += coord.longitude
            }

            centerPoints[name] = Coordinates(sumLatitude / totalPoints, sumLongitude / totalPoints, name)
        }

        return centerPoints
    }

    private fun saveCenterPoints(centerPoints: Map<String, Coordinates>) {
        val sharedPref = getSharedPreferences("CenterPointPref", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putStringSet("locationNames", centerPoints.keys)
            for ((name, coords) in centerPoints) {
                putFloat("${name}_latitude", coords.latitude.toFloat())
                putFloat("${name}_longitude", coords.longitude.toFloat())
            }
            apply()
        }
    }

    private fun getSavedSelectedLocation(): String {
        val sharedPref = getSharedPreferences("SelectedLocationPref", Context.MODE_PRIVATE)
        return sharedPref.getString("selectedLocation", "") ?: ""
    }
}
