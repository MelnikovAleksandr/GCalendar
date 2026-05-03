package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.common.asCalendar
import ru.melnikov.gcalendar.common.asCalendarEntity
import ru.melnikov.gcalendar.data.local.CalendarDao
import ru.melnikov.gcalendar.data.remote.RemoteCalendarApiService
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.data.remote.Result

@Single
class CalendarRepository(
    private val calendarDao: CalendarDao,
    private val apiService: RemoteCalendarApiService
) {

    suspend fun getCalendersForUser(userId: String){
        when(val apiCalendars = apiService.fetchCalendarsForUser(userId)){
            is Result.Error -> {
                println("Error getCalendersForUser " + apiCalendars.error.toString())
            }
            is Result.Success -> {
                println("Success getCalendersForUser $apiCalendars")
                val calendars = apiCalendars.data.map { it.asCalendar() }
                upsertCalendar(calendars)
            }
        }
    }
    fun getCalendarsForUser(userId: String): Flow<List<Calendar>> =
        calendarDao.getCalendarsByUserId(userId)
            .map { entities -> entities.map { it.asCalendar() } }

    suspend fun upsertCalendar(calendars: List<Calendar>) {
        calendarDao.upsertCalendar(calendars.map { it.asCalendarEntity() })
    }

    suspend fun deleteCalendar(calendar: Calendar) {
        calendarDao.deleteCalendar(calendar.asCalendarEntity())
    }
}