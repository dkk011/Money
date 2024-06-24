package com.tukorea.Fearow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class MyApplicationsStatusAdapter(
    private val context: Context,
    private var applications: List<String>
) : BaseAdapter() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int = applications.size

    override fun getItem(position: Int): Any = applications[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.list_item_application, parent, false)
            holder = ViewHolder()
            holder.title = view.findViewById(R.id.applicationTitle)
            holder.image = view.findViewById(R.id.applicationImage)
            holder.cancelButton = view.findViewById(R.id.cancelButton)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val application = applications[position]
        val parts = application.split(" - ")
        if (parts.size >= 4) {
            val title = parts[0]
            val imageResId = parts[2].toIntOrNull() ?: R.drawable.placeholder_image

            holder.title?.text = title
            holder.image?.setImageResource(imageResId)
        }

        // 취소 버튼 클릭 시 거래 신청 취소
        holder.cancelButton?.setOnClickListener {
            val mutableApplications = applications.toMutableList()
            mutableApplications.removeAt(position)
            applications = mutableApplications

            notifyDataSetChanged()

            // SharedPreferences에서 해당 항목 삭제
            val sharedPref = context.getSharedPreferences("ApplicationsPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putStringSet("applications", applications.toMutableSet())
            editor.apply()

            Toast.makeText(context, "거래 신청이 취소되었습니다.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private class ViewHolder {
        var title: TextView? = null
        var image: ImageView? = null
        var cancelButton: Button? = null
    }
}
