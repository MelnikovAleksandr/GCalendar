package ru.melnikov.gcalendar.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color


object GCalendarColors {
    val holiday = Color(0xFF007F73)
    val holidayContainer = Color(0xFF007F73).copy(alpha = 0.1f)
    val onHoliday = Color.White
    val scheduleHoliday = Color(0xFF4CAF50)
    val scheduleHolidayContainer = Color(0xFF4CAF50).copy(alpha = 0.15f)
    val eventDot = Color(0xFF2196F3)
    val gridLine = Color(0xFFE0E0E0)
    val currentTimeLine = Color(0xFFEA4335)
    val todayBackground = Color(0xFF4285F4)
    val onToday = Color.White
    val weekendText = Color(0xFF5F6368).copy(alpha = 0.7f)
}

val GCalendarTheme.extendedColors: GCalendarColors
    @Composable @ReadOnlyComposable
    get() = GCalendarColors