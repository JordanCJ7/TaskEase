package com.chirathi.taskease

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class launchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, Onboarding1Activity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}