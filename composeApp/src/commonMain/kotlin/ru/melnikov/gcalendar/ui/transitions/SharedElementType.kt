package ru.melnikov.gcalendar.ui.transitions

import kotlinx.datetime.LocalDate

enum class SharedElementType {
    DateCell,
    DayHeader,
    DayColumn,
    EventCard,
    EventBackground,
    TimeColumn,
    EventTitle,
    EventColorIndicator
}

data class DateSharedElementKey(
    val date: LocalDate,
    val type: SharedElementType
)

data class EventSharedElementKey(
    val eventId: String,
    val type: SharedElementType
)

data class TimeColumnSharedElementKey(
    val type: SharedElementType = SharedElementType.TimeColumn
)