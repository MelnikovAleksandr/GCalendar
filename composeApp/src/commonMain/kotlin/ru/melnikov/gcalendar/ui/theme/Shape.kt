package ru.melnikov.gcalendar.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

private val ExtraSmallCornerSize = 4.dp
private val SmallCornerSize = 8.dp
private val MediumCornerSize = 12.dp
private val LargeCornerSize = 16.dp
private val ExtraLargeCornerSize = 24.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(ExtraSmallCornerSize),
    small = RoundedCornerShape(SmallCornerSize),
    medium = RoundedCornerShape(MediumCornerSize),
    large = RoundedCornerShape(LargeCornerSize),
    extraLarge = RoundedCornerShape(ExtraLargeCornerSize)
)

val EventShape = RoundedCornerShape(4.dp)
val CalendarDayShape = RoundedCornerShape(50)