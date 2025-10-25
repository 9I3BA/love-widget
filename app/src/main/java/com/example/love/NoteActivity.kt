package com.example.love // ← ваш реальный package!

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class NoteActivity : AppCompatActivity() {

    private lateinit var etNoteContent: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)

        etNoteContent = findViewById(R.id.etNoteContent)
        val btnSave = findViewById<Button>(R.id.btnSaveNote)

        val savedNote = getSharedPreferences("LoveWidget", MODE_PRIVATE)
            .getString("note", "")
        etNoteContent.setText(savedNote)

        btnSave.setOnClickListener {
            saveNote()
            finish()
        }
    }

    private fun saveNote() {
        val text = etNoteContent.text.toString()
        getSharedPreferences("LoveWidget", MODE_PRIVATE).edit()
            .putString("note", text)
            .apply()
        Toast.makeText(this, "Заметка сохранена", Toast.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        saveNote()
        super.onBackPressed()
    }
}