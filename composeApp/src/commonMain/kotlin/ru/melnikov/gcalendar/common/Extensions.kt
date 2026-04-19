package ru.melnikov.gcalendar.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun Long.toLocalDateTime(timeZone: TimeZone): LocalDateTime {
    return Instant.fromEpochMilliseconds(this).toLocalDateTime(timeZone)
}

fun Month.lengthOfMonth(isLeap: Boolean): Int {
    return when (this) {
        Month.JANUARY, Month.MARCH, Month.MAY, Month.JULY,
        Month.AUGUST, Month.OCTOBER, Month.DECEMBER -> 31

        Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
        Month.FEBRUARY -> if (isLeap) 29 else 28
    }
}


fun String.toSentenceCase(): String {
    return this.lowercase().replaceFirstChar {
        if (it
                .isLowerCase()
        ) it.titlecase() else it.toString()
    }
}

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
}

@Composable
expect fun getScreenWidth(): Dp

@Composable
expect fun getScreenHeight(): Dp

@Composable
fun getTopSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    return with(density) {
        windowInsets.getTop(density).toDp()
    }
}

@Composable
fun getBottomSystemBarHeight(): Dp {
    val windowInsets = WindowInsets.systemBars
    val density = LocalDensity.current

    return with(density) {
        windowInsets.getBottom(density).toDp()
    }
}

fun formatHour(hour: Int): String {
    val displayHour = when {
        hour == 0 || hour == 12 -> "12"
        hour > 12 -> (hour - 12).toString()
        else -> hour.toString()
    }
    val amPm = if (hour >= 12) "pm" else "am"
    if (hour == 0) {
        return ""
    }
    return "$displayHour $amPm"
}

fun Modifier.customBorder(
    start: Boolean = false,
    top: Boolean = false,
    end: Boolean = false,
    bottom: Boolean = false,
    startFraction: Float = 0f,
    topFraction: Float = 0f,
    endFraction: Float = 0f,
    bottomFraction: Float = 0f,
    startLengthFraction: Float = 1f,
    topLengthFraction: Float = 1f,
    endLengthFraction: Float = 1f,
    bottomLengthFraction: Float = 1f,
    color: Color = Color.Red,
    width: Dp = 2.dp
) = composed {
    drawBehind {
        val strokeWidth = width.toPx()

        if (start) {
            val startX = 0f
            val startY = size.height * startFraction.coerceIn(0f, 1f)
            val endX = 0f
            val endY = startY + (size.height * startLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.height - startY)

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }

        if (top) {
            val startX = size.width * topFraction.coerceIn(0f, 1f)
            val startY = 0f
            val endX = startX + (size.width * topLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.width - startX)
            val endY = 0f

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }

        if (end) {
            val startX = size.width
            val startY = size.height * endFraction.coerceIn(0f, 1f)
            val endX = size.width
            val endY = startY + (size.height * endLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.height - startY)

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }

        if (bottom) {
            val startX = size.width * bottomFraction.coerceIn(0f, 1f)
            val startY = size.height
            val endX = startX + (size.width * bottomLengthFraction.coerceIn(0f, 1f))
                .coerceAtMost(size.width - startX)
            val endY = size.height

            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth
            )
        }
    }
}