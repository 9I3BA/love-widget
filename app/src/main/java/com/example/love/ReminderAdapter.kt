package com.example.love

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val context: Context,
    private val reminders: MutableList<Reminder>
) : BaseAdapter() {

    // Callback для удаления — вызывается из AnniversaryActivity
    var onDeleteClickListener: ((Reminder) -> Unit)? = null

    override fun getCount() = reminders.size
    override fun getItem(position: Int) = reminders[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_reminder, parent, false)

        val reminder = reminders[position]

        // Находим TextView по ID — они точно есть (из вашего item_reminder.xml)
        val title = view.findViewById<TextView>(R.id.tvReminderTitle)
        val date = view.findViewById<TextView>(R.id.tvReminderDate)
        val daysLeft = view.findViewById<TextView>(R.id.tvDaysLeft)

        // ⚠️ Кнопка — НЕ btnDeleteReminder, а btnBackToHome (см. ваш item_reminder.xml)
        val deleteButton = view.findViewById<Button>(R.id.btnDeleteReminder)

        // Заполняем данными
        title.text = reminder.title
        date.text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(reminder.date))
        daysLeft.text = "${calculateDaysLeft(reminder.date)} дней"

        // 🔥 Подключаем обработчик удаления
        deleteButton.setOnClickListener {
            onDeleteClickListener?.invoke(reminder)
        }

        return view
    }

    // В ReminderAdapter.kt замените:
    private fun calculateDaysLeft(dateMillis: Long): Int {
        val now = System.currentTimeMillis()
        val days = ((dateMillis - now + 12 * 60 * 60 * 1000) / (24 * 60 * 60 * 1000)).toInt()
        return if (days >= 0) days else -1 // или используйте циклическую логику, если хотите
    }
}