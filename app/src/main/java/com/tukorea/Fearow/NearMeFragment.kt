package com.tukorea.Fearow

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.maps.android.data.geojson.GeoJsonFeature
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonMultiPolygon
import com.google.maps.android.data.geojson.GeoJsonPolygon
import com.google.maps.android.data.geojson.GeoJsonPolygonStyle
import java.io.IOException

class NearMeFragment : Fragment(), OnMapReadyCallback {

    private lateinit var infoIcon: ImageView
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: MapView
    private lateinit var buttons: List<Button>
    private lateinit var infoTextView: TextView
    private lateinit var selectedLocations: MutableList<Pair<String, Coordinates>>
    private lateinit var featureMap: MutableMap<String, GeoJsonFeature>
    private val addedPolygons = mutableListOf<Polygon>() // 추가된 위치의 폴리곤을 관리하기 위한 리스트

    private var mapReady = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_near_me, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()

        infoTextView = view.findViewById(R.id.textViewTitle)
        infoIcon = view.findViewById(R.id.infoIcon)

        infoIcon.setOnClickListener {
            showInfoDialog()
        }

        buttons = listOf(
            view.findViewById(R.id.button1),
            view.findViewById(R.id.button2),
            view.findViewById(R.id.button3),
            view.findViewById(R.id.button4)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener { onButtonClicked(index) }
        }

        arguments?.let {
            selectedLocations = it.getSerializable("selectedLocations") as MutableList<Pair<String, Coordinates>>? ?: mutableListOf()
        } ?: run {
            selectedLocations = mutableListOf()
        }

        // 초기값으로 리스트 채우기
        while (selectedLocations.size < 4) {
            selectedLocations.add(Pair("", Coordinates(0.0, 0.0, "")))
        }

        updateButtons()

        mapView.getMapAsync(this)

