package ru.melnikov.gcalendar.common

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant


object DateRangeHelper {

    const val DEFAULT_MONTH_RANGE = 10
    fun getCurrentDate(): LocalDate =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date

    fun getCurrentYear(): Int = getCurrentDate().year

    fun getCurrentTimeZone(): TimeZone = TimeZone.currentSystemDefault()

    fun getCurrentTimeMillis(): Long =
        Clock.System.now().toEpochMilliseconds()

    fun getStartTime(monthsBack: Int = DEFAULT_MONTH_RANGE): Long =
        getCurrentDate()
            .minus(DatePeriod(months = monthsBack))
            .atStartOfDayIn(getCurrentTimeZone())
            .toEpochMilliseconds()

    fun getEndTime(monthsForward: Int = DEFAULT_MONTH_RANGE): Long =
        getCurrentDate()
            .plus(DatePeriod(months = monthsForward))
            .atStartOfDayIn(getCurrentTimeZone())
            .toEpochMilliseconds()

    fun getYearRange(year: Int): Pair<Long, Long> {
        val timeZone = getCurrentTimeZone()

        val startDateTime = LocalDateTime(
            year = year,
            month = Month.JANUARY,
            day = 1,
            hour = 0,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        val endDateTime = LocalDateTime(
            year = year,
            month = Month.DECEMBER,
            day = 31,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999
        )

        val startMillis = startDateTime.toInstant(timeZone).toEpochMilliseconds()
        val endMillis = endDateTime.toInstant(timeZone).toEpochMilliseconds()

        return Pair(startMillis, endMillis)
    }

    fun getMonthRange(year: Int, month: Int): Pair<Long, Long> {
        val timeZone = getCurrentTimeZone()
        val kotlinMonth = Month(month)

        val startDateTime = LocalDateTime(
            year = year,
            month = kotlinMonth,
            day = 1,
            hour = 0,
            minute = 0,
            second = 0,
            nanosecond = 0
        )

        val isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
        val lastDay = kotlinMonth.lengthOfMonth(isLeapYear)

        val endDateTime = LocalDateTime(
            year = year,
            month = kotlinMonth,
            day = lastDay,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999
        )

        val startMillis = startDateTime.toInstant(timeZone).toEpochMilliseconds()
        val endMillis = endDateTime.toInstant(timeZone).toEpochMilliseconds()

        return Pair(startMillis, endMillis)
    }

    fun getDayRange(date: LocalDate): Pair<Long, Long> {
        val timeZone = getCurrentTimeZone()

        val startMillis = date.atStartOfDayIn(timeZone).toEpochMilliseconds()

        val endDateTime = LocalDateTime(
            year = date.year,
            month = date.month,
            day = date.day,
            hour = 23,
            minute = 59,
            second = 59,
            nanosecond = 999_999_999
        )
        val endMillis = endDateTime.toInstant(timeZone).toEpochMilliseconds()

        return Pair(startMillis, endMillis)
    }

    data class DateRange(
        val currentDate: LocalDate,
        val startTime: Long,
        val endTime: Long
    )

    fun getDateRange(monthsRange: Int = DEFAULT_MONTH_RANGE): DateRange {
        val currentDate = getCurrentDate()
        val timeZone = getCurrentTimeZone()

        val startTime = currentDate
            .minus(DatePeriod(months = monthsRange))
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

        val endTime = currentDate
            .plus(DatePeriod(months = monthsRange))
            .atStartOfDayIn(timeZone)
            .toEpochMilliseconds()

        return DateRange(currentDate, startTime, endTime)
    }

    fun epochToLocalDateTime(epochMillis: Long): LocalDateTime =
        Instant
            .fromEpochMilliseconds(epochMillis)
            .toLocalDateTime(getCurrentTimeZone())

    fun localDateTimeToEpoch(dateTime: LocalDateTime): Long =
        dateTime.toInstant(getCurrentTimeZone()).toEpochMilliseconds()

    fun localDateToEpoch(date: LocalDate): Long =
        date.atStartOfDayIn(getCurrentTimeZone()).toEpochMilliseconds()
}