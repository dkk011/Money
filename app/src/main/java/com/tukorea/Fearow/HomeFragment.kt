package com.tukorea.Fearow

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User

class HomeFragment : Fragment() {
    private lateinit var titleInput: EditText
    private lateinit var priceInput: EditText
    private lateinit var currencySpinner: Spinner
    private lateinit var contentInput: EditText
    private lateinit var buttonSubmit: Button
    public var email: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize Firebase
        FirebaseApp.initializeApp(requireContext())
        val database = FirebaseDatabase.getInstance()

        titleInput = view.findViewById(R.id.postTitleInput)
        priceInput = view.findViewById(R.id.priceInput)
        currencySpinner = view.findViewById(R.id.currencySpinner)
        contentInput = view.findViewById(R.id.contentInput)
        buttonSubmit = view.findViewById(R.id.commit)

        // Kakao 사용자 정보 요청
        UserApiClient.instance.me { user: User?, error ->
            if (error != null) {
                Log.e("HomeFragment", "사용자 정보 요청 실패", error)
                Toast.makeText(requireContext(), "사용자 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
            } else if (user != null) {
                email = user.kakaoAccount?.email
            }
        }

        buttonSubmit.setOnClickListener {
            val titleText = titleInput.text.toString()
            val priceText = priceInput.text.toString()
            val currencyText = currencySpinner.selectedItem.toString()
            val contentText = contentInput.text.toString()

            if (titleText.isEmpty() || priceText.isEmpty() || contentText.isEmpty()) {
                Toast.makeText(requireContext(), "모든 필드를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email == null) {
                Toast.makeText(requireContext(), "이메일 정보를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val priceValue = priceText.toInt()
                val maxPostIdRef = database.getReference("maxPostId")

                maxPostIdRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        var postId = dataSnapshot.getValue(Int::class.java) ?: 0
                        postId += 1

                        // 선택한 통화에 따라 이미지 설정
                        val currencyImageResId = when (currencyText) {
                            "USD" -> R.drawable.usd_image
                            "VND" -> R.drawable.vnd_image
                            "CNY" -> R.drawable.cny_image
                            "JPY" -> R.drawable.jpy_image
                            else -> R.drawable.placeholder_image
                        }

                        // 게시글 정보 저장
                        val postRef = database.getReference("posts")
                        val post = Post(
                            postId = postId,
                            userId = email ?: "unknown", // 이메일 정보를 사용
                            title = titleText,
                            content = contentText,
                            price = priceValue,
                            currency = currencyText,
                            imageUrl = currencyImageResId.toString()
                        )
                        postRef.child(postId.toString()).setValue(post)
                        maxPostIdRef.setValue(postId)
                        Toast.makeText(requireContext(), "게시글이 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, BoardFragment())
                            .addToBackStack(null)
                            .commit()
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        Toast.makeText(requireContext(), "maxPostId 읽기 실패", Toast.LENGTH_SHORT).show()
                    }
                })
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "가격에 유효한 숫자를 입력해주세요", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
