package ru.melnikov.gcalendar.ui

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.model.User
import kotlin.time.Clock

data class CalendarUiState(
    val currentDate: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedDay: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val selectedMonth: YearMonth = YearMonth(
        selectedDay.year,
        selectedDay.month
    ),

    val currentView: CalendarView = CalendarView.Month,
    val showMonthDropdown: Boolean = false,

    val accounts: List<User> = emptyList(),
    val calendars: List<Calendar> = emptyList(),
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList(),

    val weekStartDate: LocalDate = getWeekStartDate(selectedDay),
    val threeDayStartDate: LocalDate = selectedDay,
    val upcomingEvents: List<Event> = getUpcomingEvents(events, selectedDay),

    val showAddEventDialog: Boolean = false,
    val selectedEvent: Event? = null
) {
    companion object {
        internal fun getWeekStartDate(date: LocalDate): LocalDate {
            val dayOfWeek = date.dayOfWeek.ordinal % 7
            return date.minus(DatePeriod(days = dayOfWeek))
        }

        internal fun getUpcomingEvents(events: List<Event>, fromDate: LocalDate): List<Event> {
            val fromInstant = fromDate.atStartOfDayIn(TimeZone.currentSystemDefault())
            val toInstant = fromDate.plus(DatePeriod(days = 30)).atStartOfDayIn(TimeZone
                .currentSystemDefault())

            return events
                .filter { it.startTime >= fromInstant.toEpochMilliseconds() && it.startTime <= toInstant.toEpochMilliseconds() }
                .sortedBy { it.startTime }

        }
    }
}