package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.User
import ru.melnikov.gcalendar.ui.CalendarView
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun CalendarDrawer(
    selectedView: CalendarView,
    onViewSelect: (CalendarView) -> Unit,
    accounts: List<User>,
    calendars: List<Calendar>,
    onCalendarToggle: (Calendar) -> Unit,
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
    ) {
        Text(
            text = "Google Calendar",
            style = GCalendarTheme.typography.headlineSmall,
            color = GCalendarTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
        HorizontalDivider()
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
                style = GCalendarTheme.typography.bodySmall
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
                    if (selected) GCalendarTheme.colorScheme.primary.copy(alpha = 0.1f)
                    else Color.Transparent,
                    shape = CircleShape
                )
        ) {
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = GCalendarTheme.typography.bodySmall,
            color = if (selected) GCalendarTheme.colorScheme.primary else GCalendarTheme.colorScheme
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
                    .background(GCalendarTheme.colorScheme.primary)
            ) {
                // TODO
            }
            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = user.email,
                style = GCalendarTheme.typography.bodyMedium,
                color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.7f)
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
                    style = GCalendarTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview
@Composable
fun CalendarDrawerPreview() {
    GCalendarTheme {
        CalendarDrawer(
            selectedView = CalendarView.Week,
            onViewSelect = {},
            accounts = emptyList(),
            calendars = emptyList(),
            onCalendarToggle = {}
        )
    }
}

@Preview
@Composable
fun CalendarViewOptionPreview() {
    GCalendarTheme {
        CalendarViewOption(
            name = "Test name",
            selected = true,
            onClick = {}
        )
    }
}

@Preview
@Composable
fun AccountSectionPreview() {
    GCalendarTheme {
        AccountSection(
            user = User(
                id = "qwerty",
                name = "Test Name",
                email = "test@email.com",
                photoUrl = ""
            ),
            calendars = emptyList(),
            onCalendarToggle = {}
        )
    }
}