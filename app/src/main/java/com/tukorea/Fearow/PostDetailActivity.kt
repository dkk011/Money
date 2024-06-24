package com.tukorea.Fearow

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val imageView = findViewById<ImageView>(R.id.detailImageView)
        val titleView = findViewById<TextView>(R.id.detailTitleView)
        val contentView = findViewById<TextView>(R.id.detailContentView)
        val priceView = findViewById<TextView>(R.id.detailPriceView)
        val requestTradeButton = findViewById<Button>(R.id.requestTradeButton)

        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val price = intent.getIntExtra("price", 0)
        val currency = intent.getStringExtra("currency")

        titleView.text = title
        contentView.text = content
        priceView.text = "$price $currency"

        var imageResId = R.drawable.placeholder_image
        when (currency) {
            "USD" -> imageResId = R.drawable.usd_image
            "VND" -> imageResId = R.drawable.vnd_image
            "CNY" -> imageResId = R.drawable.cny_image
            "JPY" -> imageResId = R.drawable.jpy_image
        }
        imageView.setImageResource(imageResId)

        requestTradeButton.setOnClickListener {
            Toast.makeText(this, "거래 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()

            // 거래 신청 내역을 SharedPreferences에 저장
            val sharedPref = getSharedPreferences("ApplicationsPref", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            val applications = sharedPref.getStringSet("applications", mutableSetOf())?.toMutableSet()
            val applicationInfo = "$title - $price $currency - $imageResId - pending"
            applications?.add(applicationInfo)
            editor.putStringSet("applications", applications)
            editor.apply()

            // 받은 신청 내역을 SharedPreferences에 저장
            val receivedSharedPref = getSharedPreferences("ReceivedApplicationsPref", Context.MODE_PRIVATE)
            val receivedEditor = receivedSharedPref.edit()
            val receivedApplications = receivedSharedPref.getStringSet("receivedApplications", mutableSetOf())?.toMutableSet()
            receivedApplications?.add(applicationInfo)
            receivedEditor.putStringSet("receivedApplications", receivedApplications)
            receivedEditor.apply()
        }
    }
}
