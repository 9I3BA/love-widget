package com.example.love

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
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
        val prefs = context.getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)

        // ✅ 1. Отправляем уведомление о днях вместе — ВСЕГДА (если есть startDate)
        showDaysTogetherNotification(prefs)

        // ✅ 2. Отправляем уведомление о ближайшем событии (как было)
        val reminders = loadReminders()
        if (reminders.isNotEmpty()) {
            val now = System.currentTimeMillis()
            val nearest = reminders
                .map { reminder ->
                    val daysLeft = calculateCyclicDaysLeft(reminder.date, now)
                    reminder to daysLeft
                }
                .minByOrNull { it.second } ?: return@withContext Result.success()

            val (nextReminder, days) = nearest
            showEventNotification(nextReminder.title, days)
        }

        Result.success()
    }

    // ✅ Новое: уведомление о "днях вместе"
    private fun showDaysTogetherNotification(prefs: android.content.SharedPreferences) {
        val startDate = prefs.getLong("startDate", -1)
        if (startDate == -1L) return // дата не задана — не показываем

        val now = System.currentTimeMillis()
        val diff = now - startDate
        if (diff < 0) return // дата в будущем

        val totalDays = diff / (24 * 60 * 60 * 1000)

        val years = totalDays / 365
        val months = (totalDays % 365) / 30
        val remainingDays = (totalDays % 365) % 30

        val parts = mutableListOf<String>()
        if (years > 0) parts.add("${years} ${declineYears(years)}")
        if (months > 0) parts.add("${months} ${declineMonths(months)}")
        if (remainingDays > 0 || parts.isEmpty()) {
            parts.add("${remainingDays} ${declineDays(remainingDays)}")
        }

        val text = "Вы вместе уже: ${parts.joinToString(" ")} ❤️"

        val channelId = "love_days_together"
        val id = 102 // ⚠️ уникальный ID, чтобы не затирать событие (101)

        createNotificationChannelIfNeeded(channelId, "Дни вместе", "Уведомление о длительности отношений")

        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle("❤️ Дни вместе")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        // 🔐 Проверка разрешения
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context).notify(id, builder.build())
    }

    // ✅ Вспомогательные функции для склонения (по типу)
    private fun declineYears(n: Long) = decline(n, "год", "года", "лет")
    private fun declineMonths(n: Long) = decline(n, "месяц", "месяца", "месяцев")
    private fun declineDays(n: Long) = decline(n, "день", "дня", "дней")

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

    // ✅ Вынесено — создаём канал один раз для любого ID
    private fun createNotificationChannelIfNeeded(
        channelId: String,
        name: String,
        description: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT).apply {
                this.description = description
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    // 🔁 Как было — без изменений
    private fun calculateCyclicDaysLeft(dateMillis: Long, now: Long): Int {
        val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        cal.set(Calendar.YEAR, currentYear)

        if (cal.timeInMillis <= now) {
            cal.add(Calendar.YEAR, 1)
        }

        val nextOccurrence = cal.timeInMillis
        return ((nextOccurrence - now + 12 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000)).toInt()
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

    // ✅ Теперь showEventNotification — только для событий (старый showNotification → переименован)
    private fun showEventNotification(title: String, daysLeft: Int) {
        val channelId = "love_daily"
        val id = 101

        createNotificationChannelIfNeeded(channelId, "Напоминания о любви", "Ближайшая памятная дата")

        val intent = Intent(context, HomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pending = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val text = "До события «$title» осталось: $daysLeft ${declineDays(daysLeft.toLong())}"

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_heart)
            .setContentTitle("❤️ Виджет Любви")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(pending)
            .setAutoCancel(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        NotificationManagerCompat.from(context).notify(id, builder.build())
    }
}