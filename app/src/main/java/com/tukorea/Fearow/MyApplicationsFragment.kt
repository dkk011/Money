package com.tukorea.Fearow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class MyApplicationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_applications, container, false)

        val listView = view.findViewById<ListView>(R.id.myApplicationsListView)

        // SharedPreferences에서 거래 신청 내역을 불러옴
        val sharedPref = requireActivity().getSharedPreferences("ApplicationsPref", Context.MODE_PRIVATE)
        val applications = sharedPref.getStringSet("applications", mutableSetOf())?.toMutableList()

        // MyApplicationsStatusAdapter를 사용하여 리스트뷰에 거래 신청 내역 출력
        val adapter = MyApplicationsStatusAdapter(requireContext(), applications ?: listOf())
        listView.adapter = adapter

        return view
    }
}
