package com.tukorea.Fearow

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostAdapter(private val postList: List<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val currentPost = postList[position]

        holder.postTitleView.text = currentPost.title
        holder.postContentView.text = currentPost.content
        holder.postPriceView.text = "${currentPost.price} ${currentPost.currency}"

        val context = holder.postImageView.context
        val imageResId = when (currentPost.currency) {
            "USD" -> R.drawable.usd_image
            "VND" -> R.drawable.vnd_image
            "CNY" -> R.drawable.cny_image
            "JPY" -> R.drawable.jpy_image
            else -> R.drawable.placeholder_image
        }
        holder.postImageView.setImageResource(imageResId)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PostDetailActivity::class.java).apply {
                putExtra("title", currentPost.title)
                putExtra("content", currentPost.content)
                putExtra("price", currentPost.price)
                putExtra("currency", currentPost.currency)
                putExtra("imageUrl", currentPost.imageUrl) // 이미지 URL도 추가
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = postList.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val postTitleView: TextView = itemView.findViewById(R.id.postTitleView)
        val postContentView: TextView = itemView.findViewById(R.id.postContentView)
        val postPriceView: TextView = itemView.findViewById(R.id.postPriceView)
    }
}
