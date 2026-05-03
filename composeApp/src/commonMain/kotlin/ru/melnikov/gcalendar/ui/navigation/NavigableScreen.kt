package ru.melnikov.gcalendar.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

sealed interface NavigableScreen : NavKey {
    @Serializable
    data object Schedule : NavigableScreen

    @Serializable
    data object Day : NavigableScreen

    @Serializable
    data object ThreeDay : NavigableScreen

    @Serializable
    data object Week : NavigableScreen

    @Serializable
    data object Month : NavigableScreen
}