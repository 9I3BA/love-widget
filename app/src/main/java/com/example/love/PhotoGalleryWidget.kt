package com.example.love

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.RemoteViews
import java.io.File

class PhotoGalleryWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_photo_gallery)
            val prefs = context.getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)

            // Большое фото из Base64
            prefs.getString("widgetBigPhoto_base64", null)?.let { base64 ->
                try {
                    val bytes = Base64.decode(base64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    if (bitmap != null) {
                        views.setImageViewBitmap(R.id.ivBigPhoto, bitmap)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Маленькие фото
            val ids = intArrayOf(R.id.ivPhoto1, R.id.ivPhoto2, R.id.ivPhoto3)
            for (i in 0..2) {
                prefs.getString("widgetSmallPhoto${i + 1}_base64", null)?.let { base64 ->
                    try {
                        val bytes = Base64.decode(base64, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        if (bitmap != null) {
                            views.setImageViewBitmap(R.id.ivPhoto1 + i, bitmap)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}