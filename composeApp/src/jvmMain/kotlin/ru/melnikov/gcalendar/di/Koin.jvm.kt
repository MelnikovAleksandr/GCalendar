package ru.melnikov.gcalendar.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import ru.melnikov.gcalendar.data.local.AppDatabase
import java.io.File

actual fun getDatabase(): AppDatabase {
    val os = System.getProperty("os.name").lowercase()
    val userHome = System.getProperty("user.home")
    val appDataDir =
        when {
            os.contains("win") -> File(System.getenv("APPDATA"), "GCalendar")
            os.contains("mac") -> File(userHome, "Library/Application Support/GCalendar")
            else -> File(userHome, ".local/share/GCalendar")
        }

    if (!appDataDir.exists()) {
        appDataDir.mkdirs()
    }

    val dbFile = File(appDataDir, "calendar.db")
    return Room
        .databaseBuilder<AppDatabase>(dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.Default)
        .build()
}