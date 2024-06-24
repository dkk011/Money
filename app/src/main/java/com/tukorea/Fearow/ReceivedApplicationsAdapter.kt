package com.tukorea.Fearow

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class ReceivedApplicationsAdapter(
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
            holder.imageView = view.findViewById(R.id.applicationImageView)
            holder.textView = view.findViewById(R.id.applicationTextView)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val applicationInfo = applications[position]
        val parts = applicationInfo.split(" - ")
        val title = parts[0]
        val price = parts[1]
        val currency = parts[2]
        val imageResId = parts[3].toInt()

        holder.textView?.text = "$title - $price $currency"
        holder.imageView?.setImageResource(imageResId)

        return view
    }

    private class ViewHolder {
        var imageView: ImageView? = null
        var textView: TextView? = null
    }
}
