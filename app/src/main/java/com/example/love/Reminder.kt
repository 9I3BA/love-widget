// Reminder.kt
package com.example.love

import java.io.Serializable

data class Reminder(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val date: Long // timestamp in milliseconds
) : Serializable