package com.tukorea.Fearow


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

class BoardFragment : Fragment() {
    private lateinit var buttonWritePost: Button
    private lateinit var postRecyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_board, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonWritePost = view.findViewById(R.id.buttonWritePost)
        postRecyclerView = view.findViewById(R.id.postRecyclerView)

        buttonWritePost.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .addToBackStack(null)
                .commit()
        }

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext())
        val database = FirebaseDatabase.getInstance()
        val postRef = database.getReference("posts")

        postRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        postAdapter = PostAdapter(mutableListOf())
        postRecyclerView.adapter = postAdapter

        postRef.orderByChild("postId").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val posts = mutableListOf<Post>()
                for (postSnapshot in dataSnapshot.children) {
                    val post = postSnapshot.getValue(Post::class.java)
                    post?.let { posts.add(it) }
                }
                posts.sortByDescending { it.postId }
                postAdapter.updatePosts(posts)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        })
    }

    inner class PostAdapter(private var posts: MutableList<Post>) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

        inner class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.postImageView)
            private val titleView: TextView = itemView.findViewById(R.id.postTitleView)
            private val contentView: TextView = itemView.findViewById(R.id.postContentView)
            private val priceView: TextView = itemView.findViewById(R.id.postPriceView)

            fun bind(post: Post) {
                val context = imageView.context
                val imageResId = when (post.currency) {
                    "USD" -> R.drawable.usd_image
                    "VND" -> R.drawable.vnd_image
                    "CNY" -> R.drawable.cny_image
                    "JPY" -> R.drawable.jpy_image
                    else -> R.drawable.placeholder_image
                }
                imageView.setImageResource(imageResId)

                titleView.text = post.title
                contentView.text = post.content
                priceView.text = "${post.price} ${post.currency}"

                itemView.setOnClickListener {
                    val intent = Intent(requireContext(), PostDetailActivity::class.java).apply {
                        putExtra("postId", post.postId)
                        putExtra("userId", post.userId)
                        putExtra("title", post.title)
                        putExtra("content", post.content)
                        putExtra("price", post.price)
                        putExtra("currency", post.currency)
                        putExtra("imageUrl", post.imageUrl)
                    }
                    startActivity(intent)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
            return PostViewHolder(view)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(posts[position])
        }

        override fun getItemCount(): Int = posts.size

        fun updatePosts(newPosts: List<Post>) {
            posts.clear()
            posts.addAll(newPosts)
            notifyDataSetChanged()
        }
    }
}
