@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.common

import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime =
    Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)

fun Month.lengthOfMonth(isLeap: Boolean): Int =
    when (this) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER,
            -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeap) 29 else 28
    }

fun String.toSentenceCase(): String =
    this.lowercase().replaceFirstChar {
        if (it
                .isLowerCase()
        ) {
            it.titlecase()
        } else {
            it.toString()
        }
    }

@OptIn(ExperimentalTime::class)
fun parseDateTime(dateTimeString: String): Long =
    when {
        dateTimeString.contains("T") -> {
            try {
                Instant.parse(dateTimeString).toEpochMilliseconds()
            } catch (e: Exception) {
                val localDateTime =
                    if (dateTimeString.contains("+") || dateTimeString.contains("Z")) {
                        val parts = dateTimeString.split("+", "Z").first()
                        LocalDateTime.parse(parts)
                    } else {
                        LocalDateTime.parse(dateTimeString)
                    }

                localDateTime.toInstant(TimeZone.UTC).toEpochMilliseconds()
            }
        }

        else -> {
            LocalDate
                .parse(dateTimeString)
                .atStartOfDayIn(TimeZone.UTC)
                .toEpochMilliseconds()
        }
    }

fun formatHour(hour: Int): String {
    val displayHour =
        when {
            hour == 0 || hour == 12 -> "12"
            hour > 12 -> (hour - 12).toString()
            else -> hour.toString()
        }
    val amPm = if (hour >= 12) "pm" else "am"
    if (hour == 0) {
        return ""
    }
    return "$displayHour $amPm"
}

fun formatTimeRange(
    start: LocalDateTime,
    end: LocalDateTime,
): String {
    fun formatTime(time: LocalDateTime): String {
        val hour =
            when {
                time.hour == 0 -> 12
                time.hour > 12 -> time.hour - 12
                else -> time.hour
            }
        val minute = time.minute.toString().padStart(2, '0')
        val amPm = if (time.hour >= 12) "am" else "pm"
        return "$hour:$minute $amPm"
    }

    return "${formatTime(start)} – ${formatTime(end)}"
}

fun Int.isLeap(): Boolean = (this % 4 == 0 && this % 100 != 0) || (this % 400 == 0)

fun convertStringToColor(
    string: String,
    alpha: Int = 255,
): Int {
    if (string.isEmpty()) {
        return 0xFFF0F0F0.toInt()
    }

    val hash = string.hashCode()

    val r = 180 + (abs(hash) % 75)
    val g = 180 + (abs(hash / 7) % 75)
    val b = 180 + (abs(hash / 13) % 75)

    return ((alpha and 0xFF) shl 24) or
            ((r and 0xFF) shl 16) or
            ((g and 0xFF) shl 8) or
            (b and 0xFF)
}

inline fun Modifier.applyIf(
    condition: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier =
    if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }