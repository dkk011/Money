package com.tukorea.Fearow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment

class MyApplicationStatusFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_application_status, container, false)

        val listView = view.findViewById<ListView>(R.id.myApplicationStatusListView)

        // SharedPreferences에서 거래 신청 내역을 불러옴
        val sharedPref = requireActivity().getSharedPreferences("ApplicationsPref", Context.MODE_PRIVATE)
        val applications = sharedPref.getStringSet("applications", mutableSetOf())?.toMutableList()

        // ArrayAdapter를 사용하여 리스트뷰에 거래 신청 내역 출력
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, applications ?: listOf())
        listView.adapter = adapter

        // 항목 클릭 시 거래 신청 취소
        listView.setOnItemClickListener { parent, view, position, id ->
            val application = applications?.get(position)
            applications?.remove(application)

            // SharedPreferences에서 해당 항목 삭제
            val editor = sharedPref.edit()
            editor.putStringSet("applications", applications?.toMutableSet())
            editor.apply()

            adapter.notifyDataSetChanged()
            Toast.makeText(requireContext(), "거래 신청이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
