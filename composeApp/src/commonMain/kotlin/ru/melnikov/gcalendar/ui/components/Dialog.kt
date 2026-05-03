package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.solid.Bars
import compose.icons.fontawesomeicons.solid.Edit
import compose.icons.fontawesomeicons.solid.LocationArrow
import compose.icons.fontawesomeicons.solid.Trash
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun AddEventDialog(
    calendars: List<Calendar>,
    selectedDate: LocalDate,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCalendarId by remember { mutableStateOf(calendars.firstOrNull()?.id ?: "") }
    var isAllDay by remember { mutableStateOf(false) }

    var startDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year, selectedDate.month, selectedDate.day, 9, 0
            )
        )
    }
    var endDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year, selectedDate.month, selectedDate.day, 10, 0
            )
        )
    }


    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp)
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
            style = GCalendarTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(calendars) { calendar ->
                Box(
                    modifier = Modifier.size(24.dp).clip(CircleShape)
                        .background(Color(calendar.color)).border(
                            width = 2.dp,
                            color = if (selectedCalendarId == calendar.id) GCalendarTheme.colorScheme
                                .primary else Color.Transparent,
                            shape = CircleShape
                        ).clickable { selectedCalendarId = calendar.id })
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "All day", style = GCalendarTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.weight(1f))

            Switch(
                checked = isAllDay, onCheckedChange = { isAllDay = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (!isAllDay) {
            Text(
                text = "Start time",
                style = GCalendarTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${startDateTime.hour}:${
                    startDateTime.minute.toString().padStart(2, '0')
                }",
                style = GCalendarTheme.typography.bodySmall,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .background(GCalendarTheme.colorScheme.surface).padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "End time",
                style = GCalendarTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${endDateTime.hour}:${
                    endDateTime.minute.toString().padStart(2, '0')
                }",
                style = GCalendarTheme.typography.bodySmall,
                modifier = Modifier.clip(RoundedCornerShape(4.dp))
                    .background(GCalendarTheme.colorScheme.surface).padding(8.dp)
            )
        }
    }
}

@Composable
fun EventDetailsDialog(
    event: Event, onEdit: (Event) -> Unit, onDelete: (Event) -> Unit, onDismiss: () -> Unit
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

    AlertDialog(onDismissRequest = onDismiss, title = null, text = {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(8.dp)
                    .background(Color(event.color))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = event.title,
                style = GCalendarTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Bars,
                    contentDescription = null,
                    tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = formattedDate, style = GCalendarTheme.typography.bodyMedium
                    )

                    if (!event.isAllDay) {
                        Text(
                            text = "$formattedStartTime - $formattedEndTime",
                            style = GCalendarTheme.typography.bodyMedium
                        )
                    } else {
                        Text(
                            text = "All day", style = GCalendarTheme.typography.bodyMedium
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
                        imageVector = FontAwesomeIcons.Solid.LocationArrow,
                        contentDescription = null,
                        tint = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = location, style = GCalendarTheme.typography.bodySmall
                    )
                }
            }

            event.description?.let { description ->
                if (description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = description, style = GCalendarTheme.typography.bodySmall
                    )
                }
            }
        }
    }, confirmButton = {
        Row {
            TextButton(
                onClick = { onDelete(event) }) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Trash, contentDescription = "Delete"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete")
            }

            TextButton(
                onClick = { onEdit(event) }) {
                Icon(
                    imageVector = FontAwesomeIcons.Solid.Edit, contentDescription = "Edit"
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit")
            }
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Close")
        }
    })
}