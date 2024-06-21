package com.tukorea.Fearow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment

class MyApplicationStatusFragment : Fragment() {

    private lateinit var applications: List<ApplicationItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_application_status, container, false)

        // 예제 데이터
        applications = listOf(
            ApplicationItem("1", "post1", "B", "A", "Application 1", ApplicationStatus.ACCEPTED),
            ApplicationItem("2", "post2", "B", "A", "Application 2", ApplicationStatus.REJECTED),
            ApplicationItem("3", "post3", "B", "A", "Application 3", ApplicationStatus.PENDING)
        )

        val listView = view.findViewById<ListView>(R.id.listViewMyApplicationStatus)
        val adapter = MyApplicationStatusAdapter(requireContext(), applications)
        listView.adapter = adapter

        return view
    }
}
