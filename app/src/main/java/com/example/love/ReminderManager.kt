package com.example.love

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class ReminderManager private constructor(context: Context) {
    private val prefs = context.getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        @Volatile
        private var INSTANCE: ReminderManager? = null

        fun getInstance(context: Context): ReminderManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ReminderManager(context).also { INSTANCE = it }
            }
        }
    }

    fun saveReminders(reminders: List<Reminder>) {
        val json = gson.toJson(reminders)
        prefs.edit().putString("reminders", json).apply()
    }

    fun loadReminders(): List<Reminder> {
        val json = prefs.getString("reminders", null)
        return if (json != null && json != "[]") {
            try {
                val type: Type = object : TypeToken<List<Reminder>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                // При ошибке парсинга — создаём чистый список
                emptyList()
            }
        } else {
            // Только если напоминаний совсем нет — создаём годовщину
            val startDate = prefs.getLong("startDate", -1L)
            if (startDate > 0) {
                val anniversaryDate = startDate + (365L * 24 * 60 * 60 * 1000)
                listOf(Reminder(title = "Годовщина", date = anniversaryDate))
            } else {
                emptyList()
            }
        }
    }

    fun addReminder(reminder: Reminder) {
        val reminders = loadReminders().toMutableList()
        reminders.add(reminder)
        saveReminders(reminders)
    }

    fun deleteReminder(reminderId: Long) {
        val reminders = loadReminders().filter { it.id != reminderId }
        saveReminders(reminders)
    }

    // ✅ НОВЫЙ МЕТОД — обновление существующего напоминания
    fun updateReminder(updatedReminder: Reminder) {
        val reminders = loadReminders().toMutableList()
        val index = reminders.indexOfFirst { it.id == updatedReminder.id }
        if (index != -1) {
            reminders[index] = updatedReminder
            saveReminders(reminders)
        }
        // Если не найдено — игнорируем (можно добавить лог)
    }
}