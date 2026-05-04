package ru.melnikov.gcalendar.ui.screen.three

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.state.DateStateHolder
import ru.melnikov.gcalendar.ui.components.BaseCalendarScreen
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun ThreeDayScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: ImmutableList<Event>,
    holidays: ImmutableList<Holiday>,
    isVisible: Boolean = true,
    onEventClick: (Event) -> Unit,
    onDateClickCallback: () -> Unit,
) {
    BaseCalendarScreen(
        modifier = modifier,
        dateStateHolder = dateStateHolder,
        events = events,
        holidays = holidays,
        isVisible = isVisible,
        onEventClick = onEventClick,
        numDays = 3,
        onDateClickCallback = onDateClickCallback,
    )
}

@Preview
@Composable
fun ThreeDayScreenPreview() {
    GCalendarTheme {
        ThreeDayScreen(
            modifier = Modifier,
            dateStateHolder = DateStateHolder(),
            events = persistentListOf(),
            holidays = persistentListOf(),
            onEventClick = {},
            onDateClickCallback = {}
        )
    }
}