package com.tukorea.Fearow

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient
import com.tukorea.Fearow.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinner: Spinner = findViewById(R.id.spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.국적, // arrays.xml 파일에서 정의한 항목들
            android.R.layout.simple_spinner_item // 스피너에 표시할 레이아웃 스타일
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Log.e(TAG, "카카오 로그인 실패", error)
            } else if (token != null) {
                Log.i(TAG, "카카오 로그인 성공 ${token.accessToken}")
                startActivity(Intent(this, LocationActivity::class.java))
                finish()
            }
        }

        binding.btnLogin.setOnClickListener {
            //이전에 로그인 했던 토큰이 있는지 확인
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                //토큰이 없고 에러 발생 시 에러를 로그로 찍음
                if (error != null) {
                    Log.d("error", error.toString())

                    //토큰이 없다면 카카오톡이나 카카오계정으로 연동

                    if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                        UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)
                    } else {
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    }
                }
                //카카오 로그인을 통해 토큰 정보를 받아오면 다음 페이지로 이동
                //한번 로그인 하면 다음부터는 계속 바로 로그인
                else if (tokenInfo != null) {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("success", "kakao")
                    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                }
            }
        }
    }
}