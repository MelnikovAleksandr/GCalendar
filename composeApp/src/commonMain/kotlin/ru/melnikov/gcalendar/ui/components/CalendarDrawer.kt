package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.coil3.CoilImage
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.ListAlt
import compose.icons.fontawesomeicons.solid.CalendarAlt
import compose.icons.fontawesomeicons.solid.CalendarDay
import compose.icons.fontawesomeicons.solid.CalendarWeek
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.User
import ru.melnikov.gcalendar.ui.CalendarView
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun CalendarDrawer(
    selectedView: String,
    onViewSelect: (CalendarView) -> Unit,
    accounts: List<User>,
    calendars: List<Calendar>,
    onCalendarToggle: (Calendar) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "GCalendar",
            style = GCalendarTheme.typography.titleLarge,
            color = GCalendarTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        )
        HorizontalDivider(
            modifier = Modifier.padding(bottom = 8.dp),
            color = GCalendarTheme.colorScheme.surfaceVariant,
        )
        CalendarViewOption(
            name = "Schedule",
            selected = selectedView == CalendarView.Schedule.toString(),
            icon = FontAwesomeIcons.Regular.ListAlt,
            onClick = { onViewSelect(CalendarView.Schedule) }
        )

        CalendarViewOption(
            name = "Day",
            selected = selectedView == CalendarView.Day.toString(),
            icon = FontAwesomeIcons.Solid.CalendarDay,
            onClick = { onViewSelect(CalendarView.Day) }
        )

        CalendarViewOption(
            name = "3 Day",
            selected = selectedView == CalendarView.ThreeDay.toString(),
            icon = FontAwesomeIcons.Solid.CalendarAlt,
            onClick = { onViewSelect(CalendarView.ThreeDay) }
        )

        CalendarViewOption(
            name = "Week",
            selected = selectedView == CalendarView.Week.toString(),
            icon = FontAwesomeIcons.Solid.CalendarWeek,
            onClick = { onViewSelect(CalendarView.Week) }
        )

        CalendarViewOption(
            name = "Month",
            selected = selectedView == CalendarView.Month.toString(),
            icon = FontAwesomeIcons.Solid.CalendarAlt,
            onClick = { onViewSelect(CalendarView.Month) }
        )

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = GCalendarTheme.colorScheme.surfaceVariant
        )

        accounts.forEach { user ->
            AccountSection(
                user = user,
                calendars = calendars.filter { it.userId == user.id },
                onCalendarToggle = onCalendarToggle
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(
                top = 8.dp,
                bottom = 8.dp,
                start = 72.dp,
                end = 16.dp
            ),
            color = GCalendarTheme.colorScheme.surfaceVariant
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(horizontal = 8.dp)
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = {},
                colors = CheckboxColors(
                    checkedBoxColor = Color(0xFF8E24AA),
                    uncheckedBoxColor = Color(0xFF8E24AA),
                    checkedCheckmarkColor = GCalendarTheme.colorScheme.onPrimary,
                    uncheckedCheckmarkColor = GCalendarTheme.colorScheme.onPrimary,
                    disabledCheckedBoxColor = Color(0xFF8E24AA),
                    disabledUncheckedBoxColor = Color(0xFF8E24AA),
                    disabledIndeterminateBoxColor = Color(0xFF8E24AA),
                    checkedBorderColor = Color(0xFF8E24AA),
                    uncheckedBorderColor = Color(0xFF8E24AA),
                    disabledBorderColor = Color(0xFF8E24AA),
                    disabledUncheckedBorderColor = Color(0xFF8E24AA),
                    disabledIndeterminateBorderColor = Color(0xFF8E24AA),
                    disabledCheckmarkColor = Color(0xFF8E24AA)
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Birthdays",
                style = GCalendarTheme.typography.bodySmall
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { }
                .padding(horizontal = 8.dp)
        ) {
            Checkbox(
                checked = true,
                onCheckedChange = {},
                colors = CheckboxColors(
                    checkedBoxColor = Color(0xFF4285F4),
                    uncheckedBoxColor = Color(0xFF4285F4),
                    checkedCheckmarkColor = GCalendarTheme.colorScheme.onPrimary,
                    uncheckedCheckmarkColor = GCalendarTheme.colorScheme.onPrimary,
                    disabledCheckedBoxColor = Color(0xFF4285F4),
                    disabledUncheckedBoxColor = Color(0xFF4285F4),
                    disabledIndeterminateBoxColor = Color(0xFF4285F4),
                    checkedBorderColor = Color(0xFF4285F4),
                    uncheckedBorderColor = Color(0xFF4285F4),
                    disabledBorderColor = Color(0xFF4285F4),
                    disabledUncheckedBorderColor = Color(0xFF4285F4),
                    disabledIndeterminateBorderColor = Color(0xFF4285F4),
                    disabledCheckmarkColor = Color(0xFF4285F4)
                )
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Holidays",
                style = GCalendarTheme.typography.bodySmall
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = GCalendarTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
fun CalendarViewOption(
    name: String,
    selected: Boolean,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (selected) GCalendarTheme.colorScheme.primaryContainer
                else Color.Transparent,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)

    ) {
        Icon(
            modifier = Modifier
                .size(20.dp),
            imageVector = icon,
            tint = if (selected) GCalendarTheme.colorScheme.primary
            else GCalendarTheme.colorScheme.onSurfaceVariant,
            contentDescription = null,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = name,
            style = GCalendarTheme.typography.bodySmall,
            color = if (selected) GCalendarTheme.colorScheme.primary
            else GCalendarTheme.colorScheme.onSurfaceVariant
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
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            CoilImage(
                imageModel = { user.photoUrl },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = user.email,
                style = GCalendarTheme.typography.bodyMedium,
                color = GCalendarTheme.colorScheme.onSurfaceVariant
            )
        }

        calendars.forEach { calendar ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCalendarToggle(calendar) }
                    .padding(horizontal = 8.dp)
            ) {
                Checkbox(
                    checked = calendar.isVisible,
                    onCheckedChange = {
                        onCalendarToggle(calendar)
                    },
                    colors = CheckboxColors(
                        checkedBoxColor = Color(calendar.color),
                        uncheckedBoxColor = Color(calendar.color),
                        checkedCheckmarkColor = GCalendarTheme.colorScheme.onPrimary,
                        uncheckedCheckmarkColor = GCalendarTheme.colorScheme.onPrimary,
                        disabledCheckedBoxColor = Color(calendar.color),
                        disabledUncheckedBoxColor = Color(calendar.color),
                        disabledIndeterminateBoxColor = Color(calendar.color),
                        checkedBorderColor = Color(calendar.color),
                        uncheckedBorderColor = Color(calendar.color),
                        disabledBorderColor = Color(calendar.color),
                        disabledUncheckedBorderColor = Color(calendar.color),
                        disabledIndeterminateBorderColor = Color(calendar.color),
                        disabledCheckmarkColor = Color(calendar.color)
                    )
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = calendar.name,
                    style = GCalendarTheme.typography.bodySmall,
                    color = GCalendarTheme.colorScheme.onSurfaceVariant
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
            selectedView = CalendarView.Week.toString(),
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
            onClick = {},
            icon = FontAwesomeIcons.Solid.CalendarDay
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