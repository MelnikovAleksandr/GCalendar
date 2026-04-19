package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.melnikov.gcalendar.common.customBorder
import ru.melnikov.gcalendar.common.formatHour
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme


@Composable
fun TimeColumn(
    modifier: Modifier = Modifier,
    timeRange: IntRange = 0..23,
    hourHeightDp: Float = 60f,
    scrollState: ScrollState
) {
    Column(
        modifier = modifier
            .verticalScroll(scrollState)
    ) {
        timeRange.forEach { hour ->
            TimeCell(
                hour = hour,
                hourHeightDp = hourHeightDp
            )
        }
    }
}

@Composable
private fun TimeCell(
    hour: Int,
    hourHeightDp: Float
) {
    Box(
        modifier = Modifier
            .height(hourHeightDp.dp)
            .fillMaxWidth()
            .customBorder(
                end = true,
                bottom = true,
                endFraction = 0f,
                endLengthFraction = 1f,
                bottomFraction = 0.85f,
                bottomLengthFraction = 1f,
                color = GCalendarTheme.colorScheme.surfaceVariant,
                width = 1.dp
            )
            .padding(end = 16.dp)
    ) {
        Text(
            text = formatHour(hour),
            style = GCalendarTheme.typography.labelSmall,
            color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.End,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(y= (-8).dp)
        )
    }
}