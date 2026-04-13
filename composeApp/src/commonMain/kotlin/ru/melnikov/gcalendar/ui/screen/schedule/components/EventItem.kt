package ru.melnikov.gcalendar.ui.screen.schedule.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun EventItem(
    title: String,
    color: Color,
    onClick: () -> Unit,
    timeText: String? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable(onClick = onClick),
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = title,
                style = GCalendarTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Medium
            )

            timeText?.let {
                Text(
                    text = it,
                    style = GCalendarTheme.typography.labelSmall,
                    color = color.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview
@Composable
fun EventItemPreview() {
    GCalendarTheme {
        EventItem(
            title = "Test title",
            color = Color(0xFFE91E63.toInt()),
            onClick = { },
            timeText = "15:20"
        )
    }
}