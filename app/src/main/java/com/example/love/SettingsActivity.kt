package com.example.love

import android.app.DatePickerDialog
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
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

    private var avatar1File: File? = null
    private var avatar2File: File? = null
    private var bigPhotoFile: File? = null
    private var smallPhoto1File: File? = null
    private var smallPhoto2File: File? = null
    private var smallPhoto3File: File? = null
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
        val prefs = getSharedPreferences("LoveWidget", MODE_PRIVATE)

        etName1.setText(prefs.getString("name1", "Партнёр 1"))
        etName2.setText(prefs.getString("name2", "Партнёр 2"))

        startDateMillis = prefs.getLong("startDate", System.currentTimeMillis())
        updateDateButtonText()

        avatar1File = prefs.getString("avatar1_path", null)?.let { File(it) }
        avatar2File = prefs.getString("avatar2_path", null)?.let { File(it) }
        bigPhotoFile = prefs.getString("widgetBigPhoto_path", null)?.let { File(it) }
        smallPhoto1File = prefs.getString("widgetSmallPhoto1_path", null)?.let { File(it) }
        smallPhoto2File = prefs.getString("widgetSmallPhoto2_path", null)?.let { File(it) }
        smallPhoto3File = prefs.getString("widgetSmallPhoto3_path", null)?.let { File(it) }

        setImageView(ivSettingsAvatar1, avatar1File)
        setImageView(ivSettingsAvatar2, avatar2File)
        setImageView(ivWidgetBigPhoto, bigPhotoFile)
        setImageView(ivWidgetSmallPhoto1, smallPhoto1File)
        setImageView(ivWidgetSmallPhoto2, smallPhoto2File)
        setImageView(ivWidgetSmallPhoto3, smallPhoto3File)
    }

    private fun setImageView(imageView: ImageView, file: File?) {
        if (file?.exists() == true) {
            val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
            imageView.setImageURI(uri)
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
            Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
                startActivityForResult(this, requestCode)
            }
        } catch (e: Exception) {
            Log.e("SettingsActivity", "Галерея недоступна", e)
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

    private fun copyUriToInternalFile(uri: Uri): File? {
        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                val fileName = "photo_${System.currentTimeMillis()}.jpg"
                val file = File(filesDir, fileName)

                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null
                val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 400, 400, true)

                FileOutputStream(file).use { outputStream ->
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                }

                file
            }
        } catch (e: Exception) {
            Log.e("SettingsActivity", "Ошибка копирования файла", e)
            null
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }

    private fun saveAndExit() {
        try {
            val editor = getSharedPreferences("LoveWidget", MODE_PRIVATE).edit()

            editor.putString("name1", etName1.text.toString().ifEmpty { "Партнёр 1" })
            editor.putString("name2", etName2.text.toString().ifEmpty { "Партнёр 2" })
            editor.putLong("startDate", startDateMillis)

            // Сохраняем пути
            editor.putString("avatar1_path", avatar1File?.absolutePath)
            editor.putString("avatar2_path", avatar2File?.absolutePath)
            editor.putString("widgetBigPhoto_path", bigPhotoFile?.absolutePath)
            editor.putString("widgetSmallPhoto1_path", smallPhoto1File?.absolutePath)
            editor.putString("widgetSmallPhoto2_path", smallPhoto2File?.absolutePath)
            editor.putString("widgetSmallPhoto3_path", smallPhoto3File?.absolutePath)

            // Сохраняем Base64 миниатюры для виджета
            avatar1File?.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val scaled = Bitmap.createScaledBitmap(bitmap, 80, 80, true)
                editor.putString("avatar1_base64", bitmapToBase64(scaled))
            }

            avatar2File?.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val scaled = Bitmap.createScaledBitmap(bitmap, 80, 80, true)
                editor.putString("avatar2_base64", bitmapToBase64(scaled))
            }

            editor.apply()
            updateWidgets()
            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            Log.e("SettingsActivity", "Ошибка при сохранении", e)
        }
    }

    private fun updateWidgets() {
        val appWidgetManager = AppWidgetManager.getInstance(this)

        val daysWidget = ComponentName(this, DaysTogetherWidget::class.java)
        appWidgetManager.getAppWidgetIds(daysWidget).forEach { id ->
            DaysTogetherWidget.updateAppWidget(this, appWidgetManager, id)
        }

        val galleryWidget = ComponentName(this, PhotoGalleryWidget::class.java)
        appWidgetManager.getAppWidgetIds(galleryWidget).forEach { id ->
            PhotoGalleryWidget.updateAppWidget(this, appWidgetManager, id)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val uri = data.data ?: return

            when (requestCode) {
                REQ_AVATAR1 -> {
                    avatar1File = copyUriToInternalFile(uri)
                    setImageView(ivSettingsAvatar1, avatar1File)
                }
                REQ_AVATAR2 -> {
                    avatar2File = copyUriToInternalFile(uri)
                    setImageView(ivSettingsAvatar2, avatar2File)
                }
                REQ_BIG_PHOTO -> {
                    bigPhotoFile = copyUriToInternalFile(uri)
                    setImageView(ivWidgetBigPhoto, bigPhotoFile)
                }
                REQ_SMALL_PHOTO1 -> {
                    smallPhoto1File = copyUriToInternalFile(uri)
                    setImageView(ivWidgetSmallPhoto1, smallPhoto1File)
                }
                REQ_SMALL_PHOTO2 -> {
                    smallPhoto2File = copyUriToInternalFile(uri)
                    setImageView(ivWidgetSmallPhoto2, smallPhoto2File)
                }
                REQ_SMALL_PHOTO3 -> {
                    smallPhoto3File = copyUriToInternalFile(uri)
                    setImageView(ivWidgetSmallPhoto3, smallPhoto3File)
                }
            }
        }
    }
}