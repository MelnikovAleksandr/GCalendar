package ru.melnikov.gcalendar.ui.state

import kotlinx.datetime.LocalDate
import ru.melnikov.gcalendar.common.YearMonth

data class DateState(
    val currentDate: LocalDate,
    val selectedDate: LocalDate,
    val selectedInViewMonth: YearMonth,
)