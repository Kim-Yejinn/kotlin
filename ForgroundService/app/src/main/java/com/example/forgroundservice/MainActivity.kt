package com.example.forgroundservice

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.forgroundservice.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 버튼
        binding.btnStart.setOnClickListener{
            val intent = Intent(this, Foreground::class.java)
            ContextCompat.startForegroundService(this, intent)
            Log.d("test", "서비스 시작")
        }

        binding.btnStop.setOnClickListener {
            val intent = Intent(this, Foreground::class.java)
            Log.d("test", "서비스 종료")
            stopService(intent)
        }
    }
}