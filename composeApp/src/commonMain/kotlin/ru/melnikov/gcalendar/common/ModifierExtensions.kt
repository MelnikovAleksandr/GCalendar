package ru.melnikov.gcalendar.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.noRippleClickable(onClick: () -> Unit): Modifier = composed {
    this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() }) {
        onClick()
    }
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