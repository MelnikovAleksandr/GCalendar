package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.melnikov.gcalendar.domain.model.Calendar

interface ICalendarRepository {
    suspend fun refreshCalendarsForUser(userId: String)

    fun getCalendarsForUser(userId: String): Flow<List<Calendar>>

    suspend fun upsertCalendar(calendars: List<Calendar>)

    suspend fun deleteCalendar(calendar: Calendar)
}