package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentListOf
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import ru.melnikov.gcalendar.ui.transitions.SharedElementType
import ru.melnikov.gcalendar.ui.transitions.sharedDayColumn
import ru.melnikov.gcalendar.ui.transitions.sharedEventElement
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
internal fun CalendarEventsGrid(
    startDate: LocalDate,
    numDays: Int,
    eventsByDate: ImmutableMap<LocalDate, ImmutableList<Event>>,
    isVisible: Boolean = true,
    timeRange: IntRange,
    hourHeightDp: Float,
    onEventClick: (Event) -> Unit,
    currentDate: LocalDate,
    scrollState: ScrollState,
) {
    val dates =
        List(numDays) { index ->
            startDate.plus(DatePeriod(days = index))
        }

    val totalHeight = timeRange.count() * hourHeightDp

    BoxWithConstraints(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(GCalendarTheme.colorScheme.surfaceContainerLow),
    ) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinute = now.hour * 60 + now.minute

        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            dates.forEachIndexed { _, date ->
                val dayEvents = eventsByDate[date] ?: persistentListOf()
                val isCurrentDay = date == currentDate

                DayColumn(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(totalHeight.dp)
                            .sharedDayColumn(
                                date = date,
                                isVisible = isVisible,
                            ),
                    date = date,
                    events = dayEvents,
                    timeRange = timeRange,
                    hourHeightDp = hourHeightDp,
                    isCurrentDay = isCurrentDay,
                    currentMinute = currentMinute,
                    isVisible = isVisible,
                    onEventClick = onEventClick,
                )
            }
        }
    }
}

@Composable
private fun DayColumn(
    modifier: Modifier = Modifier,
    date: LocalDate,
    events: ImmutableList<Event>,
    timeRange: IntRange,
    hourHeightDp: Float,
    isCurrentDay: Boolean,
    currentMinute: Int,
    isVisible: Boolean,
    onEventClick: (Event) -> Unit,
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            timeRange.forEach { _ ->
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(hourHeightDp.dp)
                            .border(
                                width = 2.dp,
                                color = GCalendarTheme.colorScheme.surfaceContainerLow,
                                shape = RoundedCornerShape(10.dp),
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .background(GCalendarTheme.colorScheme.surfaceContainerHigh),
                )
            }
        }

        if (isCurrentDay) {
            val offsetY = (currentMinute / 60f * hourHeightDp).dp
            Box(
                modifier =
                    Modifier
                        .offset(y = offsetY)
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(GCalendarTheme.colorScheme.primary),
            )
        }

        val eventGroups = remember(events) { groupOverlappingEvents(events) }

        eventGroups.forEach { (_, group) ->
            val totalOverlapping = group.size

            group.forEachIndexed { _, event ->
                val eventStart =
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                val eventEnd =
                    event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())
                val hour = eventStart.hour
                val minute = eventStart.minute

                if (hour in timeRange) {
                    val durationMinutes =
                        if (eventStart.date == eventEnd.date) {
                            (eventEnd.hour - hour) * 60 + (eventEnd.minute - minute)
                        } else {
                            (24 - hour) * 60 - minute
                        }

                    val topOffset =
                        (hour - timeRange.first) * hourHeightDp + (minute / 60f) * hourHeightDp
                    val eventHeight = (durationMinutes / 60f) * hourHeightDp

                    EventItem(
                        event = event,
                        onClick = { onEventClick(event) },
                        modifier =
                            Modifier
                                .offset(y = topOffset.dp)
                                .fillMaxWidth()
                                .height(eventHeight.dp.coerceAtLeast(30.dp))
                                .padding(1.dp),
                        isOverlapping = totalOverlapping > 1,
                        isVisible = isVisible,
                    )
                }
            }
        }
    }
}

private fun groupOverlappingEvents(events: List<Event>): Map<Int, List<Event>> {
    val sortedEvents = events.sortedBy { it.startTime }
    val groups = mutableMapOf<Int, MutableList<Event>>()
    var groupId = 0

    sortedEvents.forEach { event ->
        val eventStart = event.startTime
        val eventEnd = event.endTime

        val existingGroup =
            groups.entries.firstOrNull { (_, groupEvents) ->
                groupEvents.none {
                    (eventStart < it.endTime && eventEnd > it.startTime)
                }
            }

        if (existingGroup != null) {
            existingGroup.value.add(event)
        } else {
            groups[groupId] = mutableListOf(event)
            groupId++
        }
    }

    return groups
}

@Composable
private fun EventItem(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isOverlapping: Boolean = false,
    isVisible: Boolean = true,
) {
    Box(
        modifier =
            modifier
                .sharedEventElement(
                    eventId = event.id,
                    type = SharedElementType.EventCard,
                    isVisible = isVisible,
                )
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, color = Color(event.color))
                .background(Color(event.color).copy(alpha = if (isOverlapping) 0.7f else 0.9f))
                .clickable(onClick = onClick)
                .padding(4.dp),
    ) {
        Text(
            text = event.title,
            style = GCalendarTheme.typography.labelSmall,
            color = GCalendarTheme.colorScheme.inverseOnSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}