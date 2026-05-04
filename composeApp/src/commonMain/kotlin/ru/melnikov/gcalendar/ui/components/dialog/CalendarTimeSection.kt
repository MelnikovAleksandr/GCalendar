package ru.melnikov.gcalendar.ui.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gcalendar.composeapp.generated.resources.Res
import gcalendar.composeapp.generated.resources.ic_clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.painterResource
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import ru.melnikov.gcalendar.ui.utils.DateTimeFormatter

@Composable
internal fun CalendarTimeSection(
    isAllDayInitial: Boolean,
    selectedDate: LocalDate,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime,
    onAllDayChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isAllDay by remember { mutableStateOf(isAllDayInitial) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_clock),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "All day",
            style = GCalendarTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.weight(1f))

        Switch(
            checked = isAllDay,
            onCheckedChange = {
                isAllDay = !isAllDay
                onAllDayChange(isAllDay)
            }
        )
    }

    val dateLabel = formatDateLabel(selectedDate)

    if (isAllDay) {
        TimeDisplayRow(
            label = dateLabel,
            onClick = {},
        )
    } else {
        TimeDisplayRow(
            label = dateLabel,
            startTime = DateTimeFormatter.formatTime(startDateTime),
            endTime = DateTimeFormatter.formatTime(endDateTime),
            onClick = { },
        )
    }
}

@Composable
internal fun TimeDisplayRow(
    label: String,
    startTime: String? = null,
    endTime: String? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(top = 8.dp, bottom = 8.dp, start = 52.dp, end = 16.dp),
    ) {
        Text(
            text = label,
            style = GCalendarTheme.typography.bodyMedium,
        )
        Spacer(modifier = Modifier.weight(1f))
        if (!startTime.isNullOrEmpty()) {
            Text(
                text = startTime,
                style = GCalendarTheme.typography.bodyMedium,
            )
        }
    }

    if (endTime != null) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(top = 8.dp, bottom = 8.dp, start = 52.dp, end = 16.dp),
        ) {
            Text(
                text = label,
                style = GCalendarTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = endTime,
                style = GCalendarTheme.typography.bodyMedium,
            )
        }
    }
}

private fun formatDateLabel(date: LocalDate): String {
    val month = date.month.name.lowercase().replaceFirstChar { it.titlecase() }
    return "$month ${date.day}, ${date.year}"
}