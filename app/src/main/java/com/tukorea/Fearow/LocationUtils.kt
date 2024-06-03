package com.tukorea.Fearow

import android.content.Context
import android.location.Location
import com.google.gson.Gson
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object LocationUtils {

    fun loadGeoJson(context: Context): FeatureCollection {
        val inputStream = context.resources.openRawResource(R.raw.siheung_boundary)
        val reader = InputStreamReader(inputStream, StandardCharsets.UTF_8)
        return Gson().fromJson(reader, FeatureCollection::class.java)
    }

    fun calculateDistance(context: Context, userLat: Double, userLng: Double): List<Pair<Properties, Double>> {
        val geoJson = loadGeoJson(context)
        val distances = mutableListOf<Pair<Properties, Double>>()
        geoJson.features.forEach { feature ->
            val coordinates = feature.geometry.coordinates.flatten().flatten()
            val centroid = coordinates.reduce { acc, list -> listOf(acc[0] + list[0], acc[1] + list[1]) }.map { it / coordinates.size }
            val distance = FloatArray(1)
            Location.distanceBetween(userLat, userLng, centroid[1], centroid[0], distance)
            distances.add(Pair(feature.properties, distance[0].toDouble()))
        }
        return distances.sortedBy { it.second }
    }
}

