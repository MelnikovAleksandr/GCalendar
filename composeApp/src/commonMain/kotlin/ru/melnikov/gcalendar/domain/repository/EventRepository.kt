package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.data.local.EventDao
import ru.melnikov.gcalendar.data.model.EventEntity
import ru.melnikov.gcalendar.data.model.EventReminderEntity
import ru.melnikov.gcalendar.domain.model.Event

@Single
class EventRepository(private val eventDao: EventDao) {
    fun getEventsForCalendarsInRange(calendarIds: List<String>, start: Long, end: Long): Flow<List<Event>> =
        eventDao.getEventsBetweenDates(calendarIds, start, end).map { entities -> entities.map { it.toEvent() } }

    suspend fun addEvent(event: Event) {
        val eventEntity = event.toEntity()
        val reminderEntities = event.reminderMinutes.map { minutes ->
            EventReminderEntity(
                event.id,
                minutes
            )
        }
        eventDao.insertEventWithReminders(eventEntity, reminderEntities)
    }

    suspend fun updateEvent(event: Event) {
        val eventEntity = event.toEntity()
        eventDao.upsertEvent(eventEntity)

        eventDao.deleteEventReminders(event.id)
        val reminderEntities = event.reminderMinutes.map { minutes ->
            EventReminderEntity(event.id, minutes)
        }
        reminderEntities.forEach { reminder ->
            eventDao.insertEventReminder(reminder)
        }
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.deleteEvent(event.toEntity())
    }

    private fun EventEntity.toEvent(): Event {
        return Event(
            id = id,
            calendarId = calendarId,
            title = title,
            description = description,
            location = location,
            startTime = startTime,
            endTime = endTime,
            isAllDay = isAllDay,
            isRecurring = isRecurring,
            recurringRule = recurringRule,
            reminderMinutes = emptyList(),
            color = color
        )
    }

    private fun Event.toEntity(): EventEntity =
        EventEntity(
            id = id,
            calendarId = calendarId,
            title = title,
            description = description,
            location = location,
            startTime = startTime,
            endTime = endTime,
            isAllDay = isAllDay,
            isRecurring = isRecurring,
            recurringRule = recurringRule,
            color = color
        )
}