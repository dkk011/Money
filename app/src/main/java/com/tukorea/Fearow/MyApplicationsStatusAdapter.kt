package com.tukorea.Fearow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MyApplicationStatusAdapter(
    context: Context,
    private val applications: List<ApplicationItem>
) : ArrayAdapter<ApplicationItem>(context, 0, applications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_my_application_status, parent, false)

        val applicationItem = applications[position]
        val textViewApplication = view.findViewById<TextView>(R.id.textViewMyApplication)
        val textViewStatus = view.findViewById<TextView>(R.id.textViewApplicationStatus)

        textViewApplication.text = applicationItem.application
        textViewStatus.text = when (applicationItem.status) {
            ApplicationStatus.PENDING -> "Pending"
            ApplicationStatus.ACCEPTED -> "Accepted"
            ApplicationStatus.REJECTED -> "Rejected"
        }

        return view
    }
}
