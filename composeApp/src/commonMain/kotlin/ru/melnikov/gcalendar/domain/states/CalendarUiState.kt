package ru.melnikov.gcalendar.domain.states

import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.model.User

data class CalendarUiState(
    val accounts: List<User> = emptyList(),
    val calendars: List<Calendar> = emptyList(),
    val events: List<Event> = emptyList(),
    val holidays: List<Holiday> = emptyList(),
    val selectedEvent: Event? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val hasError: Boolean get() = errorMessage != null
    val isEmpty: Boolean get() = !isLoading && accounts.isEmpty() && calendars.isEmpty() && events.isEmpty()
}