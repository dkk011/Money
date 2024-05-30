package com.tukorea.Fearow

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.data.geojson.GeoJsonLayer
import com.google.maps.android.data.geojson.GeoJsonPolygon

class NearMeFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private var googleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_near_me, container, false)
        mapView = rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // 지도의 초기 위치를 사용자 위치로 설정
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                PERMISSION_REQUEST_LOCATION
            )
            return
        }
        enableLocation()

        // 사용자의 현재 위치 가져오기
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(location.latitude, location.longitude)
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))

                // GeoJSON 파일 로드 및 사용자 위치가 포함된 행정구역 식별
                try {
                    val layer = GeoJsonLayer(googleMap, R.raw.siheung_boundary, context)
                    for (feature in layer.features) {
                        val geometry = feature.geometry
                        if (geometry is GeoJsonPolygon) {
                            if (isLocationInPolygon(userLatLng, geometry.coordinates)) {
                                val singleFeatureLayer = GeoJsonLayer(googleMap, feature)
                                singleFeatureLayer.addLayerToMap()
                                break
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun enableLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    enableLocation()
                }
                return
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val PERMISSION_REQUEST_LOCATION = 100
    }

    private fun isLocationInPolygon(location: LatLng, polygon: List<List<LatLng>>): Boolean {
        for (ring in polygon) {
            if (isPointInPolygon(location, ring)) {
                return true
            }
        }
        return false
    }

    private fun isPointInPolygon(point: LatLng, polygon: List<LatLng>): Boolean {
        var intersectCount = 0
        for (j in 0 until polygon.size - 1) {
            if (rayCastIntersect(point, polygon[j], polygon[j + 1])) {
                intersectCount++
            }
        }
        return (intersectCount % 2 == 1) // odd = inside, even = outside
    }

    private fun rayCastIntersect(point: LatLng, vertex1: LatLng, vertex2: LatLng): Boolean {
        val aY = vertex1.latitude
        val bY = vertex2.latitude
        val aX = vertex1.longitude
        val bX = vertex2.longitude
        val pY = point.latitude
        val pX = point.longitude

        if ((aY > pY && bY > pY) || (aY < pY && bY < pY) || (aX < pX && bX < pX)) {
            return false
        }
        val m = (aY - bY) / (aX - bX)
        val bee = -aX * m + aY
        val x = (pY - bee) / m

        return x > pX
    }
}
