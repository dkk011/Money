package com.tukorea.Fearow

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONObject
import java.io.InputStream

class LocationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LocationAdapter
    private lateinit var locationList: ArrayList<String>
    private lateinit var geoJsonData: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        recyclerView = findViewById(R.id.recyclerViewLocation)
        recyclerView.layoutManager = LinearLayoutManager(this)

        geoJsonData = loadGeoJson()
        locationList = parseGeoJson(geoJsonData)
        adapter = LocationAdapter(locationList) { location ->
            onLocationSelected(location)
        }
        recyclerView.adapter = adapter
    }

    private fun loadGeoJson(): String {
        val inputStream: InputStream = resources.openRawResource(R.raw.siheung_boundary)
        return inputStream.bufferedReader().use { it.readText() }
    }

    private fun parseGeoJson(jsonData: String): ArrayList<String> {
        val locations = ArrayList<String>()
        val jsonObject = JSONObject(jsonData)
        val features = jsonObject.getJSONArray("features")
        for (i in 0 until features.length()) {
            val properties = features.getJSONObject(i).getJSONObject("properties")
            val admNm = properties.getString("adm_nm")
            locations.add(admNm)
        }
        return locations
    }

    private fun onLocationSelected(location: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("selectedLocation", location)
        startActivity(intent)
        finish()
    }
}
