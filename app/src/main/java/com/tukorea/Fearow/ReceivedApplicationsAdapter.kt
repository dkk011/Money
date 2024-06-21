package com.tukorea.Fearow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

class ReceivedApplicationsAdapter(
    context: Context,
    private val applications: MutableList<ApplicationItem>
) : ArrayAdapter<ApplicationItem>(context, 0, applications) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_received_application, parent, false)

        val applicationItem = applications[position]
        val textView = view.findViewById<TextView>(R.id.textViewApplication)
        val buttonAccept = view.findViewById<Button>(R.id.buttonAccept)
        val buttonReject = view.findViewById<Button>(R.id.buttonReject)

        textView.text = applicationItem.application
        buttonAccept.setOnClickListener {
            applicationItem.status = ApplicationStatus.ACCEPTED
            notifyDataSetChanged() // 데이터 변경을 UI에 반영
        }
        buttonReject.setOnClickListener {
            applicationItem.status = ApplicationStatus.REJECTED
            notifyDataSetChanged() // 데이터 변경을 UI에 반영
        }

        return view
    }
}
