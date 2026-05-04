package ru.melnikov.gcalendar.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.melnikov.gcalendar.ui.theme.GCalendarColors
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HolidayTag(
    name: String,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
) {
    Text(
        text = name,
        style =
            if (compact) {
                GCalendarTheme.typography.labelSmallEmphasized.copy(fontSize = 8.sp)
            } else {
                GCalendarTheme.typography.labelMedium
            },
        textAlign = TextAlign.Start,
        maxLines = 1,
        color = GCalendarColors.onHoliday,
        overflow = TextOverflow.Ellipsis,
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    GCalendarColors.holiday,
                    RoundedCornerShape(if (compact) 4.dp else 8.dp),
                ).padding(if (compact) 2.dp else 8.dp),
    )
}

@Composable
fun ScheduleHolidayTag(
    name: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = name,
        style = GCalendarTheme.typography.bodyMedium,
        maxLines = 1,
        color = GCalendarColors.scheduleHoliday,
        overflow = TextOverflow.Ellipsis,
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    GCalendarColors.scheduleHolidayContainer,
                    RoundedCornerShape(4.dp),
                ).padding(horizontal = 12.dp, vertical = 8.dp),
    )
}