        return view
    }

    private fun showInfoDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_info, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<ImageView>(R.id.closeButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mapReady = true

        try {
            val layer = GeoJsonLayer(mMap, R.raw.siheung_boundary, requireContext())
            addGeoJsonLayerToMap(layer)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        drawPolygonsForSavedLocations()

        mMap.setOnPolygonClickListener { polygon ->
            val feature = featureMap[polygon.id]
            feature?.let {
                val placeName = it.getProperty("adm_nm")
                infoTextView.text = placeName
            }
        }
    }

    private fun addGeoJsonLayerToMap(layer: GeoJsonLayer) {
        featureMap = mutableMapOf()

        for (feature in layer.features) {
            val polygonOptions = createPolygonOptions(feature)
            val polygon = mMap.addPolygon(polygonOptions)
            featureMap[polygon.id] = feature
        }

        setLayerClickListener(layer)
        layer.addLayerToMap()
    }

    private fun createPolygonOptions(feature: GeoJsonFeature): PolygonOptions {
        val geometry = feature.geometry
        val style = GeoJsonPolygonStyle().apply {
            fillColor = -0x7f330100
            strokeColor = 0x2fFF2A2A
            strokeWidth = 5f
        }

        val polygonOptions = PolygonOptions()

        when (geometry) {
            is GeoJsonPolygon -> {
                geometry.outerBoundaryCoordinates?.forEach { latLng ->
                    polygonOptions.add(latLng)
                }
            }
            is GeoJsonMultiPolygon -> {
                geometry.polygons.forEach { polygon ->
                    polygon.outerBoundaryCoordinates?.forEach { latLng ->
                        polygonOptions.add(latLng)
                    }
                }
            }
        }

        polygonOptions.strokeColor(style.strokeColor)
        polygonOptions.strokeWidth(style.strokeWidth)
        polygonOptions.fillColor(style.fillColor)

        return polygonOptions
    }

    private fun setLayerClickListener(layer: GeoJsonLayer) {
        layer.setOnFeatureClickListener { feature ->
            val placeName = feature.getProperty("adm_nm")
            infoTextView.text = placeName

            when (val geometry = feature.geometry) {
                is GeoJsonPolygon -> {
                    val center = getPolygonCenter(geometry.outerBoundaryCoordinates)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 12f))
                }
                is GeoJsonMultiPolygon -> {
                    // Handle multi-polygon click if needed
                }
            }
        }
    }

    private fun drawPolygonsForSavedLocations() {
        if (!mapReady) return

        selectedLocations.forEach { location ->
            if (location.first.isNotEmpty()) {
                drawPolygonForLocation(location.first)
            }
        }
    }

    private fun drawPolygonForLocation(locationName: String) {
        if (!mapReady) return

        try {
            val layer = GeoJsonLayer(mMap, R.raw.siheung_boundary, requireContext())

            for (feature in layer.features) {
                if (feature.getProperty("adm_nm") == locationName) {
                    val polygonOptions = createPolygonOptions(feature)

                    if (selectedLocations.any { it.first == locationName }) {
                        polygonOptions.fillColor(0x4fFF2A2A) // 선택된 위치의 폴리곤 색상
                    }

                    val polygon = mMap.addPolygon(polygonOptions)
                    addedPolygons.add(polygon) // 추가된 폴리곤 리스트에 추가

                    val bounds = getPolygonBounds(polygonOptions.points)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPolygonCenter(points: List<LatLng>?): LatLng {
        var latSum = 0.0
        var lngSum = 0.0

        points?.forEach { latLng ->
            latSum += latLng.latitude
            lngSum += latLng.longitude
        }

        val pointCount = points?.size ?: 1
        return LatLng(latSum / pointCount, lngSum / pointCount)
    }

    private fun getPolygonBounds(points: List<LatLng>?): LatLngBounds {
        val builder = LatLngBounds.builder()

        points?.forEach { latLng ->
            builder.include(latLng)
        }

        return builder.build()
    }

    private fun updateButtons() {
        buttons.forEachIndexed { index, button ->
            val locationName = selectedLocations[index].first
            val displayedText = locationName.substringAfter("시 ")

            if (displayedText.isNotEmpty()) {
                button.text = displayedText
            } else {
                button.text = "+"
            }
        }
    }

    private fun onButtonClicked(index: Int) {
        if (selectedLocations[index].first.isNotEmpty()) {
            // 위치 삭제
            selectedLocations[index] = Pair("", Coordinates(0.0, 0.0, ""))

            // SharedPreferences에서도 삭제
            val sharedPref = requireContext().getSharedPreferences("SelectedLocationPref", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                remove("selectedLocation_$index")
                apply()
            }
            // 추가된 폴리곤만 지우기
            removeAddedPolygons()
            drawPolygonsForSavedLocations()

            // 버튼 업데이트
            updateButtons()
        } else {
            // 위치 추가
            val intent = Intent(requireContext(), LocationActivity::class.java)
            startActivityForResult(intent, index)
        }
    }


    private fun removeAddedPolygons() {
        addedPolygons.forEach { it.remove() }
        addedPolygons.clear()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val locationName = data.getStringExtra("selectedLocation") ?: return

            val sharedPref = requireContext().getSharedPreferences("CenterPointPref", Context.MODE_PRIVATE)
            val latitude = sharedPref.getFloat("${locationName}_latitude", 0.0f).toDouble()
            val longitude = sharedPref.getFloat("${locationName}_longitude", 0.0f).toDouble()
            val coordinates = Coordinates(latitude, longitude, locationName)

            if (requestCode < selectedLocations.size) {
                selectedLocations[requestCode] = Pair(locationName, coordinates)
            }

            removeAddedPolygons() // 추가된 폴리곤만 지우기
            drawPolygonsForSavedLocations()
            updateButtons()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(selectedLocations: ArrayList<Pair<String, Coordinates>>): NearMeFragment {
            val fragment = NearMeFragment()
            val args = Bundle()
            args.putSerializable("selectedLocations", selectedLocations)
            fragment.arguments = args
            return fragment
        }
    }
}
