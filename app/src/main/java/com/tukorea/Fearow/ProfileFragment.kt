package com.tukorea.Fearow

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.User
import com.tukorea.Fearow.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsIcon: ImageView
    private lateinit var nearMeSettingsText: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsIcon = view.findViewById(R.id.settings_icon)
        settingsIcon.setOnClickListener {
            showSettingsOptions()
        }

        nearMeSettingsText = view.findViewById(R.id.near_me_settings_text)
        nearMeSettingsText.setOnClickListener {
            navigateToNearMeFragment()
        }

        UserApiClient.instance.me { user: User?, error ->
            if (error != null) {
                Log.e("ProfileFragment", "사용자 정보 요청 실패", error)
                binding.profileInfo.text = "사용자 정보를 불러오지 못했습니다."
            } else if (user != null) {
                user.kakaoAccount?.profile?.profileImageUrl?.let { imageUrl ->
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .into(binding.profileImage)
                }

                binding.profileInfo.text = """
                    ${user.kakaoAccount?.profile?.nickname ?: ""}
                    ${user.kakaoAccount?.email ?: ""}
                """.trimIndent()
            }
        }
    }

    private fun showSettingsOptions() {
        val dialogFragment = SettingsOptionsDialogFragment()
        dialogFragment.show(parentFragmentManager, "SettingsOptionsDialog")
    }

    private fun navigateToNearMeFragment() {
        val nearMeFragment = NearMeFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, nearMeFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
