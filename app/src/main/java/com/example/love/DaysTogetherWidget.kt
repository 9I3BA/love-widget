package com.example.love

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.RemoteViews

class DaysTogetherWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (id in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, id)
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            println("🔄 Обновление виджета ID=$appWidgetId")
            val prefs = context.getSharedPreferences("LoveWidget", Context.MODE_PRIVATE)

            // Дни вместе
            val startDate = prefs.getLong("startDate", 0L)
            val days = if (startDate > 0) {
                ((System.currentTimeMillis() - startDate) / (1000 * 60 * 60 * 24)).toInt()
            } else {
                0
            }

            val views = RemoteViews(context.packageName, R.layout.widget_days_together)
            views.setTextViewText(R.id.tvDays, "$days")

            // Аватар 1 из Base64
            val base64_1 = prefs.getString("avatar1_base64", null)
            if (base64_1 != null) {
                try {
                    val bytes = Base64.decode(base64_1, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    views.setImageViewBitmap(R.id.ivAvatar1, bitmap)
                } catch (e: Exception) {
                    println("❌ Ошибка загрузки аватара 1 из Base64: ${e.message}")
                    views.setImageViewResource(R.id.ivAvatar1, R.drawable.default_avatar)
                }
            } else {
                views.setImageViewResource(R.id.ivAvatar1, R.drawable.default_avatar)
            }

            // Аватар 2 из Base64
            val base64_2 = prefs.getString("avatar2_base64", null)
            if (base64_2 != null) {
                try {
                    val bytes = Base64.decode(base64_2, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    views.setImageViewBitmap(R.id.ivAvatar2, bitmap)
                } catch (e: Exception) {
                    println("❌ Ошибка загрузки аватара 2 из Base64: ${e.message}")
                    views.setImageViewResource(R.id.ivAvatar2, R.drawable.default_avatar)
                }
            } else {
                views.setImageViewResource(R.id.ivAvatar2, R.drawable.default_avatar)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}