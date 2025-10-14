// ReminderAdapter.kt
package com.example.love

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ReminderAdapter(
    private val context: Context,
    private val reminders: List<Reminder>
) : BaseAdapter() {

    override fun getCount() = reminders.size
    override fun getItem(position: Int) = reminders[position]
    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: View.inflate(context, R.layout.item_reminder, null)

        val reminder = reminders[position]
        val titleView = view.findViewById<TextView>(R.id.tvReminderTitle)
        val dateView = view.findViewById<TextView>(R.id.tvReminderDate)
        val daysView = view.findViewById<TextView>(R.id.tvDaysLeft)

        titleView.text = reminder.title

        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        dateView.text = formatter.format(Date(reminder.date))

        val now = Calendar.getInstance().timeInMillis
        val diff = reminder.date - now
        val daysLeft = if (diff > 0) diff / (1000 * 60 * 60 * 24) else 0
        daysView.text = "$daysLeft дней"

        return view
    }
}