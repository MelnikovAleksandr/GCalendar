package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.melnikov.gcalendar.common.customBorder
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.domain.states.DateStateHolderImpl
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun BaseCalendarScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit,
    onDateClickCallback: () -> Unit,
    numDays: Int
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp = 60f
    val startDate = dateState.selectedDate
    val isToday = startDate == dateState.currentDate

    Row(
        modifier = modifier
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(hourHeightDp.dp)
                    .width(timeColumnWidth)
                    .background(color = GCalendarTheme.colorScheme.onPrimary)
            ) {
                if (numDays == 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .customBorder(
                                end = true,
                                endFraction = 0f,
                                endLengthFraction = 1f,
                                color = GCalendarTheme.colorScheme.surfaceVariant,
                                width = 1.dp
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = startDate.dayOfWeek.name.take(3),
                            style = GCalendarTheme.typography.labelSmall
                        )
                        Box(
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .size(28.dp)
                                .background(
                                    when {
                                        isToday -> GCalendarTheme.colorScheme.primary
                                        else -> GCalendarTheme.colorScheme.onPrimary
                                    },
                                    if (isToday)
                                        CircleShape
                                    else
                                        RectangleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = startDate.day.toString(),
                                style = GCalendarTheme.typography.bodyMedium,
                                color = when {
                                    isToday -> GCalendarTheme.colorScheme.inverseOnSurface
                                    else -> GCalendarTheme.colorScheme.onSurface
                                },
                            )
                        }
                    }
                }
            }
            TimeColumn(
                modifier = Modifier
                    .width(timeColumnWidth),
                timeRange = timeRange,
                hourHeightDp = hourHeightDp,
                scrollState = verticalScrollState
            )
        }
        SwipeableCalendarView(
            startDate = startDate,
            events = events,
            holidays = holidays,
            onDayClick = { date ->
                dateStateHolder.updateSelectedDateState(date)
                onDateClickCallback()
            },
            onEventClick = onEventClick,
            onDateRangeChange = { newStartDate ->
                dateStateHolder.updateSelectedDateState(newStartDate)
            },
            numDays = numDays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState,
            currentDate = dateState.currentDate
        )
    }
}

@Preview
@Composable
fun BaseCalendarScreenPreview() {
    GCalendarTheme {
        BaseCalendarScreen(
            events = emptyList(),
            holidays = emptyList(),
            dateStateHolder = DateStateHolderImpl(),
            onEventClick = {},
            numDays = 3,
            onDateClickCallback = {}
        )
    }
}