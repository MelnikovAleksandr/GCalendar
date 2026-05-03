package ru.melnikov.gcalendar.ui.screen.schedule.components

import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday

sealed class ScheduleItem {
    abstract val uniqueId: String

    data class MonthHeader(val yearMonth: YearMonth) : ScheduleItem() {
        override val uniqueId: String = "month_${yearMonth.year}_${yearMonth.month.number}"
    }

    data class WeekHeader(val startDate: LocalDate, val endDate: LocalDate) : ScheduleItem() {
        override val uniqueId: String = "week_${startDate.year}_${startDate.month}_${startDate.day}"
    }

    data class DayEvents(
        val date: LocalDate,
        val events: List<Event>,
        val holidays: List<Holiday>
    ) : ScheduleItem() {
        override val uniqueId: String = "day_${date.year}_${date.month}_${date.day}"
    }
}