package com.tukorea.Fearow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class ReceivedApplicationsFragment : Fragment() {

    private lateinit var adapter: ReceivedApplicationsAdapter
    private lateinit var applications: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_received_applications, container, false)

        val listView = view.findViewById<ListView>(R.id.receivedApplicationsListView)

        // SharedPreferences에서 받은 신청 내역을 불러옴
        val sharedPref = requireActivity().getSharedPreferences("ReceivedApplicationsPref", Context.MODE_PRIVATE)
        applications = sharedPref.getStringSet("receivedApplications", mutableSetOf())?.toMutableList() ?: mutableListOf()

        // 어댑터 설정
        adapter = ReceivedApplicationsAdapter(requireContext(), applications)
        listView.adapter = adapter

        return view
    }
}
