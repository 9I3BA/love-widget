package com.example.love

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.util.Calendar
import java.util.concurrent.TimeUnit
import kotlin.math.abs

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

        // 🔔 Запрос разрешения и планирование напоминаний
        requestNotificationPermissionIfNeeded()
        scheduleDailyReminder()

        // ✅ Показываем уведомление при старте
        showStartupReminderIfNeeded()
        showDaysTogetherStartupNotification()  // ← НОВОЕ: уведомление о днях вместе

        // ✅ Показываем уведомление ТОЛЬКО если активити запущена как главная (не при возврате)
        if (intent.flags and Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY == 0 &&
            (intent.flags and Intent.FLAG_ACTIVITY_CLEAR_TOP == 0) &&
            (savedInstanceState == null)
        ) {
            showStartupReminderIfNeeded()
            showDaysTogetherStartupNotification()  // ← НОВОЕ: повторно при первом запуске
        }

        btnNotes.setOnClickListener {
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
            scheduleDailyReminder()  // обновляем при изменении даты
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
        if (!path.isNullOrEmpty()) {
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
        if (!path.isNullOrEmpty()) {
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

        val parts = mutableListOf<String>()
        if (years > 0) parts.add("${years} ${decline(years, "год", "года", "лет")}")
        if (months > 0) parts.add("${months} ${decline(months, "месяц", "месяца", "месяцев")}")
        if (remainingDays > 0 || parts.isEmpty()) {
            parts.add("${remainingDays} ${decline(remainingDays, "день", "дня", "дней")}")
        }

        tvDaysTogether.text = parts.joinToString(" ")
    }

    // Вспомогательная функция для склонения числительных
    private fun decline(n: Long, one: String, few: String, many: String): String {
        val mod10 = n % 10
        val mod100 = n % 100
        return when {
            mod100 in 11..14 -> many
            mod10 == 1L -> one
            mod10 in 2L..4L -> few
            else -> many
        }
    }

    private fun scheduleDailyReminder() {
        val workRequest = PeriodicWorkRequestBuilder<DailyReminderWorker>(
            15, TimeUnit.MINUTES,
            1, TimeUnit.MINUTES
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
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    200
                )
            }
        }
    }

    // ✅ Уведомление при запуске приложения
    private var isStartupReminderShown = false

    private fun showStartupReminderIfNeeded() {
        val prefs = getSharedPreferences("LoveWidget", MODE_PRIVATE)
        val json = prefs.getString("reminders", "[]") ?: "[]"
        val reminders = try {
            val listType = object : TypeToken<List<Reminder>>() {}.type
            Gson().fromJson<List<Reminder>>(json, listType)
        } catch (e: Exception) {
            emptyList<Reminder>()
        }

        if (reminders.isEmpty()) return

        val now = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        var nearestReminder: Reminder? = null
        var minDays = Long.MAX_VALUE

        for (r in reminders) {
            val target = Calendar.getInstance().apply { timeInMillis = r.date }.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val candidate = target.clone() as Calendar
            candidate.set(Calendar.YEAR, now.get(Calendar.YEAR))

            if (candidate.timeInMillis < now.timeInMillis) {
                candidate.add(Calendar.YEAR, 1)
            }

            val diffMs = candidate.timeInMillis - now.timeInMillis
            val diffDays = diffMs / (24 * 60 * 60 * 1000L)
            if (diffDays < minDays) {
                minDays = diffDays
                nearestReminder = r
            }
        }

        nearestReminder ?: return

        // Показ уведомления
        val channelId = "love_daily"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Напоминания о любви",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val days = minDays.toInt()
        val dayWord = when {
            days % 10 == 1 && days % 100 != 11 -> "день"
            days % 10 in 2..4 && days % 100 !in 12..14 -> "дня"
            else -> "дней"
        }

        val text = "До события «${nearestReminder.title}» осталось: $days $dayWord"

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle("❤️ Напоминание:")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        NotificationManagerCompat.from(this).notify(101, builder.build())
    }

    // ✅ НОВОЕ: Уведомление о днях вместе при запуске
    private fun showDaysTogetherStartupNotification() {
        val prefs = getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)
        val startDate = prefs.getLong("startDate", -1L)
        if (startDate == -1L) return

        val now = System.currentTimeMillis()
        val diff = now - startDate
        if (diff < 0) return

        val totalDays = diff / (24 * 60 * 60 * 1000)

        val years = totalDays / 365
        val months = (totalDays % 365) / 30
        val remainingDays = (totalDays % 365) % 30

        val parts = mutableListOf<String>()
        if (years > 0) parts.add("${years} ${decline(years, "год", "года", "лет")}")
        if (months > 0) parts.add("${months} ${decline(months, "месяц", "месяца", "месяцев")}")
        if (remainingDays > 0 || parts.isEmpty()) {
            parts.add("${remainingDays} ${decline(remainingDays, "день", "дня", "дней")}")
        }

        val text = "Вы вместе уже: ${parts.joinToString(" ")} ❤️"

        val channelId = "love_days_together"
        val id = 102  // уникальный ID

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Дни вместе",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ежедневное напоминание о длительности отношений"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(this, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            this, 1, intent,  // requestCode = 1 — отличается от 0
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle("❤️ Дни вместе:")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) return
        }

        NotificationManagerCompat.from(this).notify(id, builder.build())
    }
}