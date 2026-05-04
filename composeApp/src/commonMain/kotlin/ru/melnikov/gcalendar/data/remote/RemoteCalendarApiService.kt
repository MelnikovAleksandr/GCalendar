package ru.melnikov.gcalendar.data.remote

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.data.remote.error.DataError
import ru.melnikov.gcalendar.data.remote.model.CalendarResponseItem
import ru.melnikov.gcalendar.data.remote.model.EventResponseItem

@Single
class RemoteCalendarApiService(client: HttpClient, json: Json) {
    private val clientWrapper = ClientWrapper(client, json)
    private val baseUrl = "https://raw.githubusercontent.com/MelnikovAleksandr/GCalendar/develop/"
    suspend fun fetchCalendarsForUser(userId: String): Result<List<CalendarResponseItem>,
            DataError> {
        return clientWrapper.networkGetUseCase<List<CalendarResponseItem>>(
            baseUrl + "assets/calendars.json",
            mapOf(
                "user_id" to userId
            )
        )
    }

    suspend fun fetchEventsForCalendar(
        calendarIds: List<String>,
        startTime: Long,
        endTime: Long
    ): Result<List<EventResponseItem>, DataError> {
        return clientWrapper.networkGetUseCase<List<EventResponseItem>>(
            baseUrl + "assets/events.json",
            mapOf(
                "calendar_ids" to calendarIds.toString(),
                "start_time" to startTime.toString(),
                "end_time" to endTime.toString()
            )
        )
    }
}