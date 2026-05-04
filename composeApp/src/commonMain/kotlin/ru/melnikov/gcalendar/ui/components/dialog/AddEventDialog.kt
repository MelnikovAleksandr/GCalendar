package ru.melnikov.gcalendar.ui.components.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import gcalendar.composeapp.generated.resources.Res
import gcalendar.composeapp.generated.resources.ic_description
import gcalendar.composeapp.generated.resources.ic_location
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import ru.melnikov.gcalendar.common.convertStringToColor
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.User
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class, ExperimentalUuidApi::class)
@Composable
fun AddEventDialog(
    user: User,
    calendars: ImmutableList<Calendar>,
    selectedDate: LocalDate,
    onSave: (Event) -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCalendarId by remember { mutableStateOf(calendars.firstOrNull()?.id ?: "") }
    var isAllDay by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    var selectedEventType by remember { mutableStateOf(EventType.EVENT) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var startDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.dayOfMonth,
                12,
                0,
            )
        )
    }
    var endDateTime by remember {
        mutableStateOf(
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.dayOfMonth,
                12,
                30,
            )
        )
    }
    var showLocationField by remember { mutableStateOf(false) }
    var reminderMinutes by remember { mutableStateOf(10) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = true),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            DialogHeader(
                onCancel = onDismiss,
                onSave = {
                    if (title.isNotBlank()) {
                        val selectedCalendar = calendars.find { it.id == selectedCalendarId }
                        val event = createEvent(
                            title = title,
                            description = description,
                            location = location,
                            selectedDate = selectedDate,
                            startDateTime = startDateTime,
                            endDateTime = endDateTime,
                            isAllDay = isAllDay,
                            selectedCalendar = selectedCalendar,
                            selectedCalendarId = selectedCalendarId,
                            reminderMinutes = reminderMinutes,
                        )
                        onSave(event)
                    }
                },
            )

            TitleTextField(
                value = title,
                onValueChange = { title = it },
                interactionSource = interactionSource,
            )

            EventTypeSelector(
                selectedType = selectedEventType,
                onTypeSelected = { selectedEventType = it },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

            CalendarTimeSection(
                isAllDayInitial = isAllDay,
                selectedDate = selectedDate,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                onAllDayChange = { isAllDay = it },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

            CalendarSelectionSection(
                user = user,
                calendars = calendars,
                selectedCalendarId = selectedCalendarId,
                onCalendarSelected = { selectedCalendarId = it },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

            EventOptionRow(
                icon = Res.drawable.ic_location,
                text = "Add location",
                onClick = { showLocationField = !showLocationField },
            )

            if (showLocationField) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    placeholder = { Text("Enter location") },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    singleLine = true,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

            NotificationRow(
                reminderMinutes = reminderMinutes,
                onReminderChange = { reminderMinutes = it },
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp)

            EventOptionRow(
                icon = Res.drawable.ic_description,
                text = "Add description",
                onClick = { },
            )
        }
    }
}

@Composable
private fun DialogHeader(
    onCancel: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Cancel",
            modifier = Modifier.clickable { onCancel() },
            color = GCalendarTheme.colorScheme.primary,
        )
        Text(
            "Save",
            style = GCalendarTheme.typography.bodyLarge,
            modifier = Modifier.clickable { onSave() },
            color = GCalendarTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun TitleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    interactionSource: MutableInteractionSource,
) {
    TextField(
        modifier = Modifier.fillMaxWidth().padding(start = 40.dp),
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.headlineSmall,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        interactionSource = interactionSource,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                text = "Add title",
                color = GCalendarTheme.colorScheme.onSurface,
                style = GCalendarTheme.typography.headlineSmall,
            )
        },
    )
}

@OptIn(ExperimentalUuidApi::class, kotlin.time.ExperimentalTime::class)
private fun createEvent(
    title: String,
    description: String,
    location: String,
    selectedDate: LocalDate,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime,
    isAllDay: Boolean,
    selectedCalendar: Calendar?,
    selectedCalendarId: String,
    reminderMinutes: Int,
): Event {
    return Event(
        id = Uuid.random().toString(),
        calendarId = selectedCalendarId,
        calendarName = selectedCalendar?.name ?: "",
        title = title,
        description = description.takeIf { it.isNotBlank() },
        location = location.takeIf { it.isNotBlank() },
        startTime = if (isAllDay) {
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.dayOfMonth,
                0, 0,
            ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } else {
            startDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        },
        endTime = if (isAllDay) {
            LocalDateTime(
                selectedDate.year,
                selectedDate.month,
                selectedDate.dayOfMonth,
                23, 59,
            ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        } else {
            endDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        },
        isAllDay = isAllDay,
        reminderMinutes = if (reminderMinutes > 0) listOf(reminderMinutes) else emptyList(),
        color = selectedCalendar?.color ?: convertStringToColor("defaultColor", 255),
    )
}