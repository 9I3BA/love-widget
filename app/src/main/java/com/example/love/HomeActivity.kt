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
    private lateinit var btnCalendar: ImageView
    private lateinit var btnSettings: ImageView
    private lateinit var btnHome: ImageView
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
        btnHome = findViewById(R.id.btnHome)
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

        btnHome.setOnClickListener {
            // уже на главном экране
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_SETTINGS && resultCode == RESULT_OK) {
            // Обновляем все данные после возврата из настроек
            loadProfileData()
            calculateDaysTogether()
        }
    }

    private fun loadProfileData() {
        val prefs = getSharedPreferences("LoveWidget", MODE_PRIVATE)

        // Загружаем имена
        val name1 = prefs.getString("name1", "Партнёр 1")
        val name2 = prefs.getString("name2", "Партнёр 2")
        tvName1.text = name1
        tvName2.text = name2

        // Загружаем аватары
        val avatar1Path = prefs.getString("avatar1", null)
        val avatar2Path = prefs.getString("avatar2", null)

        avatar1Path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ivYou.setImageURI(Uri.fromFile(file))
            } else {
                ivYou.setImageResource(R.drawable.ic_person)
            }
        } ?: run {
            ivYou.setImageResource(R.drawable.ic_person)
        }

        avatar2Path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ivPartner.setImageURI(Uri.fromFile(file))
            } else {
                ivPartner.setImageResource(R.drawable.ic_person)
            }
        } ?: run {
            ivPartner.setImageResource(R.drawable.ic_person)
        }

        // Загружаем фото для виджета
        val bigPhotoPath = prefs.getString("widgetBigPhoto", null)
        val smallPhoto1Path = prefs.getString("widgetSmallPhoto1", null)
        val smallPhoto2Path = prefs.getString("widgetSmallPhoto2", null)
        val smallPhoto3Path = prefs.getString("widgetSmallPhoto3", null)

        bigPhotoPath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ivWidgetBigPhoto.setImageURI(Uri.fromFile(file))
            } else {
                ivWidgetBigPhoto.setImageResource(R.drawable.pink_background)
            }
        } ?: run {
            ivWidgetBigPhoto.setImageResource(R.drawable.pink_background)
        }

        smallPhoto1Path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ivWidgetSmallPhoto1.setImageURI(Uri.fromFile(file))
            } else {
                ivWidgetSmallPhoto1.setImageResource(R.drawable.pink_background)
            }
        } ?: run {
            ivWidgetSmallPhoto1.setImageResource(R.drawable.pink_background)
        }

        smallPhoto2Path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ivWidgetSmallPhoto2.setImageURI(Uri.fromFile(file))
            } else {
                ivWidgetSmallPhoto2.setImageResource(R.drawable.pink_background)
            }
        } ?: run {
            ivWidgetSmallPhoto2.setImageResource(R.drawable.pink_background)
        }

        smallPhoto3Path?.let { path ->
            val file = File(path)
            if (file.exists()) {
                ivWidgetSmallPhoto3.setImageURI(Uri.fromFile(file))
            } else {
                ivWidgetSmallPhoto3.setImageResource(R.drawable.pink_background)
            }
        } ?: run {
            ivWidgetSmallPhoto3.setImageResource(R.drawable.pink_background)
        }
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