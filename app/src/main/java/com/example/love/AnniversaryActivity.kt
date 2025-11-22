package com.example.love

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

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

        adapter = ReminderAdapter(this, reminders)
        remindersListView.adapter = adapter
        remindersListView.emptyView = emptyView

        // 🔥 Удаляем напоминание
        adapter.onDeleteClickListener = { reminder ->
            ReminderManager.getInstance(this).deleteReminder(reminder.id)
            reminders.remove(reminder)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "✅ Напоминание удалено", Toast.LENGTH_SHORT).show()
        }

        // 🔁 Обновляем список после изменений
        adapter.onReminderUpdatedListener = {
            loadReminders()
        }

        loadReminders()

        btnAddReminder.setOnClickListener {
            val intent = Intent(this, CreateReminderActivity::class.java)
            startActivityForResult(intent, REQUEST_CREATE_REMINDER)
        }

        // ✅ ИСПРАВЛЕНО: не создаём новую HomeActivity — просто возвращаемся
        btnBackToHome.setOnClickListener {
            finish() // ← вот и всё! теперь HomeActivity не пересоздаётся
        }
    }

    override fun onResume() {
        super.onResume()
        loadReminders()
    }

    private fun loadReminders() {
        reminders.clear()
        reminders.addAll(ReminderManager.getInstance(this).loadReminders())
        adapter.notifyDataSetChanged()
        emptyView.visibility = if (reminders.isEmpty()) View.VISIBLE else View.GONE
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