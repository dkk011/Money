package com.tukorea.Fearow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.kakao.sdk.user.UserApiClient


class SettingsOptionsDialogFragment : DialogFragment() {

    private lateinit var logoutOption: TextView
    private lateinit var deleteAccountOption: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.settings_options_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logoutOption = view.findViewById(R.id.logout_option)
        deleteAccountOption = view.findViewById(R.id.delete_account_option)

        logoutOption.setOnClickListener {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "로그아웃 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "로그아웃 성공", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
            }
        }

        deleteAccountOption.setOnClickListener {
            UserApiClient.instance.unlink { error ->
                if (error != null) {
                    Toast.makeText(requireContext(), "회원 탈퇴 실패 $error", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "회원 탈퇴 성공", Toast.LENGTH_SHORT).show()
                    navigateToLogin()
                }
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(requireContext(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        requireActivity().finish()
    }
}
