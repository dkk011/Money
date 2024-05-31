package com.tukorea.Fearow

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.user.UserApiClient

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // 카카오 SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_app_key))

        // Handler를 사용하여 일정 시간 대기 후 다음 액티비티로 전환
        Handler(Looper.getMainLooper()).postDelayed({
            // 로그인 상태 확인
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                if (tokenInfo != null) {
                    // 토큰이 존재하면 MainActivity로 이동
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    // 토큰이 없거나 오류가 발생하면 LoginActivity로 이동
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish() // SplashActivity 종료
            }
        }, 1000) // 2000 milliseconds = 2 seconds
    }
}
