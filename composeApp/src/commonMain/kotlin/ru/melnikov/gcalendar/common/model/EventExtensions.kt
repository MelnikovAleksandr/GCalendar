package ru.melnikov.gcalendar.common.model

import ru.melnikov.gcalendar.common.convertStringToColor
import ru.melnikov.gcalendar.data.local.model.EventEntity
import ru.melnikov.gcalendar.data.local.model.EventWithReminders
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
        calendarName = calendarName ?: "",
        color = convertStringToColor(calendarId + calendarName)
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
        color = convertStringToColor(calendarId + calendarName)
    )
}

fun EventWithReminders.asEvent(): Event {
    return Event(
        id = event.id,
        calendarId = event.calendarId,
        title = event.title,
        description = event.description,
        location = event.location,
        startTime = event.startTime,
        endTime = event.endTime,
        isAllDay = event.isAllDay,
        isRecurring = event.isRecurring,
        recurringRule = event.recurringRule,
        reminderMinutes = reminders.map { it.minutes },
        calendarName = event.calendarName,
        color = convertStringToColor(event.calendarId + event.calendarName)
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