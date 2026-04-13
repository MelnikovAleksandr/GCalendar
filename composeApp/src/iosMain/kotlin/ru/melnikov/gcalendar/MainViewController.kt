package ru.melnikov.gcalendar

import androidx.compose.ui.window.ComposeUIViewController
import ru.melnikov.gcalendar.di.initKoin

fun MainViewController() = ComposeUIViewController {
    initKoin()
    App()
}