package com.tukorea.Fearow

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LocationAdapter(
    private val locationList: List<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_location, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locationList[position]
        holder.bind(location)
    }

    override fun getItemCount() = locationList.size

    inner class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationName: TextView = itemView.findViewById(R.id.locationName)

        fun bind(location: String) {
            locationName.text = location
            itemView.setOnClickListener {
                onItemClick(location)
            }
        }
    }
}
