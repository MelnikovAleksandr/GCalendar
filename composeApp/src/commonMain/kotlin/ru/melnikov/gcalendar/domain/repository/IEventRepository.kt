package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.melnikov.gcalendar.domain.model.Event

interface IEventRepository {
    suspend fun syncEventsForCalendar(
        calendarIds: List<String>,
        startTime: Long,
        endTime: Long,
    )

    fun getEventsForCalendarsInRange(
        userId: String,
        start: Long,
        end: Long,
    ): Flow<List<Event>>

    suspend fun addEvent(event: Event)

    suspend fun updateEvent(event: Event)

    suspend fun deleteEvent(event: Event)
}