package com.example.love

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var etName1: EditText
    private lateinit var etName2: EditText
    private lateinit var btnNext: Button
    private lateinit var ivAvatar1: ImageView
    private lateinit var ivAvatar2: ImageView

    private var avatar1Uri: Uri? = null
    private var avatar2Uri: Uri? = null

    companion object {
        private const val REQUEST_CODE_GALLERY_1 = 1001
        private const val REQUEST_CODE_GALLERY_2 = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        // Находим элементы по ID
        etName1 = findViewById(R.id.etName1)
        etName2 = findViewById(R.id.etName2)
        btnNext = findViewById(R.id.btnNext)
        ivAvatar1 = findViewById(R.id.ivAvatar1)
        ivAvatar2 = findViewById(R.id.ivAvatar2)

        // Обработчики кликов по аватарам
        ivAvatar1.setOnClickListener {
            openGallery(REQUEST_CODE_GALLERY_1)
        }

        ivAvatar2.setOnClickListener {
            openGallery(REQUEST_CODE_GALLERY_2)
        }

        btnNext.setOnClickListener {
            saveProfileDataAndProceed()
        }
    }

    private fun openGallery(requestCode: Int) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data

            when (requestCode) {
                REQUEST_CODE_GALLERY_1 -> {
                    avatar1Uri = selectedImageUri
                    ivAvatar1.setImageURI(selectedImageUri)
                }
                REQUEST_CODE_GALLERY_2 -> {
                    avatar2Uri = selectedImageUri
                    ivAvatar2.setImageURI(selectedImageUri)
                }
            }
        }
    }

    private fun copyUriToFile(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)

            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun saveProfileDataAndProceed() {
        try {
            val name1 = etName1.text.toString().takeIf { it.isNotBlank() } ?: "Партнёр 1"
            val name2 = etName2.text.toString().takeIf { it.isNotBlank() } ?: "Партнёр 2"

            val editor = getSharedPreferences("LoveWidget", MODE_PRIVATE).edit()
            editor.putString("name1", name1)
            editor.putString("name2", name2)

            // Сохраняем аватары как файлы
            avatar1Uri?.let { uri ->
                val filePath = copyUriToFile(uri)
                if (filePath != null) {
                    editor.putString("avatar1", filePath)
                }
            }

            avatar2Uri?.let { uri ->
                val filePath = copyUriToFile(uri)
                if (filePath != null) {
                    editor.putString("avatar2", filePath)
                }
            }

            editor.apply()

            // Переходим на главный экран
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Ошибка сохранения профиля", Toast.LENGTH_SHORT).show()
        }
    }
}