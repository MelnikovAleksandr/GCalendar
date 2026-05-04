package ru.melnikov.gcalendar

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.melnikov.gcalendar.di.initKoin

fun main() {
    initKoin()
    application {
        System.setProperty("skiko.renderApi", "SOFTWARE")
        Window(
            onCloseRequest = ::exitApplication,
            title = "XCalendar",
        ) {
            CalendarApp()
        }
    }
}