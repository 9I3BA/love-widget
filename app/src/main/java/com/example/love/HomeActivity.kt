package com.example.love

import java.util.concurrent.TimeUnit
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.util.*
import kotlin.math.abs
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.work.*
import android.os.Build
class HomeActivity : AppCompatActivity() {

    private lateinit var tvDaysTogether: TextView
    private lateinit var btnCalendar: ImageButton
    private lateinit var btnSettings: ImageButton
    private lateinit var btnNotes: ImageButton
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

        tvDaysTogether = findViewById(R.id.tvDaysTogether)
        btnCalendar = findViewById(R.id.btnCalendar)
        btnSettings = findViewById(R.id.btnSettings)
        btnNotes = findViewById(R.id.btnNotes)
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

        // 🔔 —— НОВЫЕ СТРОКИ ——
        requestNotificationPermissionIfNeeded()
        scheduleDailyReminder()

        findViewById<ImageButton>(R.id.btnNotes).setOnClickListener {
            startActivity(Intent(this, NoteActivity::class.java))
        }

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
            // 🔁 Обновляем напоминание при изменении даты
            scheduleDailyReminder()
        }
    }

    private fun loadProfileData() {
        val prefs = getSharedPreferences("LoveWidget", MODE_PRIVATE)

        tvName1.text = prefs.getString("name1", "Партнёр 1")
        tvName2.text = prefs.getString("name2", "Партнёр 2")

        loadAvatar(prefs.getString("avatar1_path", null), ivYou)
        loadAvatar(prefs.getString("avatar2_path", null), ivPartner)

        loadPhoto(prefs.getString("widgetBigPhoto_path", null), ivWidgetBigPhoto)
        loadPhoto(prefs.getString("widgetSmallPhoto1_path", null), ivWidgetSmallPhoto1)
        loadPhoto(prefs.getString("widgetSmallPhoto2_path", null), ivWidgetSmallPhoto2)
        loadPhoto(prefs.getString("widgetSmallPhoto3_path", null), ivWidgetSmallPhoto3)
    }

    private fun loadAvatar(path: String?, imageView: ImageView) {
        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                    imageView.setImageURI(uri)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        imageView.setImageResource(R.drawable.ic_person)
    }

    private fun loadPhoto(path: String?, imageView: ImageView) {
        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                try {
                    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
                    imageView.setImageURI(uri)
                    return
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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

    // ——— НОВЫЕ МЕТОДЫ ———

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            15, TimeUnit.MINUTES,  // ✅ теперь напоминание будет приходить каждые 15 минут
            1, TimeUnit.MINUTES    // и первый запуск — через 1 минуту
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork(
                "DailyLoveReminder",
                ExistingPeriodicWorkPolicy.UPDATE,
                workRequest
            )
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    200
                )
            }
        }
    }
}