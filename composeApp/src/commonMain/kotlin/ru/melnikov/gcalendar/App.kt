package ru.melnikov.gcalendar

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.model.User
import ru.melnikov.gcalendar.ui.CalendarView
import ru.melnikov.gcalendar.ui.CalendarViewModel
import ru.melnikov.gcalendar.ui.YearMonth
import ru.melnikov.gcalendar.ui.isLeap
import kotlin.time.Clock
import kotlin.time.Instant


@Composable
@Preview
fun App() {
    MaterialTheme {
        val viewModel = koinViewModel<CalendarViewModel>()
        CalendarApp(viewModel)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarApp(
    viewModel: CalendarViewModel
) {
    val calendarUiState by viewModel.uiState.collectAsState()
    var showDrawer by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    MaterialTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { showDrawer = true }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                            Text(
                                text = when (calendarUiState.currentView) {
                                    is CalendarView.Month -> calendarUiState.selectedMonth.month.name
                                    is CalendarView.Week -> "Week of ${calendarUiState.selectedDay}"
                                    is CalendarView.Day -> calendarUiState.selectedDay.toString()
                                    is CalendarView.Schedule -> "Schedule"
                                    is CalendarView.ThreeDay -> "3 Day"
                                },
                                style = MaterialTheme.typography.headlineMedium
                            )
                            if (calendarUiState.currentView is CalendarView.Month) {
                                IconButton(onClick = { viewModel.toggleMonthDropdown() }) {
                                    Icon(
                                        imageVector = if (calendarUiState.showMonthDropdown)
                                            Icons.Default.KeyboardArrowUp else Icons.Default.ArrowDropDown,
                                        contentDescription = "Toggle Month Dropdown"
                                    )
                                }

                                DropdownMenu(
                                    expanded = calendarUiState.showMonthDropdown,
                                    onDismissRequest = { viewModel.toggleMonthDropdown() }
                                ) {
                                    val currentYear = Clock.System.now()
                                        .toLocalDateTime(TimeZone.currentSystemDefault()).year
                                    for (monthNum in 1..12) {
                                        val monthName = Month(monthNum).name
                                        DropdownMenuItem(
                                            text = { Text(monthName) },
                                            onClick = {
                                                scope.launch {
                                                    viewModel.selectMonth(
                                                        Month(monthNum),
                                                        currentYear
                                                    )
                                                    viewModel.toggleMonthDropdown()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.selectToday() }) {
                            Text(
                                text = calendarUiState.currentDate.day.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = { /* Handle search */ }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                        IconButton(onClick = { /* Open account menu */ }) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.showAddEventDialog() },
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Event",
                        tint = Color.White
                    )
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Row {
                    AnimatedVisibility(
                        visible = showDrawer,
                        enter = slideInHorizontally() + expandHorizontally(),
                        exit = slideOutHorizontally() + shrinkHorizontally()
                    ) {
                        CalendarDrawer(
                            selectedView = calendarUiState.currentView,
                            onViewSelect = { view ->
                                viewModel.selectView(view)
                                showDrawer = false
                            },
                            accounts = calendarUiState.accounts,
                            calendars = calendarUiState.calendars,
                            onCalendarToggle = { calendar ->
                                viewModel.toggleCalendarVisibility(
                                    calendar
                                )
                            },
                            onDrawerClose = { showDrawer = false }
                        )
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        when (calendarUiState.currentView) {
                            is CalendarView.Month -> MonthView(
                                month = calendarUiState.selectedMonth,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onDayClick = { date -> viewModel.selectDay(date) },
                                selectedDay = calendarUiState.selectedDay
                            )

                            is CalendarView.Week -> WeekView(
                                startDate = calendarUiState.weekStartDate,
                                events = calendarUiState.events,
                                holidays = calendarUiState.holidays,
                                onEventClick = { event -> viewModel.selectEvent(event) }
                            )

                            is CalendarView.Day -> DayView(
                                date = calendarUiState.selectedDay,
                                events = calendarUiState.events.filter {
                                    it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date ==
                                            calendarUiState.selectedDay
                                },
                                onEventClick = { event -> viewModel.selectEvent(event) }
                            )

                            is CalendarView.Schedule -> ScheduleView(
                                events = calendarUiState.upcomingEvents,
                                onEventClick = { event -> viewModel.selectEvent(event) }
                            )

                            is CalendarView.ThreeDay -> ThreeDayView(
                                startDate = calendarUiState.threeDayStartDate,
                                events = calendarUiState.events,
                                onEventClick = { event -> viewModel.selectEvent(event) }
                            )
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
                        onDismiss = { viewModel.hideAddEventDialog() }
                    )
                }

                if (calendarUiState.selectedEvent != null) {
                    EventDetailsDialog(
                        event = calendarUiState.selectedEvent!!,
                        onEdit = { viewModel.editEvent(it) },
                        onDelete = { viewModel.deleteEvent(it) },
                        onDismiss = { viewModel.clearSelectedEvent() }
                    )
                }
            }
        }
    }
}

@Composable
fun CalendarDrawer(
    selectedView: CalendarView,
    onViewSelect: (CalendarView) -> Unit,
    accounts: List<User>,
    calendars: List<Calendar>,
    onCalendarToggle: (Calendar) -> Unit,
    onDrawerClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "Google Calendar",
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 16.dp)
        )

        CalendarViewOption(
            name = "Schedule",
            selected = selectedView is CalendarView.Schedule,
            onClick = { onViewSelect(CalendarView.Schedule) }
        )

        CalendarViewOption(
            name = "Day",
            selected = selectedView is CalendarView.Day,
            onClick = { onViewSelect(CalendarView.Day) }
        )

        CalendarViewOption(
            name = "3 Day",
            selected = selectedView is CalendarView.ThreeDay,
            onClick = { onViewSelect(CalendarView.ThreeDay) }
        )

        CalendarViewOption(
            name = "Week",
            selected = selectedView is CalendarView.Week,
            onClick = { onViewSelect(CalendarView.Week) }
        )

        CalendarViewOption(
            name = "Month",
            selected = selectedView is CalendarView.Month,
            onClick = { onViewSelect(CalendarView.Month) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        accounts.forEach { user ->
            AccountSection(
                user = user,
                calendars = calendars.filter { it.userId == user.id },
                onCalendarToggle = onCalendarToggle
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp)
                .clickable { /* Toggle birthdays visibility */ }
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .background(Color(0xFF8E24AA), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp).align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Birthdays",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun CalendarViewOption(
    name: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(
                    if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent,
                    shape = CircleShape
                )
        ) {
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme
                .onSurface
        )
    }
}

@Composable
fun AccountSection(
    user: User,
    calendars: List<Calendar>,
    onCalendarToggle: (Calendar) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            ) {
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = user.email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        calendars.forEach { calendar ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .clickable { onCalendarToggle(calendar) }
            ) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(calendar.color), shape = CircleShape)
                ) {
                    if (calendar.isVisible) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp).align(Alignment.Center)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = calendar.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun MonthView(
    month: YearMonth,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    selectedDay: LocalDate
) {
    Column(modifier = Modifier.fillMaxSize()) {

        WeekdayHeader()

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxSize()
        ) {
            val firstDayOfMonth = LocalDate(month.year, month.month, 1)
            val firstDayOfWeek =
                firstDayOfMonth.dayOfWeek.ordinal % 7
            val daysInMonth = month.month.lengthOfMonth(month.year.isLeap())

            for (i in 0 until firstDayOfWeek) {
                item {
                    val prevMonth =
                        if (month.month.ordinal == 1) Month(12) else Month(month.month.ordinal - 1)
                    val prevYear = if (month.month.ordinal == 1) month.year - 1 else month.year
                    val daysInPrevMonth = prevMonth.lengthOfMonth(prevYear.isLeap())
                    val day = daysInPrevMonth - (firstDayOfWeek - i - 1)
                    val date = LocalDate(prevYear, prevMonth, day)

                    DayCell(
                        date = date,
                        events = events.filter { event ->
                            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        },
                        holidays = holidays.filter { holiday ->
                            holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        },
                        isCurrentMonth = false,
                        isSelected = false,
                        onDayClick = onDayClick
                    )
                }
            }

            for (day in 1..daysInMonth) {
                item {
                    val date = LocalDate(month.year, month.month, day)

                    DayCell(
                        date = date,
                        events = events.filter { event ->
                            event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        },
                        holidays = holidays.filter { holiday ->
                            holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                        },
                        isCurrentMonth = true,
                        isSelected = date == selectedDay,
                        onDayClick = onDayClick
                    )
                }
            }

            val totalCells = (firstDayOfWeek + daysInMonth)
            val remainingCells = 7 - (totalCells % 7)
            if (remainingCells < 7) {
                for (day in 1..remainingCells) {
                    item {
                        val nextMonth =
                            if (month.month.ordinal == 12) Month(1) else Month(month.month.ordinal + 1)
                        val nextYear = if (month.month.ordinal == 12) month.year + 1 else month.year
                        val date = LocalDate(nextYear, nextMonth, day)

                        DayCell(
                            date = date,
                            events = events.filter { event ->
                                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                            },
                            holidays = holidays.filter { holiday ->
                                holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                            },
                            isCurrentMonth = false,
                            isSelected = false,
                            onDayClick = onDayClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        daysOfWeek.forEach { day ->
            Text(
                text = day,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    isToday -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .clickable { onDayClick(date) }
            .padding(4.dp)
    ) {
        Column {
            Text(
                text = date.day.toString(),
                style = MaterialTheme.typography.bodyLarge,
                color = when {
                    isToday -> MaterialTheme.colorScheme.primary
                    isCurrentMonth -> MaterialTheme.colorScheme.onSurface
                    else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            holidays.firstOrNull()?.let { holiday ->
                Text(
                    text = holiday.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2196F3),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF2196F3).copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                        .padding(2.dp)
                )
            }

            val maxEventsToDisplay = 3
            val displayedEvents = events.take(maxEventsToDisplay)

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            ) {
                displayedEvents.forEach { event ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                Color(event.color ?: 0xFFE91E63.toInt()),
                                CircleShape
                            )
                            .padding(1.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                if (events.size > maxEventsToDisplay) {
                    Text(
                        text = "+${events.size - maxEventsToDisplay}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}


@Composable
fun DayView(
    date: LocalDate,
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Date header
        Text(
            text = "${date.dayOfWeek.name}, ${date.month.name} ${date.day}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        val allDayEvents = events.filter { it.isAllDay }
        if (allDayEvents.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "All-day",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                allDayEvents.forEach { event ->
                    EventItem(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }
            }

            HorizontalDivider()
        }

        val timeEvents = events.filter { !it.isAllDay }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                for (hour in 0..23) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Text(
                            text = "${if (hour == 0) 12 else if (hour > 12) hour - 12 else hour} ${if (hour >= 12) "PM" else "AM"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier
                                .width(60.dp)
                                .padding(end = 8.dp)
                                .padding(top = 4.dp),
                            textAlign = TextAlign.End
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 12.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                val eventsByHour = timeEvents.groupBy {
                    it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).hour
                }

                for (hour in 0..23) {
                    val hourEvents = eventsByHour[hour] ?: emptyList()

                    if (hourEvents.isNotEmpty()) {
                        hourEvents.forEach { event ->
                            val eventDateTime =
                                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            val startMinute = eventDateTime.hour * 60 + eventDateTime.minute
                            val endDateTime =
                                event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())
                            val endMinute = endDateTime.hour * 60 + endDateTime.minute
                            val durationMinutes = endMinute - startMinute

                            item {
                                Box(
                                    modifier = Modifier
                                        .offset(y = (startMinute).dp)
                                        .padding(start = 68.dp, end = 8.dp)
                                        .fillMaxWidth()
                                        .height(durationMinutes.dp)
                                ) {
                                    TimeEventItem(
                                        event = event,
                                        onClick = { onEventClick(event) }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
            val currentDay = now.date

            if (date == currentDay) {
                val currentMinute = now.hour * 60 + now.minute

                Box(
                    modifier = Modifier
                        .offset(y = currentMinute.dp)
                        .padding(start = 60.dp)
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun EventItem(
    event: Event,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(event.color ?: 0xFFE91E63.toInt()).copy(alpha = 0.1f))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(event.color ?: 0xFFE91E63.toInt()), CircleShape)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = event.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )

        event.location?.let { location ->
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = location,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TimeEventItem(
    event: Event,
    onClick: () -> Unit
) {
    val startTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

    val formattedStartTime = "${startTime.hour % 12}:${
        startTime.minute.toString().padStart(2, '0')
    } ${if (startTime.hour >= 12) "PM" else "AM"}"
    val formattedEndTime = "${endTime.hour % 12}:${
        endTime.minute.toString().padStart(2, '0')
    } ${if (endTime.hour >= 12) "PM" else "AM"}"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(event.color ?: 0xFFE91E63.toInt()).copy(alpha = 0.2f))
            .border(
                width = 1.dp,
                color = Color(event.color ?: 0xFFE91E63.toInt()),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = event.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = Color(event.color ?: 0xFFE91E63.toInt()).copy(alpha = 0.8f)
            )

            Text(
                text = "$formattedStartTime - $formattedEndTime",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            event.location?.let { location ->
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun AddEventDialog(
    calendars: List<Calendar>,
    selectedDate: LocalDate,
    onSave: (Event) -> Unit,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCalendarId by remember { mutableStateOf(calendars.firstOrNull()?.id ?: "") }
    var isAllDay by remember { mutableStateOf(false) }

    var startDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.day,
                9,
                0
            )
        )
    }
    var endDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.day,
                10,
                0
            )
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Event") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Calendar",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(calendars) { calendar ->
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(calendar.color))
                                .border(
                                    width = 2.dp,
                                    color = if (selectedCalendarId == calendar.id)
                                        MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedCalendarId = calendar.id }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "All day",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Switch(
                        checked = isAllDay,
                        onCheckedChange = { isAllDay = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (!isAllDay) {
                    Text(
                        text = "Start time",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${startDateTime.hour}:${
                            startDateTime.minute.toString().padStart(2, '0')
                        }",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "End time",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${endDateTime.hour}:${
                            endDateTime.minute.toString().padStart(2, '0')
                        }",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val calendar = calendars.first { it.id == selectedCalendarId }
                    val startInstant = startDateTime.toInstant(TimeZone.currentSystemDefault())
                    val endInstant = endDateTime.toInstant(TimeZone.currentSystemDefault())

                    val event = Event(
                        id = "sdfdsfsdfsdfsdfdfs",
                        calendarId = selectedCalendarId,
                        title = title,
                        description = description,
                        location = location.takeIf { it.isNotEmpty() },
                        startTime = startInstant.toEpochMilliseconds(),
                        endTime = endInstant.toEpochMilliseconds(),
                        isAllDay = isAllDay,
                        color = calendar.color
                    )

                    onSave(event)
                },
                enabled = title.isNotEmpty() && selectedCalendarId.isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EventDetailsDialog(
    event: Event,
    onEdit: (Event) -> Unit,
    onDelete: (Event) -> Unit,
    onDismiss: () -> Unit
) {
    val startDateTime = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
    val endDateTime = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

    val formattedDate =
        "${startDateTime.date.month.name} ${startDateTime.date.day}, ${startDateTime.date.year}"
    val formattedStartTime = "${startDateTime.hour % 12}:${
        startDateTime.minute.toString().padStart(2, '0')
    } ${if (startDateTime.hour >= 12) "PM" else "AM"}"
    val formattedEndTime = "${endDateTime.hour % 12}:${
        endDateTime.minute.toString().padStart(2, '0')
    } ${if (endDateTime.hour >= 12) "PM" else "AM"}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = null,
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(Color(event.color ?: 0xFFE91E63.toInt()))
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (!event.isAllDay) {
                            Text(
                                text = "$formattedStartTime - $formattedEndTime",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = "All day",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                event.location?.let { location ->
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                event.description?.let { description ->
                    if (description.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row {
                TextButton(
                    onClick = { onDelete(event) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete")
                }

                TextButton(
                    onClick = { onEdit(event) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun WeekView(
    startDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        WeekHeader(startDate = startDate)

        Text(
            text = "Week View - Starting ${startDate.month.name} ${startDate.day}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        val weekEvents = events.sortedBy { it.startTime }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(weekEvents) { event ->
                EventItem(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
fun WeekHeader(startDate: LocalDate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        for (i in 0..6) {
            val date = startDate.plus(DatePeriod(days = i))
            val dayOfWeek = date.dayOfWeek.name.substring(0, 1)
            val dayOfMonth = date.day

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = dayOfMonth.toString(),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun ThreeDayView(
    startDate: LocalDate,
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "3-Day View - Starting ${startDate.month.name} ${startDate.day}",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        val threeDayEvents = events
            .filter { event ->
                val eventDate =
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date
                eventDate >= startDate && eventDate < startDate.plus(DatePeriod(days = 3))
            }
            .sortedBy { it.startTime }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(threeDayEvents) { event ->
                EventItem(
                    event = event,
                    onClick = { onEventClick(event) }
                )
            }
        }
    }
}

@Composable
fun ScheduleView(
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            text = "Schedule View",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        val eventsByDate = events
            .sortedBy { it.startTime }
            .groupBy { it.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            eventsByDate.forEach { (date, dateEvents) ->
                item {
                    Text(
                        text = "${date.dayOfWeek.name}, ${date.month.name} ${date.day}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                items(dateEvents) { event ->
                    EventItem(
                        event = event,
                        onClick = { onEventClick(event) }
                    )
                }

                item {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun Month.lengthOfMonth(isLeap:Boolean): Int {
    return when (this) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31
        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeap) 29 else 28
    }
}