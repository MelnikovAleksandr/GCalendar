package ru.melnikov.gcalendar

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import ru.melnikov.gcalendar.di.initKoin

class GCalendarApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@GCalendarApp)
            androidLogger()
        }
    }

}