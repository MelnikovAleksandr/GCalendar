package ru.melnikov.gcalendar.ui.screen.month

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.ui.components.SwipeablePager
import ru.melnikov.gcalendar.ui.screen.month.components.MonthView
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun MonthScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onDateClick: () -> Unit,
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()

    val onSpecificDayClicked =
        remember(dateStateHolder, onDateClick) {
            { date: LocalDate ->
                dateStateHolder.updateSelectedDateState(date)
                onDateClick()
            }
        }

    val onMonthChange =
        remember(dateStateHolder) {
            { yearMonth: YearMonth ->
                dateStateHolder.updateSelectedInViewMonthState(yearMonth)
            }
        }

    SwipeablePager(
        modifier = modifier.testTag("SwipeableMonthView"),
        currentReference =
            YearMonth(
                dateState.selectedInViewMonth.year,
                dateState.selectedInViewMonth.month,
            ),
        calculateOffset = { current, base ->
            (current.year - base.year) * 12 + (current.month.number - base.month.number)
        },
        pageToReference = { baseMonth, initialPage, page ->
            val offset = page - initialPage
            baseMonth.plusMonths(offset)
        },
        onReferenceChange = onMonthChange,
    ) { month ->
        val monthEvents =
            remember(month, events) {
                events
            }
        val monthHolidays =
            remember(month, holidays) {
                holidays
            }

        MonthView(
            modifier = Modifier.testTag("MonthView_$month"),
            month = month,
            events = monthEvents.toImmutableList(),
            holidays = monthHolidays.toImmutableList(),
            onDayClick = onSpecificDayClicked,
        )
    }
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