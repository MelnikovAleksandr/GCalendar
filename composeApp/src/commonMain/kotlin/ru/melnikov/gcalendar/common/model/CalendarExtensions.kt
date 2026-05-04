package ru.melnikov.gcalendar.common.model

import ru.melnikov.gcalendar.common.convertStringToColor
import ru.melnikov.gcalendar.data.local.model.CalendarEntity
import ru.melnikov.gcalendar.data.remote.model.CalendarResponseItem
import ru.melnikov.gcalendar.domain.model.Calendar


fun CalendarResponseItem.asCalendar(): Calendar {
    return Calendar(
        id = id,
        name = name,
        color = convertStringToColor(id + name),
        isVisible = isVisible,
        isPrimary = isPrimary,
        userId = userId
    )
}

fun CalendarEntity.asCalendar(): Calendar =
    Calendar(id, name, color, userId, isVisible, isPrimary)

fun Calendar.asCalendarEntity(): CalendarEntity =
    CalendarEntity(id, name, color, userId, isVisible, isPrimary)