package ru.melnikov.gcalendar

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Plus
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.ui.CalendarView
import ru.melnikov.gcalendar.ui.CalendarViewModel
import ru.melnikov.gcalendar.ui.components.AddEventDialog
import ru.melnikov.gcalendar.ui.components.CalendarDrawer
import ru.melnikov.gcalendar.ui.components.CalendarTopAppBar
import ru.melnikov.gcalendar.ui.components.EventDetailsDialog
import ru.melnikov.gcalendar.ui.screen.day.DayScreen
import ru.melnikov.gcalendar.ui.screen.month.MonthScreen
import ru.melnikov.gcalendar.ui.screen.schedule.ScheduleScreen
import ru.melnikov.gcalendar.ui.screen.three.ThreeDayScreen
import ru.melnikov.gcalendar.ui.screen.week.WeekScreen
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun CalendarApp() {
    val viewModel = koinViewModel<CalendarViewModel>()
    val dateStateHolder = koinInject<DateStateHolder>()
    GCalendarTheme {
        val calendarUiState by viewModel.uiState.collectAsState()
        val dataState by dateStateHolder.currentDateState.collectAsState()
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val navController = rememberNavController()
        val currentRoute by navController.currentBackStackEntryAsState()
        var showAddBottomSheet by remember { mutableStateOf(false) }
        var showDetailsBottomSheet by remember { mutableStateOf(false) }

        val drawerAccounts = remember(calendarUiState.accounts) { calendarUiState.accounts }
        val drawerCalendars = remember(calendarUiState.calendars) { calendarUiState.calendars }

        val visibleCalendars by remember(calendarUiState.calendars) {
            derivedStateOf { calendarUiState.calendars.filter { it.isVisible } }
        }

        ModalNavigationDrawer(
            modifier = Modifier.testTag("ModalNavigationDrawer"),
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = RectangleShape,
                    drawerContainerColor = GCalendarTheme.colorScheme.surfaceContainerHigh,
                ) {
                    val stableRoute =
                        remember(currentRoute?.destination?.route) {
                            currentRoute?.destination?.route
                        }

                    stableRoute?.let { route ->
                        CalendarDrawer(
                            selectedView = route,
                            onViewSelect =
                                remember {
                                    { view ->
                                        scope.launch {
                                            navController.navigate(view.toString())
                                            drawerState.close()
                                        }
                                    }
                                },
                            accounts = drawerAccounts,
                            calendars = drawerCalendars,
                            onCalendarToggle =
                                remember {
                                    { calendar ->
                                        viewModel.toggleCalendarVisibility(calendar)
                                    }
                                }
                        )
                    }
                }
            },
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    CalendarTopAppBar(
                        dateState = dataState,
                        monthDropdownState = calendarUiState.showMonthDropdown,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onSelectToday = {
                            dateStateHolder.updateSelectedDateState(dataState.currentDate)
                        },
                        onToggleMonthDropdown = { show ->
                            viewModel.setTopAppBarMonthDropdown(show)
                        },
                        onDayClick = { date ->
                            dateStateHolder.updateSelectedDateState(date)
                        },
                        calendarUiState.events,
                        calendarUiState.holidays,
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { showAddBottomSheet = true }) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = FontAwesomeIcons.Solid.Plus,
                            contentDescription = "Add Event",
                        )
                    }
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = CalendarView.Month.toString(),
                ) {
                    composable(route = CalendarView.Month.toString()) {
                        val events = calendarUiState.events
                        val holidays = calendarUiState.holidays
                        val eventsProvider =
                            remember(events) {
                                { events }
                            }
                        val holidaysProvider =
                            remember(holidays) {
                                { holidays }
                            }
                        val onDateClickCallback =
                            remember(navController) {
                                { navController.navigate(CalendarView.Day.toString()) }
                            }

                        MonthScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = eventsProvider,
                            holidays = holidaysProvider,
                            onDateClick = onDateClickCallback,
                        )
                    }
                    composable(route = CalendarView.Week.toString()) {
                        WeekScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            },
                            onDateClickCallback = {
                                navController.navigate(CalendarView.Day.toString())
                            }
                        )
                    }
                    composable(route = CalendarView.Day.toString()) {
                        DayScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            }
                        )
                    }
                    composable(route = CalendarView.ThreeDay.toString()) {
                        ThreeDayScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            },
                            onDateClickCallback = {
                                navController.navigate(CalendarView.Day.toString())
                            }
                        )
                    }
                    composable(route = CalendarView.Schedule.toString()) {
                        ScheduleScreen(
                            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
                            dateStateHolder = dateStateHolder,
                            events = calendarUiState.events,
                            holidays = calendarUiState.holidays,
                            onEventClick = { event ->
                                viewModel.selectEvent(event)
                                showDetailsBottomSheet = true
                            }
                        )
                    }
                }
                if (showAddBottomSheet) {
                    AddEventDialog(
                        user = calendarUiState.accounts[0],
                        calendars = visibleCalendars,
                        selectedDate = dataState.currentDate,
                        onSave = { event ->
                            viewModel.addEvent(event)
                            showAddBottomSheet = false
                        },
                        onDismiss = {
                            showAddBottomSheet = false
                        }
                    )
                }

                if (showDetailsBottomSheet) {
                    calendarUiState.selectedEvent?.let { event ->
                        EventDetailsDialog(
                            event = event,
                            onEdit = {
                                viewModel.editEvent(it)
                                viewModel.clearSelectedEvent()
                                showDetailsBottomSheet = false
                            },
                            onDismiss = {
                                viewModel.clearSelectedEvent()
                                showDetailsBottomSheet = false
                            }
                        )
                    }
                }
            }
        }
    }
}