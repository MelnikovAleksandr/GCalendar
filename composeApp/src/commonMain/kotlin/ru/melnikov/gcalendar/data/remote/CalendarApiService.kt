package ru.melnikov.gcalendar.data.remote

import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday

interface CalendarApiService {
    suspend fun fetchCalendarsForUser(userId: String): List<Calendar>
    suspend fun fetchEventsForCalendar(calendarId: String, startTime: Long, endTime: Long): List<Event>
    suspend fun fetchHolidays(countryCode: String, year: Int): List<Holiday>
}