@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.ui.screen.schedule.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.formatTimeRange
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DayWithEvents(
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isVisible: Boolean = true,
    onEventClick: (Event) -> Unit
) {
    val today = remember {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
    val isToday = remember(date) { date == today }

    val dayOfWeekText = remember(date) {
        date.dayOfWeek.name.take(3).uppercase()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Text(
                text = dayOfWeekText,
                style = GCalendarTheme.typography.labelSmall,
                color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Text(
                text = date.day.toString(),
                style = GCalendarTheme.typography.headlineSmall,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                color = GCalendarTheme.colorScheme.onSurface
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            holidays.forEach { holiday ->
                EventItem(
                    title = holiday.name,
                    color = Color(0xFF4CAF50),
                    onClick = { }
                )
            }

            events.forEach { event ->
                val timeText = remember(event) {
                    if (!event.isAllDay) {
                        val startDateTime =
                            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                        val endDateTime =
                            event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())
                        formatTimeRange(startDateTime, endDateTime)
                    } else null
                }

                EventItem(
                    title = event.title,
                    color = Color(event.color),
                    onClick = { onEventClick(event) },
                    eventId = event.id,
                    isVisible = isVisible,
                    timeText = timeText
                )
            }
        }
    }
}

@Preview
@Composable
fun DayWithEventsPreview() {
    GCalendarTheme {
        DayWithEvents(
            date = LocalDate(2025, 12, 12),
            events = emptyList(),
            holidays = emptyList(),
            onEventClick = {}
        )
    }
}