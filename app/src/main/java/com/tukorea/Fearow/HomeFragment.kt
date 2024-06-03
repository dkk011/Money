package com.tukorea.Fearow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private lateinit var selectedLocation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selectedLocation = it.getString(ARG_LOCATION) ?: "Unknown"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val locationTextView: TextView = view.findViewById(R.id.locationTextView)
        locationTextView.text = "Selected Location: $selectedLocation"
        return view
    }

    companion object {
        private const val ARG_LOCATION = "selectedLocation"

        fun newInstance(location: String) = HomeFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_LOCATION, location)
            }
        }
    }
}
