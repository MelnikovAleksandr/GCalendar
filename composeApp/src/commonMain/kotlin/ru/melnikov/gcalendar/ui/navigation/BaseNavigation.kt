package ru.melnikov.gcalendar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.ui.screen.day.DayScreen
import ru.melnikov.gcalendar.ui.screen.month.MonthScreen
import ru.melnikov.gcalendar.ui.screen.schedule.ScheduleScreen
import ru.melnikov.gcalendar.ui.screen.three.ThreeDayScreen
import ru.melnikov.gcalendar.ui.screen.week.WeekScreen

@Composable
fun NavigationHost(
    modifier: Modifier,
    backStack: MutableList<NavigableScreen>,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider =
            entryProvider {
                entry(NavigableScreen.Month) {
                    MonthScreen(
                        dateStateHolder = dateStateHolder,
                        events = events,
                        holidays = holidays,
                        onDateClick = {
                            backStack.add(NavigableScreen.Day)
                        }
                    )
                }
                entry(NavigableScreen.Week) {
                    WeekScreen(
                        dateStateHolder = dateStateHolder,
                        events = events,
                        holidays = holidays,
                        onEventClick = onEventClick,
                        onDateClickCallback = {
                            backStack.add(NavigableScreen.Day)
                        }
                    )
                }
                entry(NavigableScreen.Day) {
                    DayScreen(
                        dateStateHolder = dateStateHolder,
                        events = events,
                        holidays = holidays,
                        onEventClick = onEventClick,
                    )
                }
                entry(NavigableScreen.ThreeDay) {
                    ThreeDayScreen(
                        dateStateHolder = dateStateHolder,
                        events = events,
                        holidays = holidays,
                        onEventClick = onEventClick,
                        onDateClickCallback = {
                            backStack.add(NavigableScreen.Day)
                        }
                    )
                }
                entry(NavigableScreen.Schedule) {
                    ScheduleScreen(
                        dateStateHolder = dateStateHolder,
                        events = events,
                        holidays = holidays,
                        onEventClick = onEventClick,
                    )
                }
            }
    )
}