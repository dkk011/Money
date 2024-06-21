package com.tukorea.Fearow

import android.database.DataSetObserver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class ReceivedApplicationsFragment : Fragment() {

    private lateinit var applications: MutableList<ApplicationItem>
    private lateinit var textViewAccepted: TextView
    private lateinit var textViewRejected: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_received_applications, container, false)

        applications = mutableListOf(
            ApplicationItem("1", "post1", "B", "A", "Application 1", ApplicationStatus.PENDING),
            ApplicationItem("2", "post2", "B", "A", "Application 2", ApplicationStatus.PENDING),
            ApplicationItem("3", "post3", "B", "A", "Application 3", ApplicationStatus.PENDING)
        )

        val listView = view.findViewById<ListView>(R.id.listViewReceivedApplications)
        val adapter = ReceivedApplicationsAdapter(requireContext(), applications)
        listView.adapter = adapter

        textViewAccepted = view.findViewById(R.id.textViewAcceptedApplications)
        textViewRejected = view.findViewById(R.id.textViewRejectedApplications)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ListView 어댑터의 데이터가 변경될 때마다 호출되는 리스너를 추가하여 상태를 업데이트합니다.
        val listView = view.findViewById<ListView>(R.id.listViewReceivedApplications)
        (listView.adapter as ReceivedApplicationsAdapter).registerDataSetObserver(object : DataSetObserver() {
            override fun onChanged() {
                updateStatusLists()
            }
        })
    }

    private fun updateStatusLists() {
        val acceptedApplications = applications.filter { it.status == ApplicationStatus.ACCEPTED }
        val rejectedApplications = applications.filter { it.status == ApplicationStatus.REJECTED }

        val acceptedText = "Accepted Applications: ${acceptedApplications.size}\n" +
                acceptedApplications.joinToString("\n") { it.application }
        val rejectedText = "Rejected Applications: ${rejectedApplications.size}\n" +
                rejectedApplications.joinToString("\n") { it.application }

        textViewAccepted.text = acceptedText
        textViewRejected.text = rejectedText
    }
}
