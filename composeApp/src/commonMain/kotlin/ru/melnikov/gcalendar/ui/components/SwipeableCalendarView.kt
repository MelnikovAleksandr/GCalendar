@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.customBorder
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.screen.month.components.EventTag
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
fun SwipeableCalendarView(
    startDate: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    onDateRangeChange: (LocalDate) -> Unit,
    numDays: Int = 7,
    timeRange: IntRange = 0..23,
    hourHeightDp: Float = 60f,
    scrollState: ScrollState,
    currentDate: LocalDate,
    dynamicHeaderHeightState: MutableState<Int>
) {

    require(numDays in 1..31) { "numDays must be between 1 and 31" }

    var size by remember { mutableStateOf(IntSize.Zero) }
    val screenWidth by derivedStateOf { size.width.toFloat() }
    var offsetX by remember { mutableStateOf(0f) }
    var isAnimating by remember { mutableStateOf(false) }
    var targetOffsetX by remember { mutableStateOf(0f) }
    val prevStartDate = remember(startDate) {
        startDate.minus(DatePeriod(days = numDays))
    }
    val nextStartDate = remember(startDate) {
        startDate.plus(DatePeriod(days = numDays))
    }

    val animatedOffset by animateFloatAsState(
        targetValue = targetOffsetX,
        animationSpec = tween(durationMillis = 300),
        finishedListener = {
            if (isAnimating) {
                if (targetOffsetX > 0) {
                    onDateRangeChange(prevStartDate)
                } else if (targetOffsetX < 0) {
                    onDateRangeChange(nextStartDate)
                }
                offsetX = 0f
                targetOffsetX = 0f
                isAnimating = false
            }
        }
    )

    val effectiveOffset = if (isAnimating) animatedOffset else offsetX

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = screenWidth * 0.3f
                        if (abs(offsetX) > threshold) {
                            isAnimating = true
                            targetOffsetX = if (offsetX > 0) {
                                screenWidth
                            } else {
                                -screenWidth
                            }
                        } else {
                            isAnimating = true
                            targetOffsetX = 0f
                        }
                    },
                    onDragCancel = {
                        isAnimating = true
                        targetOffsetX = 0f
                    },
                    onHorizontalDrag = { change, amount ->
                        targetOffsetX += amount
                        if (!isAnimating) {
                            offsetX += amount
                            change.consume()
                        }
                    }
                )
            }
    ) {

        CalendarContent(
            startDate = startDate,
            numDays = numDays,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(effectiveOffset.roundToInt(), 0)
                },
            dynamicHeaderHeightState = dynamicHeaderHeightState
        )

        CalendarContent(
            startDate = prevStartDate,
            numDays = numDays,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(-screenWidth.roundToInt() + effectiveOffset.roundToInt(), 0) },
            dynamicHeaderHeightState = null
        )

        CalendarContent(
            startDate = nextStartDate,
            numDays = numDays,
            events = events,
            holidays = holidays,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onDayClick = onDayClick,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(screenWidth.roundToInt() + effectiveOffset.roundToInt(), 0) },
            dynamicHeaderHeightState = null
        )
    }
}

@Composable
private fun CalendarContent(
    startDate: LocalDate,
    numDays: Int,
    events: List<Event>,
    holidays: List<Holiday>,
    timeRange: IntRange,
    hourHeightDp: Float,
    onDayClick: (LocalDate) -> Unit,
    onEventClick: (Event) -> Unit,
    currentDate: LocalDate,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
    dynamicHeaderHeightState: MutableState<Int>?
) {
    Column(modifier) {
        DaysHeaderRow(
            startDate = startDate,
            numDays = numDays,
            currentDate = currentDate,
            holidays = holidays,
            onDayClick = onDayClick,
            modifier = Modifier.fillMaxWidth(),
            dynamicHeaderHeightState = dynamicHeaderHeightState
        )

        CalendarEventsGrid(
            startDate = startDate,
            numDays = numDays,
            events = events,
            timeRange = timeRange,
            hourHeightDp = hourHeightDp,
            onEventClick = onEventClick,
            currentDate = currentDate,
            scrollState = scrollState
        )
    }
}

