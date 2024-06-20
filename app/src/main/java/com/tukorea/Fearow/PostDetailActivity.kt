package com.tukorea.Fearow

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso

class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_detail)

        val imageView = findViewById<ImageView>(R.id.detailImageView)
        val titleView = findViewById<TextView>(R.id.detailTitleView)
        val contentView = findViewById<TextView>(R.id.detailContentView)
        val priceView = findViewById<TextView>(R.id.detailPriceView)
        val requestTradeButton = findViewById<Button>(R.id.requestTradeButton)

        val imageUrl = intent.getStringExtra("imageUrl")
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val price = intent.getIntExtra("price", 0)

        if (!imageUrl.isNullOrEmpty()) {
            Picasso.get().load(imageUrl).into(imageView)
        } else {
            imageView.setImageResource(R.drawable.placeholder_image)
        }
        titleView.text = title
        contentView.text = content
        priceView.text = "$price 원"

        requestTradeButton.setOnClickListener {
            Toast.makeText(this, "거래 신청이 완료되었습니다.", Toast.LENGTH_SHORT).show()
            // 거래 신청 로직 추가 가능
        }
    }
}
