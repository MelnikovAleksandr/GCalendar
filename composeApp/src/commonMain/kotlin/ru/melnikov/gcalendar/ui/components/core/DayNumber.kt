package ru.melnikov.gcalendar.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayNumber(
    day: Int,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    isCurrentMonth: Boolean = true,
    isSelected: Boolean = false,
    size: Dp = 30.dp,
) {
    Box(
        modifier =
            modifier
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .size(size)
                .background(
                    when {
                        isToday -> GCalendarTheme.colorScheme.primary
                        isSelected -> GCalendarTheme.colorScheme.primaryContainer
                        else -> Color.Transparent
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            style = GCalendarTheme.typography.labelSmall,
            color =
                when {
                    isToday -> GCalendarTheme.colorScheme.inverseOnSurface
                    isSelected -> GCalendarTheme.colorScheme.onPrimaryContainer
                    isCurrentMonth -> GCalendarTheme.colorScheme.onSurface
                    else -> GCalendarTheme.colorScheme.onSurfaceVariant
                },
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DayNumberLarge(
    day: Int,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 30.dp,
) {
    Box(
        modifier =
            modifier
                .clip(MaterialShapes.Cookie9Sided.toShape())
                .size(size)
                .background(
                    when {
                        isToday -> GCalendarTheme.colorScheme.primary
                        else -> Color.Transparent
                    },
                ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = day.toString(),
            style = GCalendarTheme.typography.bodyMedium,
            color =
                when {
                    isToday -> GCalendarTheme.colorScheme.inverseOnSurface
                    else -> GCalendarTheme.colorScheme.onSurface
                },
        )
    }
}