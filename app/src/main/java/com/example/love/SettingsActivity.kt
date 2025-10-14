package com.example.love

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var etName1: EditText
    private lateinit var etName2: EditText
    private lateinit var btnDate: Button
    private lateinit var btnSave: Button
    private lateinit var ivSettingsAvatar1: ImageView
    private lateinit var ivSettingsAvatar2: ImageView
    private lateinit var ivWidgetBigPhoto: ImageView
    private lateinit var ivWidgetSmallPhoto1: ImageView
    private lateinit var ivWidgetSmallPhoto2: ImageView
    private lateinit var ivWidgetSmallPhoto3: ImageView

    private var avatar1Uri: Uri? = null
    private var avatar2Uri: Uri? = null
    private var bigPhotoUri: Uri? = null
    private var smallPhoto1Uri: Uri? = null
    private var smallPhoto2Uri: Uri? = null
    private var smallPhoto3Uri: Uri? = null
    private var startDateMillis: Long = 0

    companion object {
        private const val REQ_AVATAR1 = 1
        private const val REQ_AVATAR2 = 2
        private const val REQ_BIG_PHOTO = 3
        private const val REQ_SMALL_PHOTO1 = 4
        private const val REQ_SMALL_PHOTO2 = 5
        private const val REQ_SMALL_PHOTO3 = 6
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        initViews()
        loadPreferences()
        setupClickListeners()
    }

    private fun initViews() {
        etName1 = findViewById(R.id.etSettingsName1)
        etName2 = findViewById(R.id.etSettingsName2)
        btnDate = findViewById(R.id.btnChangeDate)
        btnSave = findViewById(R.id.btnSave)
        ivSettingsAvatar1 = findViewById(R.id.ivSettingsAvatar1)
        ivSettingsAvatar2 = findViewById(R.id.ivSettingsAvatar2)
        ivWidgetBigPhoto = findViewById(R.id.ivWidgetBigPhoto)
        ivWidgetSmallPhoto1 = findViewById(R.id.ivWidgetSmallPhoto1)
        ivWidgetSmallPhoto2 = findViewById(R.id.ivWidgetSmallPhoto2)
        ivWidgetSmallPhoto3 = findViewById(R.id.ivWidgetSmallPhoto3)
    }

    private fun loadPreferences() {
        try {
            val prefs = getSharedPreferences("LoveWidget", MODE_PRIVATE)

            // Имена
            etName1.setText(prefs.getString("name1", "Партнёр 1"))
            etName2.setText(prefs.getString("name2", "Партнёр 2"))

            // Дата
            startDateMillis = prefs.getLong("startDate", System.currentTimeMillis())
            updateDateButtonText()

            // Аватары
            val avatar1Path = prefs.getString("avatar1", null)
            val avatar2Path = prefs.getString("avatar2", null)

            avatar1Path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    avatar1Uri = Uri.fromFile(file)
                    ivSettingsAvatar1.setImageURI(avatar1Uri)
                }
            }

            avatar2Path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    avatar2Uri = Uri.fromFile(file)
                    ivSettingsAvatar2.setImageURI(avatar2Uri)
                }
            }

            // Фото для виджета
            val bigPhotoPath = prefs.getString("widgetBigPhoto", null)
            val smallPhoto1Path = prefs.getString("widgetSmallPhoto1", null)
            val smallPhoto2Path = prefs.getString("widgetSmallPhoto2", null)
            val smallPhoto3Path = prefs.getString("widgetSmallPhoto3", null)

            bigPhotoPath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    bigPhotoUri = Uri.fromFile(file)
                    ivWidgetBigPhoto.setImageURI(bigPhotoUri)
                }
            }

            smallPhoto1Path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    smallPhoto1Uri = Uri.fromFile(file)
                    ivWidgetSmallPhoto1.setImageURI(smallPhoto1Uri)
                }
            }

            smallPhoto2Path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    smallPhoto2Uri = Uri.fromFile(file)
                    ivWidgetSmallPhoto2.setImageURI(smallPhoto2Uri)
                }
            }

            smallPhoto3Path?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    smallPhoto3Uri = Uri.fromFile(file)
                    ivWidgetSmallPhoto3.setImageURI(smallPhoto3Uri)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        ivSettingsAvatar1.setOnClickListener { openGallery(REQ_AVATAR1) }
        ivSettingsAvatar2.setOnClickListener { openGallery(REQ_AVATAR2) }
        ivWidgetBigPhoto.setOnClickListener { openGallery(REQ_BIG_PHOTO) }
        ivWidgetSmallPhoto1.setOnClickListener { openGallery(REQ_SMALL_PHOTO1) }
        ivWidgetSmallPhoto2.setOnClickListener { openGallery(REQ_SMALL_PHOTO2) }
        ivWidgetSmallPhoto3.setOnClickListener { openGallery(REQ_SMALL_PHOTO3) }
        btnDate.setOnClickListener { showDatePicker() }
        btnSave.setOnClickListener { saveAndExit() }
    }

    private fun openGallery(requestCode: Int) {
        try {
            Intent(Intent.ACTION_PICK).also { intent ->
                intent.type = "image/*"
                startActivityForResult(intent, requestCode)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Галерея недоступна", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance().apply { timeInMillis = startDateMillis }
        DatePickerDialog(
            this,
            { _, year, month, day ->
                Calendar.getInstance().apply {
                    set(year, month, day)
                    startDateMillis = timeInMillis
                    updateDateButtonText()
                }
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateDateButtonText() {
        val date = Date(startDateMillis)
        val formatter = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        btnDate.text = formatter.format(date)
    }

    private fun saveAndExit() {
        try {
            val editor = getSharedPreferences("LoveWidget", MODE_PRIVATE).edit()

            // Сохраняем данные
            editor.putString("name1", etName1.text.toString().ifEmpty { "Партнёр 1" })
            editor.putString("name2", etName2.text.toString().ifEmpty { "Партнёр 2" })
            editor.putLong("startDate", startDateMillis)

            // Сохраняем аватары (путь к файлу)
            avatar1Uri?.let { uri ->
                val filePath = uri.path
                if (filePath != null) {
                    editor.putString("avatar1", filePath)
                }
            }

            avatar2Uri?.let { uri ->
                val filePath = uri.path
                if (filePath != null) {
                    editor.putString("avatar2", filePath)
                }
            }

            // Сохраняем фото для виджета
            bigPhotoUri?.let { uri ->
                val filePath = uri.path
                if (filePath != null) {
                    editor.putString("widgetBigPhoto", filePath)
                }
            }

            smallPhoto1Uri?.let { uri ->
                val filePath = uri.path
                if (filePath != null) {
                    editor.putString("widgetSmallPhoto1", filePath)
                }
            }

            smallPhoto2Uri?.let { uri ->
                val filePath = uri.path
                if (filePath != null) {
                    editor.putString("widgetSmallPhoto2", filePath)
                }
            }

            smallPhoto3Uri?.let { uri ->
                val filePath = uri.path
                if (filePath != null) {
                    editor.putString("widgetSmallPhoto3", filePath)
                }
            }

            editor.apply()

            // Устанавливаем результат для HomeActivity
            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyUriToFile(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return

            when (requestCode) {
                REQ_AVATAR1 -> {
                    val filePath = copyUriToFile(uri)
                    if (filePath != null) {
                        avatar1Uri = Uri.fromFile(File(filePath))
                        ivSettingsAvatar1.setImageURI(avatar1Uri)
                    }
                }
                REQ_AVATAR2 -> {
                    val filePath = copyUriToFile(uri)
                    if (filePath != null) {
                        avatar2Uri = Uri.fromFile(File(filePath))
                        ivSettingsAvatar2.setImageURI(avatar2Uri)
                    }
                }
                REQ_BIG_PHOTO -> {
                    val filePath = copyUriToFile(uri)
                    if (filePath != null) {
                        bigPhotoUri = Uri.fromFile(File(filePath))
                        ivWidgetBigPhoto.setImageURI(bigPhotoUri)
                    }
                }
                REQ_SMALL_PHOTO1 -> {
                    val filePath = copyUriToFile(uri)
                    if (filePath != null) {
                        smallPhoto1Uri = Uri.fromFile(File(filePath))
                        ivWidgetSmallPhoto1.setImageURI(smallPhoto1Uri)
                    }
                }
                REQ_SMALL_PHOTO2 -> {
                    val filePath = copyUriToFile(uri)
                    if (filePath != null) {
                        smallPhoto2Uri = Uri.fromFile(File(filePath))
                        ivWidgetSmallPhoto2.setImageURI(smallPhoto2Uri)
                    }
                }
                REQ_SMALL_PHOTO3 -> {
                    val filePath = copyUriToFile(uri)
                    if (filePath != null) {
                        smallPhoto3Uri = Uri.fromFile(File(filePath))
                        ivWidgetSmallPhoto3.setImageURI(smallPhoto3Uri)
                    }
                }
            }
        }
    }
}