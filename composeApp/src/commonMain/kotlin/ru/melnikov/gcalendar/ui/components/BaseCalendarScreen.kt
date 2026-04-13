package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    numDays: Int
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val verticalScrollState = rememberScrollState()
    val timeColumnWidth = 60.dp
    val timeRange = 0..23
    val hourHeightDp = 60f

    val startDate = dateState.viewStartDate

    Row(
        modifier = modifier
    ) {
        TimeColumn(
            modifier = Modifier
                .padding(top = hourHeightDp.dp)
                .width(timeColumnWidth),
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            scrollState = verticalScrollState
        )
        SwipeableCalendarView(
            startDate = startDate,
            events = events,
            holidays = holidays,
            onDayClick = { date -> dateStateHolder.updateSelectedDateState(date) },
            onEventClick = onEventClick,
            selectedDay = dateState.selectedDate,
            onDateRangeChange = { newStartDate ->
                dateStateHolder.updateViewStartDate(newStartDate)
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
        )
    }
}