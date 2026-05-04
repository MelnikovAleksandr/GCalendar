package ru.melnikov.gcalendar.ui.utils

import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import kotlin.jvm.JvmName

@JvmName("filterEventsByDate")
fun ImmutableList<Event>.filterByDate(date: LocalDate): ImmutableList<Event> {
    return this.filter { event ->
        event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
    }.toImmutableList()
}

@JvmName("groupEventsByDate")
fun ImmutableList<Event>.groupByDate(): ImmutableMap<LocalDate, ImmutableList<Event>> {
    return this
        .groupBy { event ->
            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .mapValues { it.value.toImmutableList() }
        .toImmutableMap()
}

@JvmName("filterEventsByDateRange")
fun ImmutableList<Event>.filterByDateRange(
    startDate: LocalDate,
    endDate: LocalDate
): ImmutableList<Event> {
    return this.filter { event ->
        val eventDate = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
        eventDate in startDate..endDate
    }.toImmutableList()
}


@JvmName("filterHolidaysByDate")
fun ImmutableList<Holiday>.filterByDate(date: LocalDate): ImmutableList<Holiday> {
    return this.filter { holiday ->
        holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
    }.toImmutableList()
}


@JvmName("groupHolidaysByDate")
fun ImmutableList<Holiday>.groupByDate(): ImmutableMap<LocalDate, ImmutableList<Holiday>> {
    return this
        .groupBy { holiday ->
            holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
        }
        .mapValues { it.value.toImmutableList() }
        .toImmutableMap()
}

fun Int.toComposeColor(): Color = Color(this)

fun Color.withEventAlpha(): Color = this.copy(alpha = 0.15f)

fun Color.withOverlappingAlpha(): Color = this.copy(alpha = 0.7f)

fun Color.withHighAlpha(): Color = this.copy(alpha = 0.9f)

fun Color.withTextAlpha(): Color = this.copy(alpha = 0.7f)