package ru.melnikov.gcalendar.domain.utils

class EventValidationException(message: String) : IllegalArgumentException(message)

object EventValidator {

    fun validate(
        title: String,
        startTime: Long,
        endTime: Long,
        calendarId: String,
        isAllDay: Boolean = false
    ) {
        validateTitle(title)
        validateTimeRange(startTime, endTime, isAllDay)
        validateCalendarId(calendarId)
    }

    private fun validateTitle(title: String) {
        if (title.isBlank()) {
            throw EventValidationException("Event title cannot be empty")
        }
        if (title.length > 200) {
            throw EventValidationException("Event title cannot exceed 200 characters")
        }
    }

    private fun validateTimeRange(startTime: Long, endTime: Long, isAllDay: Boolean) {
        if (startTime <= 0) {
            throw EventValidationException("Event start time is invalid")
        }
        if (endTime <= 0) {
            throw EventValidationException("Event end time is invalid")
        }
        if (!isAllDay && endTime < startTime) {
            throw EventValidationException("Event end time must be after start time")
        }
        if (isAllDay && endTime < startTime) {
            throw EventValidationException("Event end date must be on or after start date")
        }
    }

    private fun validateCalendarId(calendarId: String) {
        if (calendarId.isBlank()) {
            throw EventValidationException("Calendar ID cannot be empty")
        }
    }
}