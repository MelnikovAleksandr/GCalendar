package ru.melnikov.gcalendar.domain.states

import androidx.compose.runtime.mutableStateListOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.common.isLeap
import ru.melnikov.gcalendar.common.lengthOfMonth
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.screen.schedule.components.ScheduleItem

class ScheduleStateHolder(
    initialMonth: YearMonth,
    val events: List<Event>,
    val holidays: List<Holiday>
) {
    private val _items = mutableStateListOf<ScheduleItem>()
    val items: List<ScheduleItem> = _items

    private val monthRange = ScheduleState(initialMonth)
    val initialScrollIndex: Int

    init {
        val initialItems = createScheduleItemsForMonthRange(
            monthRange.getMonths(),
            events,
            holidays
        )
        _items.addAll(initialItems)

        initialScrollIndex = _items.indexOfFirst {
            it is ScheduleItem.MonthHeader &&
                    it.yearMonth.year == initialMonth.year &&
                    it.yearMonth.month == initialMonth.month
        }.coerceAtLeast(0)
    }

    fun loadMoreBackward(): Int {
        monthRange.expandBackward()
        val newMonths = monthRange.getLastAddedMonthsBackward()
        val newItems = createScheduleItemsForMonthRange(newMonths, events, holidays)

        if (newItems.isNotEmpty()) {
            _items.addAll(0, newItems)
            return newItems.size
        }
        return 0
    }

    fun loadMoreForward(): Int {
        monthRange.expandForward()
        val newMonths = monthRange.getLastAddedMonthsForward()
        val newItems = createScheduleItemsForMonthRange(newMonths, events, holidays)

        if (newItems.isNotEmpty()) {
            _items.addAll(newItems)
            return newItems.size
        }
        return 0
    }

    private fun createScheduleItemsForMonthRange(
        months: List<YearMonth>,
        allEvents: List<Event>,
        allHolidays: List<Holiday>
    ): List<ScheduleItem> {
        val items = mutableListOf<ScheduleItem>()
        fun calculateDaysInMonth(yearMonth: YearMonth): List<LocalDate> {
            val daysInMonth = yearMonth.month.lengthOfMonth(yearMonth.year.isLeap())
            return (1..daysInMonth).map { day ->
                LocalDate(yearMonth.year, yearMonth.month, day)
            }
        }
        months.forEach { yearMonth ->
            items.add(ScheduleItem.MonthHeader(yearMonth))

            val daysInMonth = calculateDaysInMonth(yearMonth)

            val weeks = daysInMonth.chunked(7)

            weeks.forEach { week ->
                if (week.isNotEmpty()) {
                    val firstDay = week.first()
                    val lastDay = week.last()

                    items.add(ScheduleItem.WeekHeader(firstDay, lastDay))

                    week.forEach { date ->
                        val dayEvents = allEvents.filter { event ->
                            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        }

                        val dayHolidays = allHolidays.filter { holiday ->
                            holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        }

                        if (dayEvents.isNotEmpty() || dayHolidays.isNotEmpty()) {
                            items.add(ScheduleItem.DayEvents(date, dayEvents, dayHolidays))
                        }
                    }
                }
            }
        }

        return items
    }

    companion object {
        const val THRESHOLD = 10
    }
}