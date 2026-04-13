package ru.melnikov.gcalendar.ui.screen.month

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.getBottomSystemBarHeight
import ru.melnikov.gcalendar.common.getScreenHeight
import ru.melnikov.gcalendar.common.getScreenWidth
import ru.melnikov.gcalendar.common.getTopSystemBarHeight
import ru.melnikov.gcalendar.common.lengthOfMonth
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.YearMonth
import ru.melnikov.gcalendar.ui.isLeap
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.time.Clock

@Composable
fun MonthView(
    modifier: Modifier,
    month: YearMonth,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    selectedDay: LocalDate
) {
    val firstDayOfMonth = LocalDate(month.year, month.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1
    val daysInMonth = month.month.lengthOfMonth(month.year.isLeap())

    val skipPreviousPadding = firstDayOfWeek >= 7
    val totalDaysDisplayed = if (skipPreviousPadding) daysInMonth else firstDayOfWeek + daysInMonth
    val remainingCells = 42 - totalDaysDisplayed

    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Fixed(7),
        userScrollEnabled = false
    ) {
        item(span = { GridItemSpan(7) }) {
            WeekdayHeader()
        }
        if (firstDayOfWeek > 0 && !skipPreviousPadding) {
            items(firstDayOfWeek) { index ->
                val prevMonth =
                    if (month.month.number == 1) Month(12) else Month(month.month.number - 1)
                val prevYear = if (month.month.number == 1) month.year - 1 else month.year
                val daysInPrevMonth = prevMonth.lengthOfMonth(prevYear.isLeap())
                val day = daysInPrevMonth - (firstDayOfWeek - index - 1)
                val date = LocalDate(prevYear, prevMonth, day)

                DayCell(
                    modifier = Modifier,
                    date = date,
                    events = events.filter { event ->
                        event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                    },
                    holidays = holidays.filter { holiday ->
                        holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                    },
                    isCurrentMonth = false,
                    isSelected = date == selectedDay,
                    onDayClick = onDayClick
                )
            }
        }

        items(daysInMonth) { day ->
            val date = LocalDate(month.year, month.month, day + 1)
            DayCell(
                modifier = Modifier,
                date = date,
                events = events.filter { event ->
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                holidays = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                isCurrentMonth = true,
                isSelected = date == selectedDay,
                onDayClick = onDayClick
            )
        }

        items(remainingCells) { day ->
            val nextMonth =
                if (month.month.number == 12) Month(1) else Month(month.month.number + 1)
            val nextYear = if (month.month.number == 12) month.year + 1 else month.year
            val date = LocalDate(nextYear, nextMonth, day + 1)

            DayCell(
                modifier = Modifier,
                date = date,
                events = events.filter { event ->
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                holidays = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                isCurrentMonth = false,
                isSelected = date == selectedDay,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GCalendarTheme.colorScheme.surface)
    ) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        daysOfWeek.forEach { day ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = day,
                    style = GCalendarTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
fun DayCell(
    modifier: Modifier,
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today
    val screenWidth = getScreenWidth()
    val screenHeight =
        getScreenHeight().plus(30.dp) - getTopSystemBarHeight() - getBottomSystemBarHeight()


    Box(
        modifier = modifier
            .border(
                width = 0.5.dp,
                color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.12f)
            )
            .aspectRatio(screenWidth / screenHeight)
            .background(
                when {
                    isSelected -> GCalendarTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else -> GCalendarTheme.colorScheme.surface
                }
            )
            .noRippleClickable { onDayClick(date) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(1.dp)
        ) {
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .size(28.dp)
                    .align(Alignment.CenterHorizontally)
                    .background(
                        when {
                            isToday -> GCalendarTheme.colorScheme.primary
                            isSelected -> GCalendarTheme.colorScheme.primary.copy(alpha = 0.3f)
                            else -> Color.Transparent
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = date.day.toString(),
                    style = GCalendarTheme.typography.bodyMedium,
                    color = when {
                        isToday -> Color.White
                        isCurrentMonth -> GCalendarTheme.colorScheme.onSurface
                        else -> GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    },
                    fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }

            val maxEventsToShow = 3
            val displayedEvents = events.take(maxEventsToShow)

            holidays.firstOrNull()?.let { holiday ->
                EventTag(
                    text = holiday.name,
                    color = Color(0xFF4285F4).copy(alpha = 0.8f),
                    textColor = Color.White
                )
            }

            displayedEvents.forEach { event ->
                EventTag(
                    text = event.title,
                    color = Color(event.color ?: 0xFFE91E63.toInt()).copy(alpha = 0.8f),
                    textColor = Color.White
                )
            }

            if (events.size > maxEventsToShow) {
                Text(
                    text = "+${events.size - maxEventsToShow} more",
                    style = GCalendarTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 2.dp, top = 1.dp)
                )
            }
        }
    }
}

@Composable
fun EventTag(
    text: String,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp, vertical = 1.dp)
            .height(16.dp)
            .background(color, RoundedCornerShape(2.dp))
            .padding(horizontal = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.CenterStart)
        )
    }
}

@Preview
@Composable
fun MonthViewPreview() {
    GCalendarTheme {
        MonthView(
            modifier = Modifier,
            month = YearMonth(2025, 12),
            events = emptyList(),
            holidays = emptyList(),
            onDayClick = {},
            selectedDay = LocalDate(2025, 12, 12)
        )
    }
}

@Preview
@Composable
fun WeekdayHeaderPreview() {
    GCalendarTheme {
        WeekdayHeader()
    }
}

@Preview
@Composable
fun DayCellPreview() {
    GCalendarTheme {
        DayCell(
            modifier = Modifier,
            date = LocalDate(2025, 12, 12),
            events = emptyList(),
            holidays = emptyList(),
            isCurrentMonth = true,
            isSelected = true,
            onDayClick = {}
        )
    }
}

@Preview
@Composable
fun EventTagPreview() {
    GCalendarTheme {
        EventTag(
            text = "Test name",
            color = Color(0xFF4285F4).copy(alpha = 0.8f),
            textColor = Color.White
        )
    }
}