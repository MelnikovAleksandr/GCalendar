@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.ui.screen.month.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.getBottomSystemBarHeight
import ru.melnikov.gcalendar.common.getScreenHeight
import ru.melnikov.gcalendar.common.getScreenWidth
import ru.melnikov.gcalendar.common.getTopSystemBarHeight
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun DayCell(
    modifier: Modifier,
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isCurrentMonth: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today
    val maxEventsToShow = 3
    val displayedEvents = events.take(maxEventsToShow)
    val screenWidth = getScreenWidth()
    val screenHeight =
        getScreenHeight().plus(30.dp) - getTopSystemBarHeight() - getBottomSystemBarHeight()

    LazyColumn(
        modifier = modifier
            .background(GCalendarTheme.colorScheme.surfaceContainerLow)
            .border(
                width = 0.2.dp,
                color = GCalendarTheme.colorScheme.outlineVariant
            )
            .aspectRatio(screenWidth / screenHeight)
            .noRippleClickable { onDayClick(date) }
            .padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Text(
                modifier = Modifier
                    .background(
                        when {
                            isToday -> GCalendarTheme.colorScheme.primary
                            else -> Color.Transparent
                        },
                        CircleShape
                    )
                    .padding(4.dp),
                text = date.day.toString(),
                style = GCalendarTheme.typography.labelSmall,
                color = when {
                    isToday -> GCalendarTheme.colorScheme.inverseOnSurface
                    isCurrentMonth -> GCalendarTheme.colorScheme.onSurface
                    else -> GCalendarTheme.colorScheme.onSurfaceVariant
                },
                textAlign = TextAlign.Center
            )
        }
        if(holidays.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(2.dp))
                holidays.forEach { holiday ->
                    EventTag(
                        modifier = Modifier.padding(bottom = 2.dp),
                        text = holiday.name,
                        color = Color(0xFF007F73),
                        textColor = GCalendarTheme.colorScheme.inverseOnSurface
                    )
                }
            }
        }
        if(displayedEvents.isNotEmpty()) {
            items(displayedEvents) { event ->
                Spacer(modifier = Modifier.height(2.dp))
                EventTag(
                    text = event.title,
                    color = Color(event.color),
                    textColor = GCalendarTheme.colorScheme.inverseOnSurface
                )
            }

            if (events.size > maxEventsToShow) {
                item {
                    Text(
                        text = "+${events.size - maxEventsToShow} more",
                        style = GCalendarTheme.typography.labelSmall.copy(fontSize = 8.sp),
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 2.dp, top = 1.dp)
                    )
                }
            }
        }
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
            onDayClick = {}
        )
    }
}
