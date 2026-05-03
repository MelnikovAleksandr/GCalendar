package ru.melnikov.gcalendar.ui.screen.month.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.common.isLeap
import ru.melnikov.gcalendar.common.lengthOfMonth
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun MonthView(
    modifier: Modifier,
    month: YearMonth,
    events: () -> List<Event>,
    holidays: () -> List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
) {
    val firstDayOfMonth = LocalDate(month.year, month.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1
    val daysInMonth = month.month.lengthOfMonth(month.year.isLeap())

    val skipPreviousPadding = firstDayOfWeek >= 7
    val totalDaysDisplayed = if (skipPreviousPadding) daysInMonth else firstDayOfWeek + daysInMonth
    val remainingCells = 42 - totalDaysDisplayed
    val allEvents = events()
    val allHolidays = holidays()

    val eventsByDate =
        remember(month.year, month.month, allEvents) {
            allEvents.groupBy { event ->
                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
            }
        }

    val holidaysByDate =
        remember(month.year, month.month, allHolidays) {
            allHolidays.groupBy { holiday ->
                holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date
            }
        }

    val prevMonth = if (month.month.number == 1) Month(12) else Month(month.month.number - 1)
    val prevYear = if (month.month.number == 1) month.year - 1 else month.year
    val daysInPrevMonth = prevMonth.lengthOfMonth(prevYear.isLeap())
    val nextMonth = if (month.month.number == 12) Month(1) else Month(month.month.number + 1)
    val nextYear = if (month.month.number == 12) month.year + 1 else month.year
    val gridState = rememberLazyGridState()

    BoxWithConstraints {
        val itemSize = DpSize(maxWidth / 7, maxHeight / 6)
        LazyVerticalGrid(
            modifier = modifier
                .fillMaxSize()
                .background(color = GCalendarTheme.colorScheme.surfaceContainerLow),
            state = gridState,
            columns = GridCells.Fixed(7),
            userScrollEnabled = false,
        ) {
            item(span = { GridItemSpan(7) }) {
                WeekdayHeader()
            }
            if (firstDayOfWeek > 0 && !skipPreviousPadding) {
                items(key = { day -> "prev_$day" }, count = firstDayOfWeek) { index ->
                    val ordinal = daysInPrevMonth - (firstDayOfWeek - index - 1)
                    val date = LocalDate(prevYear, prevMonth, ordinal)
                    DayCell(
                        modifier = Modifier,
                        date = date,
                        events = eventsByDate[date] ?: emptyList(),
                        holidays = holidaysByDate[date] ?: emptyList(),
                        isCurrentMonth = false,
                        onDayClick = onDayClick,
                        itemSize = itemSize,
                        isTopLeft = index == 0,
                        isTopRight = index == 6,
                        isBottomLeft = index == 35,
                        isBottomRight = index == 41,
                    )
                }
            }

            items(key = { day -> "current_$day" }, count = daysInMonth) { day ->
                val date = LocalDate(month.year, month.month, day + 1)
                val currentMonthStartIndex = if (skipPreviousPadding) 0 else firstDayOfWeek
                val cellIndex = currentMonthStartIndex + day
                DayCell(
                    modifier = Modifier,
                    date = date,
                    events = eventsByDate[date] ?: emptyList(),
                    holidays = holidaysByDate[date] ?: emptyList(),
                    isCurrentMonth = true,
                    onDayClick = onDayClick,
                    itemSize = itemSize,
                    isTopLeft = cellIndex == 0,
                    isTopRight = cellIndex == 6,
                    isBottomLeft = cellIndex == 35,
                    isBottomRight = cellIndex == 41,
                )
            }

            items(key = { day -> "next_$day" }, count = remainingCells) { day ->
                val date = LocalDate(nextYear, nextMonth, day + 1)
                val cellIndex = totalDaysDisplayed + day

                DayCell(
                    modifier = Modifier,
                    date = date,
                    events = eventsByDate[date] ?: emptyList(),
                    holidays = holidaysByDate[date] ?: emptyList(),
                    isCurrentMonth = false,
                    onDayClick = onDayClick,
                    itemSize = itemSize,
                    isTopLeft = cellIndex == 0,
                    isTopRight = cellIndex == 6,
                    isBottomLeft = cellIndex == 35,
                    isBottomRight = cellIndex == 41,
                )
            }
        }
    }
}

@Preview
@Composable
fun MonthViewPreview() {
    GCalendarTheme {
        MonthView(
            modifier = Modifier,
            month = YearMonth(2025, 12),
            events = { emptyList() },
            holidays = { emptyList() },
            onDayClick = {}
        )
    }
}