// CreateReminderActivity.kt
package com.example.love

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class CreateReminderActivity : AppCompatActivity() {

    private lateinit var tvSelectedDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var etReminderTheme: EditText
    private lateinit var btnSave: Button

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reminder)

        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnPickDate = findViewById(R.id.btnPickDate)
        etReminderTheme = findViewById(R.id.etReminderTheme)
        btnSave = findViewById(R.id.btnSave)

        updateDateDisplay()

        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnSave.setOnClickListener {
            saveReminder()
        }
    }

    private fun showDatePicker() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            selectedDate.set(y, m, d)
            updateDateDisplay()
        }, year, month, day).show()
    }

    private fun updateDateDisplay() {
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        tvSelectedDate.text = formatter.format(selectedDate.time)
    }

    private fun saveReminder() {
        try {
            val theme = etReminderTheme.text.toString().ifEmpty { "Важное событие" }
            val dateMillis = selectedDate.timeInMillis

            val reminder = Reminder(title = theme, date = dateMillis)
            ReminderManager.getInstance(this).addReminder(reminder)

            Toast.makeText(this, "Напоминание добавлено!", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
        }
    }
}