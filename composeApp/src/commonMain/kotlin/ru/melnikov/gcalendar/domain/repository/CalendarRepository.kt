package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.data.local.CalendarDao
import ru.melnikov.gcalendar.data.local.model.CalendarEntity
import ru.melnikov.gcalendar.domain.model.Calendar

@Single
class CalendarRepository(private val calendarDao: CalendarDao) {
    fun getCalendarsForUser(userId: String): Flow<List<Calendar>> =
        calendarDao.getCalendarsByUserId(userId).map { entities -> entities.map { it.toCalendar() } }

    suspend fun upsertCalendar(calendar: Calendar) {
        calendarDao.upsertCalendar(calendar.toEntity())
    }

    suspend fun deleteCalendar(calendar: Calendar) {
        calendarDao.deleteCalendar(calendar.toEntity())
    }

    private fun CalendarEntity.toCalendar(): Calendar =
        Calendar(id, name, color, userId, isVisible, isPrimary)

    private fun Calendar.toEntity(): CalendarEntity =
        CalendarEntity(id, name, color, userId, isVisible, isPrimary)
}