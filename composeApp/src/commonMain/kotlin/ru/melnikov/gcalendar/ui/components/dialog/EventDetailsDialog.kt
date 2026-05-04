package ru.melnikov.gcalendar.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import gcalendar.composeapp.generated.resources.Res
import gcalendar.composeapp.generated.resources.ic_close
import gcalendar.composeapp.generated.resources.ic_description
import gcalendar.composeapp.generated.resources.ic_edit
import gcalendar.composeapp.generated.resources.ic_location
import gcalendar.composeapp.generated.resources.ic_notifications
import org.jetbrains.compose.resources.painterResource
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import ru.melnikov.gcalendar.ui.transitions.SharedElementType
import ru.melnikov.gcalendar.ui.transitions.sharedEventElement
import ru.melnikov.gcalendar.ui.utils.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailsDialog(
    event: Event,
    onEdit: (Event) -> Unit,
    onDismiss: () -> Unit = {},
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DetailsHeader(
                onClose = onDismiss,
                onEdit = { onEdit(event) },
            )

            Spacer(modifier = Modifier.height(8.dp))

            EventTitleRow(
                eventId = event.id,
                title = event.title,
                color = event.color,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = DateTimeFormatter.formatEventSubheading(event),
                style = GCalendarTheme.typography.bodyMedium,
                color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(start = 52.dp, end = 16.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            DetailRow(
                icon = Res.drawable.ic_location,
                text = "Join with Google Meet",
                iconTint = GCalendarTheme.colorScheme.primary,
                textColor = GCalendarTheme.colorScheme.primary,
            )

            event.location?.let { location ->
                DetailRow(
                    icon = Res.drawable.ic_location,
                    text = location,
                )
            }

            val reminderText = event.reminderMinutes.firstOrNull()?.let {
                "$it minutes before"
            } ?: "10 minutes before"

            DetailRow(
                icon = Res.drawable.ic_notifications,
                text = reminderText,
            )

            DetailRow(
                icon = Res.drawable.ic_description,
                text = "The full guest list has been hidden at the organiser's request.",
            )

            event.description?.takeIf { it.isNotEmpty() }?.let { description ->
                DetailRow(
                    icon = Res.drawable.ic_description,
                    text = description,
                    verticalAlignment = Alignment.Top,
                )
            }
        }
    }
}

@Composable
private fun DetailsHeader(
    onClose: () -> Unit,
    onEdit: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = "Close",
            modifier = Modifier.clickable { onClose() },
            tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
        Icon(
            painter = painterResource(Res.drawable.ic_edit),
            contentDescription = "Edit",
            modifier = Modifier.clickable { onEdit() },
            tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        )
    }
}

@Composable
private fun EventTitleRow(
    eventId: String,
    title: String,
    color: Int,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .width(16.dp)
                    .height(16.dp)
                    .background(
                        Color(color),
                        RoundedCornerShape(2.dp),
                    ),
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = GCalendarTheme.typography.headlineSmall,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DetailRow(
    icon: org.jetbrains.compose.resources.DrawableResource,
    text: String,
    iconTint: Color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    textColor: Color = GCalendarTheme.colorScheme.onSurface,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    Row(
        verticalAlignment = verticalAlignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = iconTint,
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = text,
            style = GCalendarTheme.typography.bodyMedium,
            color = textColor,
        )
    }
}