package ru.melnikov.gcalendar.ui.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gcalendar.composeapp.generated.resources.Res
import gcalendar.composeapp.generated.resources.ic_notifications
import gcalendar.composeapp.generated.resources.ic_unfold_more
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
internal fun NotificationRow(
    reminderMinutes: Int,
    onReminderChange: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_notifications),
            contentDescription = null,
            tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = formatReminderText(reminderMinutes),
            style = GCalendarTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(Res.drawable.ic_unfold_more),
            contentDescription = null,
            tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    }
}

private fun formatReminderText(minutes: Int): String {
    return when {
        minutes <= 0 -> "No reminder"
        minutes < 60 -> "$minutes minutes before"
        minutes == 60 -> "1 hour before"
        minutes < 1440 -> "${minutes / 60} hours before"
        minutes == 1440 -> "1 day before"
        else -> "${minutes / 1440} days before"
    }
}

@Composable
internal fun EventOptionRow(
    icon: DrawableResource,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = GCalendarTheme.typography.bodyMedium,
        )
    }
}