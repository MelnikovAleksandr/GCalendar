package ru.melnikov.gcalendar.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.mp.KoinPlatform
import ru.melnikov.gcalendar.data.local.AppDatabase
import ru.melnikov.gcalendar.data.local.DATABASE_NAME

actual fun getDatabase(): AppDatabase {
    val context = KoinPlatform.getKoin().get<Context>()
    return Room.databaseBuilder<AppDatabase>(
        context,
        context.getDatabasePath(DATABASE_NAME).absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .addMigrations(*AppDatabase.MIGRATIONS)
        .build()
}