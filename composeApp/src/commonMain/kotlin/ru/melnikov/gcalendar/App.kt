package ru.melnikov.gcalendar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.domain.states.ViewType
import ru.melnikov.gcalendar.ui.CalendarView
import ru.melnikov.gcalendar.ui.CalendarViewModel
import ru.melnikov.gcalendar.ui.components.AddEventDialog
import ru.melnikov.gcalendar.ui.components.CalendarDrawer
import ru.melnikov.gcalendar.ui.components.EventDetailsDialog
import ru.melnikov.gcalendar.ui.components.CalendarTopAppBar
import ru.melnikov.gcalendar.ui.screen.day.DayScreen
import ru.melnikov.gcalendar.ui.screen.month.MonthScreen
import ru.melnikov.gcalendar.ui.screen.schedule.ScheduleScreen
import ru.melnikov.gcalendar.ui.screen.three.ThreeDayScreen
import ru.melnikov.gcalendar.ui.screen.week.WeekScreen
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope =
    compositionLocalOf<SharedTransitionScope> {
        error("Error Occurred during creation of SharedTransitionScope ")
    }

@Composable
@Preview
fun App() {
    val viewModel = koinViewModel<CalendarViewModel>()
    val dateStateHolder = koinInject<DateStateHolder>()
    GCalendarTheme {
        CalendarApp(viewModel, dateStateHolder)
    }
}

@Composable
fun CalendarApp(
    viewModel: CalendarViewModel, dateStateHolder: DateStateHolder
) {
    val calendarUiState by viewModel.uiState.collectAsState()
    val dataState by dateStateHolder.currentDateState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentRoute by navController.currentBackStackEntryAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                currentRoute?.destination?.route?.let {
                    CalendarDrawer(
                        selectedView = it,
                        onViewSelect = { view ->
                            navController.navigate(view.toString())
                            scope.launch { drawerState.close() }
                        },
                        accounts = calendarUiState.accounts,
                        calendars = calendarUiState.calendars,
                        onCalendarToggle = { calendar ->
                            viewModel.toggleCalendarVisibility(
                                calendar
                            )
                        }
                    )
                }
            }
        },
    ) {
        Scaffold(
            topBar = {
                CalendarTopAppBar(
                    dateState = dataState,
                    monthDropdownState = calendarUiState.showMonthDropdown,
                    onMenuClick = { scope.launch { drawerState.open() } },
                    onSelectToday = {
                        dateStateHolder.updateSelectedDateState(
                            dataState.currentDate
                        )
                    },
                    onToggleMonthDropdown = { viewModel.setTopAppBarMonthDropdown(it) },
                    onDayClick = { date ->
                        dateStateHolder.updateSelectedDateState(
                            date
                        )
                    },
                    calendarUiState.events,
                    calendarUiState.holidays
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddEventDialog() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                    )
                }
            },
        ) { paddingValues ->
            SharedTransitionLayout {
                CompositionLocalProvider(
                    LocalSharedTransitionScope provides this,
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = CalendarView.Month.toString()
                    ) {
                        composable(
                            route = CalendarView.Month.toString(),
                        ) {
                            MonthScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder,
                                calendarUiState.events,
                                calendarUiState.holidays,
                                onDateClick = {
                                    navController.navigate(CalendarView.Day.toString())
                                }
                            )
                        }

                        composable(
                            route = CalendarView.Week.toString(),
                        ) {
                            WeekScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClickCallback = {
                                    navController.navigate(CalendarView.Day.toString())
                                }
                            )
                        }

                        composable(
                            route = CalendarView.Day.toString(),
                        ) {
                            DayScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClickCallback = {}
                            )
                        }

                        composable(
                            route = CalendarView.Schedule.toString(),
                        ) {
                            ScheduleScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) })
                        }

                        composable(
                            route = CalendarView.ThreeDay.toString(),
                        ) {
                            ThreeDayScreen(
                                modifier = Modifier.padding(paddingValues),
                                dateStateHolder = dateStateHolder,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) },
                                onDateClickCallback = {
                                    navController.navigate(CalendarView.Day.toString())
                                }
                            )
                        }
                    }
                }
            }

            if (calendarUiState.showAddEventDialog) {
                AddEventDialog(
                    calendars = calendarUiState.calendars.filter { it.isVisible },
                    selectedDate = calendarUiState.selectedDay,
                    onSave = { event ->
                        viewModel.addEvent(event)
                        viewModel.hideAddEventDialog()
                    },
                    onDismiss = { viewModel.hideAddEventDialog() },
                )
            }

            if (calendarUiState.selectedEvent != null) {
                EventDetailsDialog(
                    event = calendarUiState.selectedEvent!!,
                    onEdit = { viewModel.editEvent(it) },
                    onDelete = { viewModel.deleteEvent(it) },
                    onDismiss = { viewModel.clearSelectedEvent() },
                )
            }
        }
    }
}