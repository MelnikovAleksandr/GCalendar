package ru.melnikov.gcalendar.data.store

data class HolidayKey(
    val countryCode: String,
    val year: Int
)

data class EventKey(
    val userId: String,
    val startTime: Long,
    val endTime: Long
)

data class SingleEventKey(
    val eventId: String
)