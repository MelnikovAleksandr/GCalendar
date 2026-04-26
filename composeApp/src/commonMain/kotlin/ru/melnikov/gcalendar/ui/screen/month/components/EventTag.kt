package ru.melnikov.gcalendar.ui.screen.month.components


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun EventTag(
    modifier: Modifier = Modifier,
    text: String,
    color: Color,
) {
    Text(
        text = text,
        style = GCalendarTheme.typography.labelSmall.copy(fontSize = 8.sp),
        textAlign = TextAlign.Start,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.fillMaxWidth()
            .background(color, RoundedCornerShape(2.dp))
            .padding(2.dp)
    )
}

@Preview
@Composable
fun EventTagPreview() {
    GCalendarTheme {
        EventTag(
            text = "Test name",
            color = Color(0xFF4285F4).copy(alpha = 0.8f)
        )
    }
}