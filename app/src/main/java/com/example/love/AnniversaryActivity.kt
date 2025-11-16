package com.example.love

import android.content.Intent
import android.os.Bundle
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

        adapter.onDeleteClickListener = { reminder ->
            // 1. Удаляем из менеджера
            ReminderManager.getInstance(this).deleteReminder(reminder.id)

            // 2. Удаляем из локального списка
            reminders.remove(reminder)

            // 3. Обновляем адаптер
            adapter.notifyDataSetChanged()

            Toast.makeText(this, "✅ Напоминание удалено", Toast.LENGTH_SHORT).show()
        }
        loadReminders()

        btnAddReminder.setOnClickListener {
            val intent = Intent(this, CreateReminderActivity::class.java)
            startActivityForResult(intent, REQUEST_CREATE_REMINDER)
        }

        btnBackToHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
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