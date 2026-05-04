package ru.melnikov.gcalendar.ui.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event

object DateTimeFormatter {

    fun formatTime(dateTime: LocalDateTime): String {
        val hour = when {
            dateTime.hour == 0 -> 12
            dateTime.hour > 12 -> dateTime.hour - 12
            else -> dateTime.hour
        }
        val minute = dateTime.minute.toString().padStart(2, '0')
        val amPm = if (dateTime.hour >= 12) "PM" else "AM"
        return "$hour:$minute $amPm"
    }

    fun formatTimeRange(start: LocalDateTime, end: LocalDateTime): String {
        return "${formatTime(start)} – ${formatTime(end)}"
    }

    fun formatCompactTimeRange(start: LocalDateTime, end: LocalDateTime): String {
        val startHour = when {
            start.hour == 0 -> 12
            start.hour > 12 -> start.hour - 12
            else -> start.hour
        }
        val endHour = when {
            end.hour == 0 -> 12
            end.hour > 12 -> end.hour - 12
            else -> end.hour
        }
        val startMinute = if (start.minute == 0) "" else ":${start.minute.toString().padStart(2, '0')}"
        val endMinute = if (end.minute == 0) "" else ":${end.minute.toString().padStart(2, '0')}"

        return when {
            (start.hour < 12 && end.hour < 12) || (start.hour >= 12 && end.hour >= 12) -> {
                val amPm = if (end.hour >= 12) "PM" else "AM"
                "$startHour$startMinute-$endHour$endMinute$amPm"
            }
            else -> {
                val startAmPm = if (start.hour >= 12) "PM" else "AM"
                val endAmPm = if (end.hour >= 12) "PM" else "AM"
                "$startHour$startMinute$startAmPm-$endHour$endMinute$endAmPm"
            }
        }
    }

    fun formatEventSubheading(event: Event): String {
        val startDateTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
        val endDateTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

        val dayOfWeek = startDateTime.date.dayOfWeek.name
            .lowercase()
            .replaceFirstChar { it.titlecase() }

        val day = startDateTime.date.day
        val month = startDateTime.date.month.name
            .take(3)
            .lowercase()
            .replaceFirstChar { it.titlecase() }

        val mainLine = if (event.isAllDay) {
            "$dayOfWeek, $day $month"
        } else {
            val timeRange = formatCompactTimeRange(startDateTime, endDateTime)
            "$dayOfWeek, $day $month $timeRange"
        }

        return if (event.isRecurring && !event.recurringRule.isNullOrBlank()) {
            val recurringText = when {
                event.recurringRule.contains("WEEKLY", ignoreCase = true) -> "Repeat every week"
                event.recurringRule.contains("DAILY", ignoreCase = true) -> "Repeat every day"
                event.recurringRule.contains("MONTHLY", ignoreCase = true) -> "Repeat every month"
                event.recurringRule.contains("YEARLY", ignoreCase = true) -> "Repeat every year"
                else -> "Recurring event"
            }
            "$mainLine\n$recurringText"
        } else {
            mainLine
        }
    }

    fun formatHour(hour: Int): String {
        val displayHour = when {
            hour == 0 || hour == 12 -> "12"
            hour > 12 -> (hour - 12).toString()
            else -> hour.toString()
        }
        val amPm = if (hour >= 12) "pm" else "am"
        return if (hour == 0) "" else "$displayHour $amPm"
    }

    fun formatMonthYear(dateTime: LocalDateTime): String {
        val month = dateTime.month.name
            .lowercase()
            .replaceFirstChar { it.titlecase() }
        return "$month ${dateTime.year}"
    }

    fun formatFullDate(dateTime: LocalDateTime): String {
        val month = dateTime.month.name
            .lowercase()
            .replaceFirstChar { it.titlecase() }
        return "$month ${dateTime.day}, ${dateTime.year}"
    }
}