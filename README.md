# 💖 Love Widget  
*Android-виджеты для пар: «Дней вместе» и «Мини-галерея»*  

## 🎯 Описание и цели  

**Love Widget** — автономное Android-приложение для пар, созданное с акцентом на локальное хранение и минимализм.  

🔹 **Основные возможности:**  
- 🕒 Отображение **количества дней вместе** с плавным обновлением (в т.ч. в виджете),  
- 📅 Управление **напоминаниями** (годовщины, дни рождения) — даже прошедшие,  
- 📸 Работа с **фотографиями**: аватары, главное фото, мини-галерея (до 4 изображений),  
- 📱 Два **виджета на рабочий стол**:  
  - `Дней вместе` — цифровой таймер в реальном времени (без интернета),  
  - `Мини-галерея` — показ 4 избранных фото с возможностью обновления из приложения.  

🔒 Все данные хранятся **локально** (SharedPreferences / внутренняя память).  
📱 Поддержка Android 7.0+ (API ≥24).  
💡 Приложение **не требует интернета**, аккаунтов или разрешений за пределами `READ_EXTERNAL_STORAGE` (для фото).  

---

## 🛠 Используемые технологии  

| Категория       | Инструменты и библиотеки |
|----------------|--------------------------|
| **Язык**       | Kotlin (100%) |
| **UI/UX**      | View-based (XML), `AppCompat`, `ConstraintLayout`, `RecyclerView` |
| **Хранение**   | `SharedPreferences`, внутреннее хранилище (`Context.filesDir`) |
| **Виджеты**    | `AppWidgetProvider`, `RemoteViews`, `PendingIntent` |
| **Фон**        | `WorkManager` (для ежедневного обновления виджета), `BroadcastReceiver` (обновление при изменении даты) |
| **Фото**       | `Bitmap`, `MediaStore` (для загрузки из галереи), сжатие и кэширование в памяти |
| **Дата/время** | `java.time.LocalDate` (через desugaring для API <26), `ChronoUnit.DAYS` |
| **Тестирование** | `JUnit`, `Espresso` (частично), ручное тестирование на физических устройствах |
| **Сборка**     | Gradle (Kotlin DSL), `minSdk 24`, `targetSdk 34` |

> 📌 В проекте используется **desugaring** (`coreLibraryDesugaring`) для поддержки Java 8+ API (включая `java.time`) на старых версиях Android.

---

## ⚙️ Запуск проекта  

### Требования  
- Android Studio **Giraffe (2023.2.1)** или новее *(лучше Hedgehog 2023.3.1+)*  
- Kotlin **≥1.9.22**  
- Минимальный SDK: `API 24`  
- Язык: Kotlin (все классы на Kotlin), часть ресурсов — на русском (названия файлов!)  

---
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
│   ├── Напоминание.kt
│   ├── ReminderAdapter.kt
│   ├── ReminderManager.kt
│   ├── SettingsActivity.kt
│   ├── TestWidget.kt
│   ├── Utils.kt
│   └── WelcomeActivity.kt
│
├── res/layout/
│   ├── годовщина_деятельности.xml
│   ├── activity_create_reminder.xml
│   ├── activity_date_picker.xml
│   ├── activity_home.xml
│   ├── activity_main.xml
│   ├── activity_note.xml
│   ├── activity_profile_setup.xml
│   ├── activity_settings.xml
│   ├── activity_welcome.xml
│   ├── item_reminder.xml        ← макет элемента списка напоминаний (кнопки "Возобновить"/"Ежегодно")
│   ├── widget_days_together.xml  ← разметка виджета "Дней вместе"
│   ├── widget_photo_gallery.xml  ← разметка виджета "Мини-галерея"
│   └── widget_test.xml
│
├── res/drawable/
│   ├── ... (иконки, фоновые изображения, selector'ы)
│
├── res/xml/
│   ├── days_widget_info.xml     ← конфигурация виджета "Дней вместе"
│   └── photo_widget_info.xml    ← конфигурация виджета "Мини-галерея"
│
└── AndroidManifest.xml
