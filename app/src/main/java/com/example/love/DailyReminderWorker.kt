package com.example.love

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class DailyReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val reminders = loadReminders()
        if (reminders.isEmpty()) return@withContext Result.success()

        val now = System.currentTimeMillis()

        // 🔍 1. Будущие напоминания
        val upcoming = reminders
            .filter { it.date > now }
            .map { it to daysDiff(it.date, now) }
            .sortedBy { it.second }

        // ✅ Ближайшая будущая ИЛИ циклическая (если все прошли)
        val (next, days) = upcoming.firstOrNull()
            ?: run {
                val cyclic = reminders
                    .map { it to cyclicDaysDiff(it.date, now) }
                    .minByOrNull { it.second }
                    ?: return@withContext Result.success()
                cyclic
            }

        showNotification(next.title, days)
        Result.success()
    }

    private fun daysDiff(dateMillis: Long, now: Long): Int {
        return ((dateMillis - now + 12 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000)).toInt()
    }

    private fun cyclicDaysDiff(dateMillis: Long, now: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        cal.set(Calendar.YEAR, currentYear)
        if (cal.timeInMillis <= now) {
            cal.add(Calendar.YEAR, 1)
        }
        return daysDiff(cal.timeInMillis, now)
    }

    private fun loadReminders(): List<Reminder> {
        val prefs = context.getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)
        val json = prefs.getString("reminders", "[]") ?: "[]"
        return try {
            val type = object : com.google.gson.reflect.TypeToken<List<Reminder>>() {}.type
            com.google.gson.Gson().fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun showNotification(title: String, daysLeft: Int) {
        val channelId = "love_daily"
        val id = 101

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId, "Напоминания о любви", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Ближайшая памятная дата" }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val text = "До события «$title» осталось: $daysLeft ${decline(daysLeft)}"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle("❤️ Виджет Любви")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)

        NotificationManagerCompat.from(context).notify(id, builder.build())
    }

    private fun decline(n: Int): String = when {
        n % 10 == 1 && n % 100 != 11 -> "день"
        n % 10 in 2..4 && n % 100 !in 12..14 -> "дня"
        else -> "дней"
    }
}