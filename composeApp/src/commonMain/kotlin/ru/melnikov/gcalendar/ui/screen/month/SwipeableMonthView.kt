package ru.melnikov.gcalendar.ui.screen.month

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.datetime.LocalDate
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.ui.screen.month.components.MonthView
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeableMonthView(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    events: () -> List<Event>,
    holidays: () -> List<Holiday>,
    onSpecificDayClicked: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    var swipeState by remember { mutableStateOf(SwipeState()) }
    var screenSize by remember { mutableStateOf(IntSize.Zero) }

    val screenWidth by remember {
        derivedStateOf { screenSize.width.toFloat() }
    }

    val monthWindow by remember {
        mutableStateOf(
            MonthWindow(
                previous = MonthViewData(currentMonth.plusMonths(-1), events, holidays),
                current = MonthViewData(currentMonth, events, holidays),
                next = MonthViewData(currentMonth.plusMonths(1), events, holidays)
            )
        )
    }

    val currentMonthKey = remember(currentMonth) { currentMonth.toString() }
    remember(currentMonthKey) {
        if (monthWindow.current.month != currentMonth) {
            monthWindow.updateToMonth(currentMonth, events, holidays)
        }
    }

    val animatedOffset by animateFloatAsState(
        targetValue = swipeState.targetOffsetX,
        animationSpec = tween(durationMillis = 250),
        finishedListener = { finalValue ->
            if (swipeState.isAnimating) {
                when {
                    finalValue > 0 -> {
                        val newCurrentMonth = currentMonth.plusMonths(-1)
                        monthWindow.updateForPreviousMonth(newCurrentMonth, events, holidays)
                        onMonthChange(newCurrentMonth)
                    }
                    finalValue < 0 -> {
                        val newCurrentMonth = currentMonth.plusMonths(1)
                        monthWindow.updateForNextMonth(newCurrentMonth, events, holidays)
                        onMonthChange(newCurrentMonth)
                    }
                }
                swipeState = SwipeState()
            }
        },
        label = "month_swipe_animation"
    )

    val effectiveOffset = if (swipeState.isAnimating) animatedOffset else swipeState.offsetX

    Surface(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { newSize ->
                if (screenSize != newSize) {
                    screenSize = newSize
                }
            }
            .pointerInput(screenWidth) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        val threshold = screenWidth * 0.25f
                        swipeState = if (abs(swipeState.offsetX) > threshold) {
                            swipeState.copy(
                                isAnimating = true,
                                targetOffsetX = if (swipeState.offsetX > 0) screenWidth else -screenWidth
                            )
                        } else {
                            swipeState.copy(isAnimating = true, targetOffsetX = 0f)
                        }
                    },
                    onDragCancel = {
                        swipeState = swipeState.copy(isAnimating = true, targetOffsetX = 0f)
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        if (!swipeState.isAnimating) {
                            val newOffsetX = swipeState.offsetX + dragAmount
                            swipeState = swipeState.copy(
                                offsetX = newOffsetX,
                                targetOffsetX = newOffsetX
                            )
                            change.consume()
                        }
                    }
                )
            }
    ) {
        MonthViewContainer(
            monthViewData = monthWindow.current,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(effectiveOffset.roundToInt(), 0) }
        )

        MonthViewContainer(
            monthViewData = monthWindow.previous,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        -screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                        0
                    )
                }
        )

        MonthViewContainer(
            monthViewData = monthWindow.next,
            onDayClick = onSpecificDayClicked,
            modifier = Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        screenWidth.roundToInt() + effectiveOffset.roundToInt(),
                        0
                    )
                }
        )
    }
}

@Composable
private fun MonthViewContainer(
    monthViewData: MonthViewData,
    onDayClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val events = remember(monthViewData.month) {
        monthViewData.events()
    }
    val holidays = remember(monthViewData.month) {
        monthViewData.holidays()
    }

    MonthView(
        modifier=modifier.testTag("MonthView_${monthViewData.month}"),
        month = monthViewData.month,
        events = { events },
        holidays = { holidays },
        onDayClick = onDayClick
    )
}

@Stable
private data class SwipeState(
    val offsetX: Float = 0f,
    val isAnimating: Boolean = false,
    val targetOffsetX: Float = 0f
)

@Stable
private data class MonthViewData(
    val month: YearMonth,
    val events: () -> List<Event>,
    val holidays: () -> List<Holiday>
)

@Stable
private class MonthWindow(
    previous: MonthViewData,
    current: MonthViewData,
    next: MonthViewData
) {
    var previous by mutableStateOf(previous)
    var current by mutableStateOf(current)
    var next by mutableStateOf(next)

    fun updateForPreviousMonth(newCurrentMonth: YearMonth, events: () -> List<Event>, holidays: () -> List<Holiday>) {
        next = current
        current = previous
        previous = MonthViewData(newCurrentMonth.plusMonths(-1), events, holidays)
    }

    fun updateForNextMonth(newCurrentMonth: YearMonth, events: () -> List<Event>, holidays: () -> List<Holiday>) {
        previous = current
        current = next
        next = MonthViewData(newCurrentMonth.plusMonths(1), events, holidays)
    }

    fun updateToMonth(targetMonth: YearMonth, events: () -> List<Event>, holidays: () -> List<Holiday>) {
        current = MonthViewData(targetMonth, events, holidays)
        previous = MonthViewData(targetMonth.plusMonths(-1), events, holidays)
        next = MonthViewData(targetMonth.plusMonths(1), events, holidays)
    }
}