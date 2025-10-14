package com.example.love

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Находим кнопку по ID
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            startActivity(Intent(this, DatePickerActivity::class.java))
        }
    }
}