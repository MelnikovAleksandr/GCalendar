@file:OptIn(ExperimentalTime::class)
package ru.melnikov.gcalendar.domain.states

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.model.User
import ru.melnikov.gcalendar.ui.TopBarCalendarView
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

data class CalendarUiState(
    val selectedDay: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date,
    val showMonthDropdown: TopBarCalendarView = TopBarCalendarView.NoView,
    val accounts: List<User> = emptyList(),
    val calendars: List<Calendar> = emptyList(),
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList(),
    val upcomingEvents: List<Event> = getUpcomingEvents(events, selectedDay),
    val showAddEventDialog: Boolean = false,
    val selectedEvent: Event? = null
) {
    companion object {
        internal fun getUpcomingEvents(events: List<Event>, fromDate: LocalDate): List<Event> {
            val fromInstant = fromDate.atStartOfDayIn(TimeZone.currentSystemDefault())
            val toInstant = fromDate.plus(DatePeriod(days = 30)).atStartOfDayIn(
                TimeZone
                    .currentSystemDefault()
            )

            return events
                .filter {
                    it.startTime >= fromInstant.toEpochMilliseconds() && it.startTime <= toInstant.toEpochMilliseconds()
                }
                .sortedBy { it.startTime }

        }
    }
}