package ru.melnikov.gcalendar

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import ru.melnikov.gcalendar.ui.state.DateStateHolder
import ru.melnikov.gcalendar.ui.CalendarViewModel
import ru.melnikov.gcalendar.ui.components.CalendarBottomNavigationBar
import ru.melnikov.gcalendar.ui.components.CalendarTopAppBar
import ru.melnikov.gcalendar.ui.components.ErrorSnackBar
import ru.melnikov.gcalendar.ui.components.dialog.AddEventDialog
import ru.melnikov.gcalendar.ui.components.dialog.EventDetailsDialog
import ru.melnikov.gcalendar.ui.navigation.NavigableScreen
import ru.melnikov.gcalendar.ui.navigation.NavigationHost
import ru.melnikov.gcalendar.ui.navigation.replaceLast
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import ru.melnikov.gcalendar.ui.viewmodel.EventViewModel

private val config =
    SavedStateConfiguration {
        serializersModule =
            SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(NavigableScreen.Schedule::class, NavigableScreen.Schedule.serializer())
                    subclass(NavigableScreen.Day::class, NavigableScreen.Day.serializer())
                    subclass(NavigableScreen.ThreeDay::class, NavigableScreen.ThreeDay.serializer())
                    subclass(NavigableScreen.Week::class, NavigableScreen.Week.serializer())
                    subclass(NavigableScreen.Month::class, NavigableScreen.Month.serializer())
                }
            }
    }

@Composable
fun CalendarApp() {
    val calendarViewModel = koinViewModel<CalendarViewModel>()
    val eventViewModel = koinViewModel<EventViewModel>()
    val dateStateHolder = koinInject<DateStateHolder>()
    GCalendarTheme {
        CalendarApp(
            calendarViewModel = calendarViewModel,
            eventViewModel = eventViewModel,
            dateStateHolder = dateStateHolder,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun CalendarApp(
    calendarViewModel: CalendarViewModel,
    eventViewModel: EventViewModel,
    dateStateHolder: DateStateHolder,
) {
    val calendarUiState by calendarViewModel.uiState.collectAsState()
    val eventUiState by eventViewModel.uiState.collectAsState()
    val dataState by dateStateHolder.currentDateState.collectAsState()
    val backStack = rememberNavBackStack(config, NavigableScreen.Month)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showAddBottomSheet by remember { mutableStateOf(false) }
    val selectedEvent = eventUiState.selectedEvent
    val showDetailsBottomSheet = selectedEvent != null
    val visibleCalendars by remember(calendarUiState.calendars) {
        derivedStateOf { calendarUiState.calendars.filter { it.isVisible } }
    }
    val events = remember(calendarUiState.events) { calendarUiState.events }
    val holidays = remember(calendarUiState.holidays) { calendarUiState.holidays }
    val displayError = calendarUiState.displayError ?: eventUiState.errorMessage

    Scaffold(
        containerColor = GCalendarTheme.colorScheme.surfaceContainerLow,
        topBar = {
            CalendarTopAppBar(
                dateState = dataState,
                onSelectToday = {
                    dateStateHolder.updateSelectedDateState(dataState.currentDate)
                },
                onDayClick = { date ->
                    dateStateHolder.updateSelectedDateState(date)
                    backStack.add(NavigableScreen.Day)
                },
                events = events,
                holidays = holidays,
            )
        },
        snackbarHost = {
            ErrorSnackBar(
                message = displayError,
                onDismiss = {
                    calendarViewModel.clearError()
                    eventViewModel.clearError()
                },
            )
        },
    ) { paddingValues ->
        Box {
            NavigationHost(
                modifier =
                    Modifier.padding(
                        top = paddingValues.calculateTopPadding(),
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        end = paddingValues.calculateEndPadding(LayoutDirection.Ltr),
                    ),
                backStack = backStack,
                dateStateHolder = dateStateHolder,
                events = events,
                holidays = holidays,
                onEventClick = { event ->
                    eventViewModel.selectEvent(event)
                },
            )
            CalendarBottomNavigationBar(
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = paddingValues.calculateBottomPadding()),
                selectedView = backStack.lastOrNull() as NavigableScreen,
                onViewSelect = { view ->
                    backStack.replaceLast(view)
                },
                onAddClick = { showAddBottomSheet = true },
            )
        }
        if (showAddBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddBottomSheet = false },
                sheetState = sheetState,
                properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
            ) {
                calendarUiState.accounts.firstOrNull()?.let {
                    AddEventDialog(
                        user = it,
                        calendars = visibleCalendars.toImmutableList(),
                        selectedDate = dataState.currentDate,
                        onSave = { event ->
                            eventViewModel.addEvent(event)
                            showAddBottomSheet = false
                        },
                        onDismiss = {
                            showAddBottomSheet = false
                        },
                    )
                }
            }
        }

        if (showDetailsBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    eventViewModel.clearSelectedEvent()
                },
                sheetState = sheetState,
                properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
            ) {
                EventDetailsDialog(
                    event = selectedEvent,
                    onEdit = { editedEvent ->
                        eventViewModel.editEvent(editedEvent)
                    },
                    onDismiss = {
                        eventViewModel.clearSelectedEvent()
                    },
                )
            }
        }
    }
}