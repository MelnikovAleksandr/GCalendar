package ru.melnikov.gcalendar.ui.screen.month

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.LocalDate
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun MonthScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onDateClick: (LocalDate) -> Unit
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    SwipeableMonthView(
        modifier = modifier,
        currentMonth = YearMonth(
            dateState.selectedInViewMonth.year,
            dateState.selectedInViewMonth.month
        ),
        events = events,
        holidays = holidays,
        onSpecificDayClicked = { date ->
            dateStateHolder.updateSelectedDateState(date)
            onDateClick(date)
        },
        onMonthChange = { yearMonth ->
            dateStateHolder.updateSelectedInViewMonthState(yearMonth)
        }
    )
}

@Preview
@Composable
fun MonthScreenPreview() {
    GCalendarTheme {
        MonthScreen(
            modifier = Modifier,
            dateStateHolder = DateStateHolder(),
            events = emptyList(),
            holidays = emptyList(),
            onDateClick = {}
        )
    }
}