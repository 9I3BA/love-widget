package com.example.love

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AnniversaryActivity : AppCompatActivity() {

    private lateinit var remindersListView: ListView
    private lateinit var btnAddReminder: Button
    private lateinit var btnBackToHome: Button
    private lateinit var emptyView: TextView

    private var reminders = mutableListOf<Reminder>()
    private lateinit var adapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anniversary)

        remindersListView = findViewById(R.id.remindersListView)
        btnAddReminder = findViewById(R.id.btnAddReminder)
        btnBackToHome = findViewById(R.id.btnBackToHome)
        emptyView = findViewById(R.id.emptyView)

        // Настройка адаптера
        adapter = ReminderAdapter(this, reminders)
        remindersListView.adapter = adapter
        remindersListView.emptyView = emptyView

        // Загрузка напоминаний
        loadReminders()

        // Обработчик кнопки добавления
        btnAddReminder.setOnClickListener {
            val intent = Intent(this, CreateReminderActivity::class.java)
            startActivityForResult(intent, REQUEST_CREATE_REMINDER)
        }

        // Обработчик кнопки возврата в главное меню
        btnBackToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        // Долгое нажатие для удаления
        remindersListView.setOnItemLongClickListener { _, _, position, _ ->
            showDeleteDialog(position)
            true
        }
    }

    private fun loadReminders() {
        reminders.clear()
        reminders.addAll(ReminderManager.getInstance(this).loadReminders())
        adapter.notifyDataSetChanged()
    }

    private fun showDeleteDialog(position: Int) {
        val reminder = reminders[position]
        AlertDialog.Builder(this)
            .setTitle("Удалить напоминание?")
            .setMessage("Вы уверены, что хотите удалить '${reminder.title}'?")
            .setPositiveButton("Да") { _, _ ->
                ReminderManager.getInstance(this).deleteReminder(reminder.id)
                loadReminders()
            }
            .setNegativeButton("Нет", null)
            .show()
    }

    companion object {
        private const val REQUEST_CREATE_REMINDER = 100
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CREATE_REMINDER && resultCode == RESULT_OK) {
            loadReminders()
        }
    }
}