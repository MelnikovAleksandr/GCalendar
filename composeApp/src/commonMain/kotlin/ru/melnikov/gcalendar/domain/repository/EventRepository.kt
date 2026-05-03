package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.common.asEntity
import ru.melnikov.gcalendar.common.asEvent
import ru.melnikov.gcalendar.data.local.EventDao
import ru.melnikov.gcalendar.data.local.model.EventReminderEntity
import ru.melnikov.gcalendar.data.remote.RemoteCalendarApiService
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.data.remote.Result

@Single
class EventRepository(
    private val eventDao: EventDao,
    private val apiService: RemoteCalendarApiService
) {
    suspend fun getEventsForCalendar(calendarIds: List<String>, startTime: Long, endTime: Long) {
        when (val apiEvents = apiService.fetchEventsForCalendar(calendarIds, startTime, endTime)) {
            is Result.Error -> {
                println("Error getEventsForCalendar " + apiEvents.error.toString())
            }

            is Result.Success -> {
                val events = apiEvents.data.map { it.asEvent() }
                events.forEach { event ->
                    addEvent(event)
                }
            }
        }
    }

    fun getEventsForCalendarsInRange(
        userId: String,
        start: Long,
        end: Long
    ): Flow<List<Event>> =
        eventDao.getEventsBetweenDates(userId, start, end).map { entities ->
            entities.map { it.asEvent() }
        }

    suspend fun addEvent(event: Event) {
        val eventEntity = event.asEntity()
        val reminderEntities = event.reminderMinutes.map { minutes ->
            EventReminderEntity(
                event.id,
                minutes
            )
        }
        eventDao.insertEventWithReminders(eventEntity, reminderEntities)
    }

    suspend fun updateEvent(event: Event) {
        val eventEntity = event.asEntity()
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
        eventDao.deleteEvent(event.asEntity())
    }
}