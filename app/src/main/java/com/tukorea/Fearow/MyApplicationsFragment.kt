package com.tukorea.Fearow

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso

class MyApplicationsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_applications, container, false)
        val applicationsContainer = view.findViewById<LinearLayout>(R.id.applicationsContainer)

        val dbHelper = AppDatabaseHelper(activity as Context)
        val db = dbHelper.readableDatabase

        val cursor: Cursor = db.query(
            AppDatabaseHelper.TABLE_NAME,
            null, null, null, null, null, null
        )

        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_TITLE))
            val content = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_CONTENT))
            val imageUrl = cursor.getString(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_IMAGE_URL))
            val price = cursor.getInt(cursor.getColumnIndexOrThrow(AppDatabaseHelper.COLUMN_PRICE))

            val applicationView = LayoutInflater.from(context).inflate(R.layout.item_application, applicationsContainer, false)

            val imageView = applicationView.findViewById<ImageView>(R.id.applicationImageView)
            val titleView = applicationView.findViewById<TextView>(R.id.applicationTitleView)
            val contentView = applicationView.findViewById<TextView>(R.id.applicationContentView)
            val priceView = applicationView.findViewById<TextView>(R.id.applicationPriceView)

            titleView.text = title
            contentView.text = content
            priceView.text = getString(R.string.price_format, price)

            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(imageView)
            } else {
                imageView.setImageResource(R.drawable.placeholder_image)
            }

            applicationsContainer.addView(applicationView)
        }
        cursor.close()

        return view
    }
}