package com.tukorea.Fearow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var titleInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var contentInput: EditText
    private lateinit var imageUrlInput: EditText
    private lateinit var buttonSubmit: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext())
        val database = FirebaseDatabase.getInstance()

        titleInput = view.findViewById(R.id.postTitleInput)
        priceInput = view.findViewById(R.id.priceInput)
        contentInput = view.findViewById(R.id.contentInput)
        imageUrlInput = view.findViewById(R.id.imageUrlInput)
        buttonSubmit = view.findViewById(R.id.commit)

        buttonSubmit.setOnClickListener {
            val titleText = titleInput.text.toString()
            val priceText = priceInput.text.toString()
            val contentText = contentInput.text.toString()
            val imageUrlText = imageUrlInput.text.toString()

            try {
                val priceValue = priceText.toInt()
                val maxPostIdRef = database.getReference("maxPostId")

                maxPostIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var postId = dataSnapshot.getValue(Int::class.java) ?: 0
                        postId += 1

                        // 게시글 정보 저장
                        val postRef = database.getReference("posts")
                        val post = Post(
                            postId = postId,
                            userId = "user@example.com", // 하드코딩된 유저 ID를 사용합니다.
                            title = titleText,
                            content = contentText,
                            price = priceValue,
                            imageUrl = imageUrlText
                        )
                        postRef.child(postId.toString()).setValue(post)
                        maxPostIdRef.setValue(postId)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), "Failed to read maxPostId", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Please enter a valid number for price", Toast.LENGTH_SHORT).show()
            }
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BoardFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
