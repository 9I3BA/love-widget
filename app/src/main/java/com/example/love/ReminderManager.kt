package com.example.love

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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
        return if (json != null) {
            val type = object : TypeToken<List<Reminder>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            // Создаём напоминание по умолчанию (годовщина)
            val startDate = prefs.getLong("startDate", System.currentTimeMillis())
            val anniversary = Reminder(
                title = "Годовщина",
                date = startDate + (365L * 24 * 60 * 60 * 1000) // +1 год
            )
            listOf(anniversary)
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
}