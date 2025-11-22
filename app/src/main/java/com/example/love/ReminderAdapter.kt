package com.example.love

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

class ReminderAdapter(
    private val context: Context,
    private val reminders: List<Reminder>
) : BaseAdapter() {

    var onDeleteClickListener: ((Reminder) -> Unit)? = null
    var onReminderUpdatedListener: (() -> Unit)? = null

    override fun getCount() = reminders.size

    override fun getItem(position: Int) = reminders[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_reminder, parent, false)

        val reminder = reminders[position]

        // 📌 ИСПОЛЬЗУЕМ ТОЧНЫЕ ID ИЗ item_reminder.xml
        val tvTitle = view.findViewById<TextView>(R.id.tvReminderTitle)
        val tvDate = view.findViewById<TextView>(R.id.tvReminderDate)
        val tvDaysLeft = view.findViewById<TextView>(R.id.tvDaysLeft)
        val btnDelete = view.findViewById<Button>(R.id.btnDeleteReminder) // ← Это Button, не ImageButton!

        tvTitle.text = reminder.title
        tvDate.text = formatDate(reminder.date)

        // ✅ Точный расчёт дней до даты (без учёта времени)
        val daysLeft = getDaysUntil(reminder.date)

        val daysText = when {
            daysLeft < 0 -> {
                val past = -daysLeft
                "$past ${decline(past)} назад"
            }
            daysLeft == 0L -> "Сегодня!"
            else -> "$daysLeft ${decline(daysLeft)}"
        }

        tvDaysLeft.text = daysText

        // Кнопка "Удалить" — это Button, а не ImageButton!
        btnDelete.setOnClickListener {
            onDeleteClickListener?.invoke(reminder)
        }

        // ❗ Важно: у тебя нет кнопки "Ежегодно" как отдельной кнопки в item_reminder.xml
        // Если нужно — добавь её, или убери логику с toggleAnnual

        return view
    }

    // ✅ Точный расчёт дней до даты (без учёта времени)
    private fun getDaysUntil(targetDateMillis: Long): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply { timeInMillis = targetDateMillis }

        // Обнуляем время — работаем только с датами
        listOf(now, target).forEach { cal ->
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
        }

        val diffMs = target.timeInMillis - now.timeInMillis
        return diffMs / (24 * 60 * 60 * 1000L)
    }

    private fun formatDate(millis: Long): String {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        return "${cal.get(Calendar.DAY_OF_MONTH)}.${cal.get(Calendar.MONTH) + 1}.${cal.get(Calendar.YEAR)}"
    }

    private fun decline(n: Long): String = when {
        n % 10 == 1L && n % 100 != 11L -> "день"
        n % 10 in 2L..4L && n % 100 !in 12L..14L -> "дня"
        else -> "дней"
    }
}