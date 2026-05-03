@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.common

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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

fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun Month.lengthOfMonth(isLeap: Boolean): Int {
    return when (this) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeap) 29 else 28
    }
}


fun String.toSentenceCase(): String {
    return this.lowercase().replaceFirstChar {
        if (it
                .isLowerCase()
        ) it.titlecase() else it.toString()
    }
}

fun parseDateTime(dateTimeString: String): Long {
    return when {
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
            LocalDate.parse(dateTimeString)
                .atStartOfDayIn(TimeZone.UTC)
                .toEpochMilliseconds()
        }
    }
}

@Composable
expect fun getScreenWidth(): Dp

@Composable
expect fun getScreenHeight(): Dp

@Composable
fun getTopSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    return with(density) {
        windowInsets.getTop(density).toDp()
    }
}

@Composable
fun getBottomSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    return with(density) {
        windowInsets.getBottom(density).toDp()
    }
}

fun formatHour(hour: Int): String {
    val displayHour = when {
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

fun formatTimeRange(start: LocalDateTime, end: LocalDateTime): String {
    fun formatTime(time: LocalDateTime): String {
        val hour = when {
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

fun Int.isLeap(): Boolean {
    return (this % 4 == 0 && this % 100 != 0) || (this % 400 == 0)
}

fun stringToColor(string: String, alpha: Int = 255): Int {
    if (string.isEmpty()) {
        return 0xFF000000.toInt()
    }
    val hash = string.hashCode()
    val r = (abs(hash) % 255)
    val g = (abs(hash / 7) % 255)
    val b = (abs(hash / 13) % 255)
    return ((alpha and 0xFF) shl 24) or
            ((r and 0xFF) shl 16) or
            ((g and 0xFF) shl 8) or
            (b and 0xFF)
}