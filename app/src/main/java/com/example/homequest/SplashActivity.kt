package com.example.homequest

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        enableEdgeToEdge()

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this@SplashActivity, WelcomeActivity::class.java))
            finish()
        }, 3000)
    }
}
