package com.example.love

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.net.Uri
import android.widget.RemoteViews
import java.io.File

class PhotoGalleryWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_photo_gallery)
            val prefs = context.getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)

            // Загружаем до 3 фото по URI
            for (i in 0..2) {
                val uriString = prefs.getString("widgetSmallPhoto${i + 1}_uri", null)
                if (uriString != null) {
                    try {
                        val uri = Uri.parse(uriString)
                        views.setImageViewUri(R.id.ivPhoto1 + i, uri)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}