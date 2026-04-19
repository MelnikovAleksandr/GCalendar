package ru.melnikov.gcalendar.ui.screen.three

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.domain.states.DateStateHolderImpl
import ru.melnikov.gcalendar.ui.components.BaseCalendarScreen
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun ThreeDayScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit,
    onDateClickCallback: () -> Unit
) {
    BaseCalendarScreen(
        modifier = modifier,
        dateStateHolder = dateStateHolder,
        events = events,
        holidays = holidays,
        onEventClick = onEventClick,
        numDays = 3,
        onDateClickCallback = onDateClickCallback
    )
}

@Preview
@Composable
fun ThreeDayScreenPreview() {
    GCalendarTheme {
        ThreeDayScreen(
            modifier = Modifier,
            dateStateHolder = DateStateHolderImpl(),
            events = emptyList(),
            holidays = emptyList(),
            onEventClick = {},
            onDateClickCallback = {}
        )
    }
}