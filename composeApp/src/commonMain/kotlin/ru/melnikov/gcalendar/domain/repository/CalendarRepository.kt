package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.common.model.asCalendar
import ru.melnikov.gcalendar.common.model.asCalendarEntity
import ru.melnikov.gcalendar.data.local.CalendarDao
import ru.melnikov.gcalendar.data.remote.RemoteCalendarApiService
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.data.remote.Result

@Single(binds = [ICalendarRepository::class])
class CalendarRepository(
    private val calendarDao: CalendarDao,
    private val apiService: RemoteCalendarApiService,
) : BaseRepository(), ICalendarRepository {

    override suspend fun refreshCalendarsForUser(userId: String) = safeCallOrThrow("refreshCalendarsForUser($userId)") {
        when (val apiCalendars = apiService.fetchCalendarsForUser(userId)) {
            is Result.Error -> {
                throw RepositoryException("Failed to fetch calendars: ${apiCalendars.error}")
            }
            is Result.Success -> {
                val calendars = apiCalendars.data.map { it.asCalendar() }
                upsertCalendar(calendars)
            }
        }
    }

    override fun getCalendarsForUser(userId: String): Flow<List<Calendar>> =
        safeFlow(
            flowName = "getCalendarsForUser($userId)",
            defaultValue = emptyList(),
            flow = calendarDao
                .getCalendarsByUserId(userId)
                .map { entities -> entities.map { it.asCalendar() } }
        )

    override suspend fun upsertCalendar(calendars: List<Calendar>) =
        safeCallOrThrow("upsertCalendar(${calendars.size} calendars)") {
            calendarDao.upsertCalendar(calendars.map { it.asCalendarEntity() })
        }

    override suspend fun deleteCalendar(calendar: Calendar) =
        safeCallOrThrow("deleteCalendar(${calendar.id})") {
            calendarDao.deleteCalendar(calendar.asCalendarEntity())
        }
}