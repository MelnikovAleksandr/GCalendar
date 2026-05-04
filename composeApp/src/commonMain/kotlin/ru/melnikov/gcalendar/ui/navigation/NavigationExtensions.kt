package ru.melnikov.gcalendar.ui.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

fun <T : NavKey> NavBackStack<T>.replaceLast(destination: T) {
    val currentView = lastOrNull()
    if (currentView != destination) {
        if (isNotEmpty()) {
            removeLastOrNull()
        }
        add(destination)
    }
}

fun <T : NavKey> NavBackStack<T>.popIfPossible(): Boolean {
    return if (isNotEmpty()) {
        removeLastOrNull()
        true
    } else {
        false
    }
}