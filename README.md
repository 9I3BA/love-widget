# 💖 Love Widget
## 🎯 Описание и цели

**Love Widget** — это личное Android-приложение, помогающее паре сохранять память о важных моментах:

- ✅ Отслеживать, **сколько дней вы вместе** — с естественным форматированием,  
- ✅ Управлять **памятными датами** (годовщины, дни рождения) — даже если они уже прошли,  
- ✅ Сохранять **фото и имена** — аватары, главное фото, мини-галерея,  
- ✅ Добавлять **два виджета на рабочий стол**:  
  - 🕒 *«Дней вместе»* — живой таймер,  
  - 📸 *«Мини-галерея»* — четыре любимых фото.

Приложение **работает автономно**, без интернета и облачных сервисов. Все данные хранятся локально на устройстве.

---

## ⚙️ Инструкция по запуску

### Требования
- Android 7.0+ (API 24 и выше)  
- Android Studio Giraffe (2022.3.1) или новее  
- Kotlin 1.9+

---

Структура проекта:
app/
├── src/main/java/com/example/love/
│   ├── AnniversaryActivity.kt
│   ├── CreateReminderActivity.kt
│   ├── DailyReminderWorker.kt
│   ├── DatePickerActivity.kt
│   ├── DaysTogetherWidget.kt
│   ├── HomeActivity.kt
│   ├── MainActivity.kt
│   ├── NoteActivity.kt
│   ├── PhotoAdapter.kt
│   ├── PhotoGalleryWidget.kt
│   ├── ProfileSetupActivity.kt
│   ├── Reminder.kt
│   ├── ReminderAdapter.kt
│   ├── ReminderManager.kt
│   ├── SettingsActivity.kt
│   ├── TestWidget.kt
│   ├── Utils.kt
│   └── WelcomeActivity.kt
│
├── res/layout/
│   ├── activity_anniversary.xml
│   ├── activity_create_reminder.xml
│   ├── activity_date_picker.xml
│   ├── activity_home.xml
│   ├── activity_main.xml
│   ├── activity_note.xml
│   ├── activity_profile_setup.xml
│   ├── activity_settings.xml
│   ├── activity_welcome.xml
│   ├── item_reminder.xml             ← ОБЯЗАТЕЛЕН для кнопок "Возобновить"/"Ежегодно"
│   ├── widget_days_together.xml     ← XML-разметка виджета "Дней вместе"
│   ├── widget_photo_gallery.xml     ← XML-разметка виджета "Мини-галерея"
│   └── widget_test.xml
│
├── res/drawable/
│   ├── ... (иконки, фоновые изображения)
│
├── res/xml/
│   ├── days_widget_info.xml         ← конфиг виджета "Дней вместе"
│   └── photo_widget_info.xml        ← конфиг виджета "Мини-галерея"
│
└── AndroidManifest.xml
