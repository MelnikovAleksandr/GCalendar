package ru.melnikov.gcalendar.ui

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

data class YearMonth(val year: Int, val month: Month) {

    constructor(year: Int, monthNumber: Int) : this(
        year,
        Month(monthNumber)
    )

    private fun lengthOfMonth(): Int {
        return when (month) {
            Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
            Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            Month.FEBRUARY -> if (year.isLeap()) 29 else 28
            else -> 0
        }
    }

    fun atDay(day: Int): LocalDate {
        require(day in 1..lengthOfMonth()) { "Day must be valid for month" }
        return LocalDate(year, month, day)
    }

    fun atStartOfMonth(): LocalDate {
        return LocalDate(year, month, 1)
    }

    fun atEndOfMonth(): LocalDate {
        return LocalDate(year, month, lengthOfMonth())
    }

    fun plusMonths(months: Int): YearMonth {
        var newYear = year
        var newMonthNum = month.number + months

        while (newMonthNum > 12) {
            newMonthNum -= 12
            newYear++
        }

        while (newMonthNum < 1) {
            newMonthNum += 12
            newYear--
        }

        return YearMonth(newYear, Month(newMonthNum))
    }

    override fun toString(): String {
        return "$year-${month.number.toString().padStart(2, '0')}"
    }

    companion object {
        fun now(timeZone: TimeZone = TimeZone.currentSystemDefault()): YearMonth {
            val now = Clock.System.now().toLocalDateTime(timeZone)
            return YearMonth(now.year, now.month)
        }

        fun from(date: LocalDate): YearMonth {
            return YearMonth(date.year, date.month)
        }
    }
}

fun Int.isLeap(): Boolean {
    return (this % 4 == 0 && this % 100 != 0) || (this % 400 == 0)
}