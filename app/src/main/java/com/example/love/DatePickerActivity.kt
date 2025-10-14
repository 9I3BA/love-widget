package com.example.love

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class DatePickerActivity : AppCompatActivity() {
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnPickDate: Button
    private lateinit var btnNext: Button
    private lateinit var btnCreateReminder: Button

    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_date_picker)

        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnNext = findViewById(R.id.btnNext)
        btnCreateReminder = findViewById(R.id.btnCreateReminder)

        updateDateDisplay()

        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnCreateReminder.setOnClickListener {
            startActivity(Intent(this, CreateReminderActivity::class.java))
        }

        btnNext.setOnClickListener {
            saveDateAndProceed()
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
        val formatter = java.text.SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        tvSelectedDate.text = formatter.format(selectedDate.time)
    }

    private fun saveDateAndProceed() {
        val editor = getSharedPreferences("LoveWidget", MODE_PRIVATE).edit()
        editor.putLong("startDate", selectedDate.timeInMillis)
        editor.putBoolean("isFirstLaunch", false)
        editor.apply()

        startActivity(Intent(this, ProfileSetupActivity::class.java))
    }
}