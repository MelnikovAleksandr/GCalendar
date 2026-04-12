package ru.melnikov.gcalendar.data.remote

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

@Single
class MockCalendarApiService : CalendarApiService {
    override suspend fun fetchCalendarsForUser(userId: String): List<Calendar> {
        val colors = listOf(0xFF4285F4, 0xFFDB4437, 0xFF0F9D58, 0xFFF4B400, 0xFF8560A8, 0xFF03BCD4)
        return listOf(
            Calendar(
                id = "${userId}_primary",
                name = "My Calendar",
                color = colors[0].toInt(),
                userId = userId,
                isPrimary = true
            ),
            Calendar(
                id = "${userId}_work",
                name = "Work",
                color = colors[1].toInt(),
                userId = userId
            ),
            Calendar(
                id = "${userId}_family",
                name = "Family",
                color = colors[2].toInt(),
                userId = userId
            )
        )
    }

    override suspend fun fetchEventsForCalendar(
        calendarId: String,
        startTime: Long,
        endTime: Long
    ): List<Event> {
        val events = mutableListOf<Event>()
        if (calendarId.contains("work")) {
            val startInstant = Instant.fromEpochMilliseconds(startTime)
            val endInstant = Instant.fromEpochMilliseconds(endTime)
            val timeZone = TimeZone.currentSystemDefault()
            var currentDate = startInstant.toLocalDateTime(timeZone).date
            val endDate = endInstant.toLocalDateTime(timeZone).date
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                if (dayOfWeek.ordinal in 1..5) {
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.day,
                        11, 30, 0, 0
                    )
                    val eventStartTimeMillis =
                        eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds
                    events.add(
                        Event(
                            id = "standup_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "Standup",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Tech Park"
                        )
                    )
                }
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
            currentDate = startInstant.toLocalDateTime(timeZone).date
            var biweeklyCounter = 0
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                if (dayOfWeek == DayOfWeek.THURSDAY && biweeklyCounter % 2 == 0) {
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.day,
                        14, 30, 0, 0
                    )
                    val eventStartTimeMillis =
                        eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds
                    events.add(
                        Event(
                            id = "automation_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "App Automation biweekly",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Tech Park 3rd Floor"
                        )
                    )
                }
                if (dayOfWeek == DayOfWeek.SUNDAY) {
                    biweeklyCounter++
                }
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
            currentDate = startInstant.toLocalDateTime(timeZone).date
            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                if (dayOfWeek == DayOfWeek.WEDNESDAY) {
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.day,
                        16, 0, 0, 0
                    )
                    val eventStartTimeMillis =
                        eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    val eventEndTimeMillis = eventStartTimeMillis + 50.minutes.inWholeMilliseconds
                    events.add(
                        Event(
                            id = "pitch_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "Product Pitch reviews [Candle 3rd floor]",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Tech Park 3rd Floor-3rd Floor-Candle (20)"
                        )
                    )
                }
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
            currentDate = startInstant.toLocalDateTime(timeZone).date

            while (currentDate <= endDate) {
                val dayOfWeek = currentDate.dayOfWeek
                if (dayOfWeek == DayOfWeek.FRIDAY) {
                    val eventStartDateTime = LocalDateTime(
                        currentDate.year,
                        currentDate.month,
                        currentDate.day,
                        16, 30, 0, 0
                    )
                    val eventStartTimeMillis =
                        eventStartDateTime.toInstant(timeZone).toEpochMilliseconds()
                    val eventEndTimeMillis = eventStartTimeMillis + 30.minutes.inWholeMilliseconds
                    events.add(
                        Event(
                            id = "fun_${eventStartTimeMillis}",
                            calendarId = calendarId,
                            title = "Fun fridays",
                            startTime = eventStartTimeMillis,
                            endTime = eventEndTimeMillis,
                            location = "Tech Park"
                        )
                    )
                }
                currentDate = currentDate.plus(DatePeriod(days = 1))
            }
        }

        return events
    }

    override suspend fun fetchHolidays(countryCode: String, year: Int): List<Holiday> {
        val holidays = mutableListOf<Holiday>()
        val timeZone = TimeZone.currentSystemDefault()

        if (countryCode == "RU") {
            val loveDay = LocalDateTime(year, Month.FEBRUARY, 14, 0, 0)
            holidays.add(
                Holiday(
                    id = "loveDay_$year",
                    name = "Love Day",
                    date = loveDay.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "RU"
                )
            )
            val mensDay = LocalDateTime(year, Month.FEBRUARY, 23, 0, 0)
            holidays.add(
                Holiday(
                    id = "mensDay_$year",
                    name = "Mens Day",
                    date = mensDay.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "RU"
                )
            )
            val womenDay = LocalDateTime(year, Month.MARCH, 8, 0, 0)
            holidays.add(
                Holiday(
                    id = "womenDay_$year",
                    name = "Women Day",
                    date = womenDay.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "RU"
                )
            )
            val newYear = LocalDateTime(year, Month.JANUARY, 1, 0, 0)
            holidays.add(
                Holiday(
                    id = "newYear_$year",
                    name = "New Year",
                    date = newYear.toInstant(timeZone).toEpochMilliseconds(),
                    countryCode = "RU"
                )
            )
        }

        return holidays
    }
}