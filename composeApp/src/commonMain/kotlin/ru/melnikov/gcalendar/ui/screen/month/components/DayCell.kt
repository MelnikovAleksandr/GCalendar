@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.ui.screen.month.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun DayCell(
    modifier: Modifier,
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isCurrentMonth: Boolean,
    onDayClick: (LocalDate) -> Unit,
    itemSize: DpSize,
    isTopLeft: Boolean = false,
    isTopRight: Boolean = false,
    isBottomLeft: Boolean = false,
    isBottomRight: Boolean = false,
) {
    val today =
        Clock.System
            .now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .date
    val isToday = date == today
    val maxEventsToShow = 3
    val displayedEvents = events.take(maxEventsToShow)
    val cornerRadius = 16.dp
    val cellShape =
        RoundedCornerShape(
            topStart = if (isTopLeft) cornerRadius else 8.dp,
            topEnd = if (isTopRight) cornerRadius else 8.dp,
            bottomStart = if (isBottomLeft) cornerRadius else 8.dp,
            bottomEnd = if (isBottomRight) cornerRadius else 8.dp,
        )

    Column(
        modifier =
            modifier
                .border(
                    width = 2.dp,
                    color = GCalendarTheme.colorScheme.surfaceContainerLow,
                    shape = cellShape,
                ).size(itemSize)
                .noRippleClickable { onDayClick(date) }
                .clip(cellShape)
                .background(GCalendarTheme.colorScheme.surfaceContainerHigh)
                .padding(2.dp)
                .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(top = GCalendarTheme.dimensions.spacing_4)
                    .clip(MaterialShapes.Cookie9Sided.toShape())
                    .size(30.dp)
                    .background(
                        when {
                            isToday -> GCalendarTheme.colorScheme.primary
                            else -> Color.Transparent
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = date.day.toString(),
                style = GCalendarTheme.typography.labelSmall,
                color =
                    when {
                        isToday -> GCalendarTheme.colorScheme.inverseOnSurface
                        isCurrentMonth -> GCalendarTheme.colorScheme.onSurface
                        else -> GCalendarTheme.colorScheme.onSurfaceVariant
                    },
                textAlign = TextAlign.Center,
            )
        }

        if (holidays.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            holidays.forEach { holiday ->
                EventTag(
                    modifier = Modifier.padding(bottom = 2.dp),
                    text = holiday.name,
                    color = Color(0xFF007F73),
                    textColor = GCalendarTheme.colorScheme.inverseOnSurface,
                )
            }
        }

        if (displayedEvents.isNotEmpty()) {
            displayedEvents.forEach { event ->
                Spacer(modifier = Modifier.height(2.dp))
                EventTag(
                    text = event.title,
                    color = Color(event.color),
                    textColor = GCalendarTheme.colorScheme.inverseOnSurface,
                )
            }

            if (events.size > maxEventsToShow) {
                Text(
                    text = "+${events.size - maxEventsToShow} more",
                    style = GCalendarTheme.typography.labelSmallEmphasized.copy(fontSize = 8.sp),
                    textAlign = TextAlign.End,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(end = 2.dp, top = 1.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun DayCellPreview() {
    GCalendarTheme {
        DayCell(
            modifier = Modifier,
            date = LocalDate(2025, 12, 12),
            events = emptyList(),
            holidays = emptyList(),
            isCurrentMonth = true,
            onDayClick = {},
            itemSize = DpSize(40.dp, 40.dp)
        )
    }
}
