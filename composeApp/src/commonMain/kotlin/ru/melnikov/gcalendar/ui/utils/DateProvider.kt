package ru.melnikov.gcalendar.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun rememberToday(): LocalDate = remember {
    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

@OptIn(ExperimentalTime::class)
fun today(): LocalDate {
    return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
}

@OptIn(ExperimentalTime::class)
fun LocalDate.isToday(): Boolean {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    return this == today
}