@Composable
private fun DaysHeaderRow(
    startDate: LocalDate,
    numDays: Int,
    currentDate: LocalDate,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    dynamicHeaderHeightState: MutableState<Int>?
) {
    val dates = List(numDays) { index ->
        startDate.plus(DatePeriod(days = index))
    }
    val dayNameLength = when {
        numDays <= 3 -> 3
        else -> 1
    }
    Row(
        modifier = modifier
            .background(GCalendarTheme.colorScheme.surfaceContainerHigh)
            .height(IntrinsicSize.Min)
            .heightIn(min = 60.dp)
            .onGloballyPositioned {
                if (dynamicHeaderHeightState != null) {
                    dynamicHeaderHeightState.value = it.size.height
                }
            }
    ) {
        if (numDays > 1) {
            dates.forEach { date ->
                val isToday = date == currentDate
                val currentDayHolidays = holidays.filter {
                    it.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                }
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                        .padding(top = 8.dp)
                        .customBorder(
                            end = true,
                            bottom = true,
                            start = true,
                            startFraction = 0.85f,
                            startLengthFraction = 1f,
                            endFraction = 0.85f,
                            endLengthFraction = 1f,
                            bottomFraction = 0f,
                            bottomLengthFraction = 1f,
                            color = GCalendarTheme.colorScheme.surfaceVariant,
                            width = 1.dp
                        )
                        .clickable { onDayClick(date) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek.name.take(dayNameLength),
                        style = GCalendarTheme.typography.labelSmall
                    )
                    Box(
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .size(28.dp)
                            .background(
                                when {
                                    isToday -> GCalendarTheme.colorScheme.primary
                                    else -> Color.Transparent
                                },
                                if (isToday) CircleShape else RectangleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.day.toString(),
                            style = GCalendarTheme.typography.bodyMedium,
                            color = when {
                                isToday -> GCalendarTheme.colorScheme.inverseOnSurface
                                else -> GCalendarTheme.colorScheme.onSurface
                            }

                        )
                    }

                    if (currentDayHolidays.isNotEmpty()) {
                        Column {
                            currentDayHolidays.forEach { holiday ->
                                EventTag(
                                    modifier = Modifier
                                        .padding(start = 4.dp, end = 4.dp, bottom = 6.dp)
                                        .fillMaxWidth(),
                                    text = holiday.name,
                                    color = Color(0xFF007F73),
                                    textColor = GCalendarTheme.colorScheme.inverseOnSurface
                                )
                            }
                        }
                    }
                }
            }
        } else {
            val currentDayHolidays = holidays.filter {
                it.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == dates.first()
            }
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
            ) {
                if (currentDayHolidays.isNotEmpty()) {
                    currentDayHolidays.forEach { holiday ->
                        Text(
                            text = holiday.name,
                            style = GCalendarTheme.typography.labelMedium,
                            textAlign = TextAlign.Start,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = GCalendarTheme.colorScheme.inverseOnSurface,
                            modifier = modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .background(Color(0xFF007F73), RoundedCornerShape(2.dp))
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarEventsGrid(
    startDate: LocalDate,
    numDays: Int,
    events: List<Event>,
    timeRange: IntRange,
    hourHeightDp: Float,
    onEventClick: (Event) -> Unit,
    currentDate: LocalDate,
    scrollState: ScrollState
) {
    val dates = List(numDays) { index ->
        startDate.plus(DatePeriod(days = index))
    }

    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
            .background(GCalendarTheme.colorScheme.surfaceContainerLow)
    ) {
        val dayColumnWidth = maxWidth / numDays
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val currentMinute = now.hour * 60 + now.minute

        Column {
            timeRange.forEach { _ ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(hourHeightDp.dp)
                ) {
                    repeat(numDays) {
                        Box(
                            Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .border(0.5.dp, GCalendarTheme.colorScheme.surfaceVariant)
                        )
                    }
                }
            }
        }

        if (dates.any { it == currentDate }) {
            val dayIndex = dates.indexOfFirst { it == currentDate }
            if (dayIndex >= 0) {
                val offsetX = dayColumnWidth * dayIndex
                val offsetY = (currentMinute / 60f * hourHeightDp).dp

                Box(
                    modifier = Modifier
                        .offset(x = offsetX, y = offsetY)
                        .width(dayColumnWidth)
                        .height(2.dp)
                        .background(GCalendarTheme.colorScheme.primary)
                )
            }
        }

        dates.forEachIndexed { dayIndex, date ->
            val dayEvents = events.filter { event ->
                event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
            }

            dayEvents.forEach { event ->
                val eventStart = event.startTime.toLocalDateTime(TimeZone.currentSystemDefault())
                val eventEnd = event.endTime.toLocalDateTime(TimeZone.currentSystemDefault())

                val hour = eventStart.hour
                val minute = eventStart.minute

                if (hour in timeRange) {
                    val durationMinutes = if (eventStart.date == eventEnd.date) {
                        (eventEnd.hour - hour) * 60 + (eventEnd.minute - minute)
                    } else {
                        (24 - hour) * 60 - minute
                    }

                    val topOffset =
                        (hour - timeRange.first) * hourHeightDp + (minute / 60f) * hourHeightDp
                    val eventHeight = (durationMinutes / 60f) * hourHeightDp

                    EventItem(
                        event = event,
                        onClick = { onEventClick(event) },
                        modifier = Modifier
                            .offset(
                                x = dayColumnWidth * dayIndex,
                                y = topOffset.dp
                            )
                            .width(dayColumnWidth)
                            .height(eventHeight.dp.coerceAtLeast(30.dp))
                            .padding(1.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EventItem(
    event: Event,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color(event.color ?: 0xFF4285F4.toInt()))
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = event.title,
            style = GCalendarTheme.typography.labelSmall,
            color = Color.White,
            fontSize = 10.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview
@Composable
fun SwipeableCalendarViewPreview() {
    val dynamicHeightOfHeaderComposableWithHolidays = remember { mutableStateOf(0) }
    GCalendarTheme {
        SwipeableCalendarView(
            startDate = LocalDate(2025, 12, 12),
            events = emptyList(),
            holidays = emptyList(),
            onDayClick = {},
            onEventClick = {},
            onDateRangeChange = {},
            scrollState = rememberScrollState(),
            currentDate = LocalDate(2025, 12, 12),
            dynamicHeaderHeightState = dynamicHeightOfHeaderComposableWithHolidays
        )
    }
}