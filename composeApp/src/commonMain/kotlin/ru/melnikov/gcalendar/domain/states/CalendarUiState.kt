package ru.melnikov.gcalendar.domain.states

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.model.User

data class CalendarUiState(
    val accounts: ImmutableList<User> = persistentListOf(),
    val calendars: ImmutableList<Calendar> = persistentListOf(),
    val events: ImmutableList<Event> = persistentListOf(),
    val holidays: ImmutableList<Holiday> = persistentListOf(),
    val selectedEvent: Event? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) {
    val hasError: Boolean get() = errorMessage != null
    val isEmpty: Boolean get() = !isLoading && accounts.isEmpty() && calendars.isEmpty() && events.isEmpty()
}