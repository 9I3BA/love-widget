// MainActivity.kt (без изменений)
package com.example.love

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("LoveWidget", MODE_PRIVATE)
        val isFirstLaunch = preferences.getBoolean("isFirstLaunch", true)

        if (isFirstLaunch) {
            startActivity(Intent(this, WelcomeActivity::class.java))
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        finish() // завершаем MainActivity, чтобы нельзя было вернуться назад
    }
}