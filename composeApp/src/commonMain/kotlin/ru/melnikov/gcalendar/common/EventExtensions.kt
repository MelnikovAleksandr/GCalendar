package ru.melnikov.gcalendar.common

import ru.melnikov.gcalendar.data.local.model.EventEntity
import ru.melnikov.gcalendar.data.remote.model.EventResponseItem
import ru.melnikov.gcalendar.domain.model.Event

fun EventResponseItem.asEvent(): Event {
    return Event(
        id = id,
        title = title,
        description = description,
        location = location,
        startTime = startTime,
        endTime = endTime,
        isAllDay = isAllDay,
        isRecurring = isRecurring,
        recurringRule = recurringRule,
        reminderMinutes = reminderMinutes,
        calendarId = calendarId,
        calendarName = calenderName,
        color = stringToColor(calendarId)
    )
}

fun EventEntity.asEvent(): Event {
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
        calendarName = calendarName,
        color = stringToColor(calendarId)
    )
}

fun Event.asEntity(): EventEntity =
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
        calendarName = calendarName,
        recurringRule = recurringRule,
    )