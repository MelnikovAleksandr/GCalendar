package ru.melnikov.gcalendar

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.melnikov.gcalendar.di.initKoin

fun main() {
    initKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "XCalendar",
        ) {
            CalendarApp()
        }
    }
}