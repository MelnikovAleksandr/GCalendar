package ru.melnikov.gcalendar.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.mp.KoinPlatform
import ru.melnikov.gcalendar.data.local.AppDatabase

actual fun getDatabase(): AppDatabase {
    val context = KoinPlatform.getKoin().get<Context>()
    return Room.databaseBuilder<AppDatabase>(
        context,
        context.getDatabasePath("calendar.db").absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}