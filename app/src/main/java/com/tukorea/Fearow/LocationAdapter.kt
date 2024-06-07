package com.tukorea.Fearow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(
    private val locationList: ArrayList<Pair<String, Double>>,
    private val onClick: (Pair<String, Double>) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val locationName: TextView = itemView.findViewById(R.id.locationName)

        fun bind(location: Pair<String, Double>) {
            locationName.text = location.first
            itemView.setOnClickListener { onClick(location) }
        }
    }

    fun updateData(newLocationList: List<Pair<String, Double>>) {
        locationList.clear()
        locationList.addAll(newLocationList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locationList[position])
    }

    override fun getItemCount(): Int {
        return locationList.size
    }
}
