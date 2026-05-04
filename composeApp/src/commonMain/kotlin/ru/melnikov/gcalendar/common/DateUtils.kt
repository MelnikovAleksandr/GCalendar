package ru.melnikov.gcalendar.common

import kotlinx.datetime.LocalDate

object DateUtils {

    const val DEFAULT_MONTH_RANGE = DateRangeHelper.DEFAULT_MONTH_RANGE

    fun getCurrentDate(): LocalDate = DateRangeHelper.getCurrentDate()

    fun getCurrentYear(): Int = DateRangeHelper.getCurrentYear()

    fun getStartTime(monthsBack: Int = DEFAULT_MONTH_RANGE): Long =
        DateRangeHelper.getStartTime(monthsBack)

    fun getEndTime(monthsForward: Int = DEFAULT_MONTH_RANGE): Long =
        DateRangeHelper.getEndTime(monthsForward)

    data class DateRange(
        val currentDate: LocalDate,
        val startTime: Long,
        val endTime: Long
    )

    fun getDateRange(monthsRange: Int = DEFAULT_MONTH_RANGE): DateRange {
        val helperRange = DateRangeHelper.getDateRange(monthsRange)
        return DateRange(
            currentDate = helperRange.currentDate,
            startTime = helperRange.startTime,
            endTime = helperRange.endTime
        )
    }
}