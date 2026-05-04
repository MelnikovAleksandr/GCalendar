package ru.melnikov.gcalendar.ui.components.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.collections.immutable.ImmutableList
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.model.User
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
internal fun CalendarSelectionSection(
    user: User,
    calendars: ImmutableList<Calendar>,
    selectedCalendarId: String,
    onCalendarSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var currentSelectedId by remember { mutableStateOf(selectedCalendarId) }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 16.dp),
        ) {
            CoilImage(
                imageModel = { user.photoUrl },
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = user.email,
                style = GCalendarTheme.typography.bodySmall,
                color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }

        CalendarList(
            calendars = calendars,
            selectedCalendarId = currentSelectedId,
            onCalendarSelected = { id ->
                currentSelectedId = id
                onCalendarSelected(id)
            },
        )
    }
}

@Composable
private fun CalendarList(
    calendars: ImmutableList<Calendar>,
    selectedCalendarId: String,
    onCalendarSelected: (String) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(calendars) { index, calendar ->
            val isSelected = selectedCalendarId == calendar.id

            Row(
                modifier = Modifier
                    .padding(
                        start = if (index == 0) 50.dp else 0.dp,
                        end = if (index == calendars.size - 1) 16.dp else 0.dp,
                    )
                    .border(
                        0.5.dp,
                        GCalendarTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp),
                    )
                    .background(
                        color = if (!isSelected) {
                            GCalendarTheme.colorScheme.surfaceContainerLow
                        } else {
                            GCalendarTheme.colorScheme.primary
                        },
                        RoundedCornerShape(8.dp),
                    )
                    .padding(8.dp)
                    .noRippleClickable { onCalendarSelected(calendar.id) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(calendar.color)),
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = calendar.name,
                    style = GCalendarTheme.typography.bodySmall,
                    color = if (!isSelected) {
                        GCalendarTheme.colorScheme.onSurfaceVariant
                    } else {
                        GCalendarTheme.colorScheme.onPrimary
                    }
                )
            }
        }
    }
}