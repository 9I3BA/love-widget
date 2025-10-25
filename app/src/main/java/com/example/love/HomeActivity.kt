package com.example.love

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*
import kotlin.math.abs

class HomeActivity : AppCompatActivity() {

    private lateinit var tvDaysTogether: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var btnNotes: ImageButton  // <-- изменено на ImageButton
    private lateinit var ivYou: ImageView
    private lateinit var ivPartner: ImageView
    private lateinit var tvName1: TextView
    private lateinit var tvName2: TextView
    private lateinit var ivWidgetBigPhoto: ImageView
    private lateinit var ivWidgetSmallPhoto1: ImageView
    private lateinit var ivWidgetSmallPhoto2: ImageView
    private lateinit var ivWidgetSmallPhoto3: ImageView

    companion object {
        private const val REQUEST_SETTINGS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Находим элементы
        tvDaysTogether = findViewById(R.id.tvDaysTogether)
        btnCalendar = findViewById(R.id.btnCalendar)
        btnSettings = findViewById(R.id.btnSettings)
        btnNotes = findViewById(R.id.btnNotes)  // <-- исправлено
        ivYou = findViewById(R.id.ivYou)
        ivPartner = findViewById(R.id.ivPartner)
        tvName1 = findViewById(R.id.tvName1)
        tvName2 = findViewById(R.id.tvName2)
        ivWidgetBigPhoto = findViewById(R.id.ivWidgetBigPhoto)
        ivWidgetSmallPhoto1 = findViewById(R.id.ivWidgetSmallPhoto1)
        ivWidgetSmallPhoto2 = findViewById(R.id.ivWidgetSmallPhoto2)
        ivWidgetSmallPhoto3 = findViewById(R.id.ivWidgetSmallPhoto3)

        loadProfileData()
        calculateDaysTogether()

        btnCalendar.setOnClickListener {
            startActivity(Intent(this, AnniversaryActivity::class.java))
        }

        btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivityForResult(intent, REQUEST_SETTINGS)
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK) {
            loadProfileData()
            calculateDaysTogether()
        }
    }

    private fun loadProfileData() {
        val prefs = getSharedPreferences("LoveWidget", MODE_PRIVATE)

        val name1 = prefs.getString("name1", "Партнёр 1")
        val name2 = prefs.getString("name2", "Партнёр 2")
        tvName1.text = name1
        tvName2.text = name2

        // Аватары
        loadAvatar(prefs.getString("avatar1", null), ivYou)
        loadAvatar(prefs.getString("avatar2", null), ivPartner)

        // Фото виджета
        loadPhoto(prefs.getString("widgetBigPhoto", null), ivWidgetBigPhoto)
        loadPhoto(prefs.getString("widgetSmallPhoto1", null), ivWidgetSmallPhoto1)
        loadPhoto(prefs.getString("widgetSmallPhoto2", null), ivWidgetSmallPhoto2)
        loadPhoto(prefs.getString("widgetSmallPhoto3", null), ivWidgetSmallPhoto3)
    }

    private fun loadAvatar(path: String?, imageView: ImageView) {
        path?.let { p ->
            val file = File(p)
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file))
                return
            }
        }
        imageView.setImageResource(R.drawable.ic_person)
    }

    private fun loadPhoto(path: String?, imageView: ImageView) {
        path?.let { p ->
            val file = File(p)
            if (file.exists()) {
                imageView.setImageURI(Uri.fromFile(file))
                return
            }
        }
        imageView.setImageResource(R.drawable.pink_background)
    }

    private fun calculateDaysTogether() {
        val startDate = getSharedPreferences("LoveWidget", MODE_PRIVATE)
            .getLong("startDate", System.currentTimeMillis())

        val now = Calendar.getInstance()
        val start = Calendar.getInstance().apply { timeInMillis = startDate }

        val diff = abs(now.timeInMillis - start.timeInMillis)
        val days = diff / (1000 * 60 * 60 * 24)

        val years = days / 365
        val months = (days % 365) / 30
        val remainingDays = (days % 365) % 30

        tvDaysTogether.text = "$years года $months месяцев $remainingDays день"
    }
}