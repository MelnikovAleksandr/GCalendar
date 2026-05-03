package ru.melnikov.gcalendar.ui.screen.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.melnikov.gcalendar.common.YearMonth
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.domain.states.ScheduleStateHolder
import ru.melnikov.gcalendar.ui.screen.schedule.components.DayWithEvents
import ru.melnikov.gcalendar.ui.screen.schedule.components.MonthHeader
import ru.melnikov.gcalendar.ui.screen.schedule.components.ScheduleItem
import ru.melnikov.gcalendar.ui.screen.schedule.components.WeekHeader
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit,
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val currentDate = dateState.currentDate
    val currentYearMonth = YearMonth.from(currentDate)

    val scheduleStateHolder =
        remember(
            currentYearMonth.year,
            currentYearMonth.month,
        ) {
            ScheduleStateHolder(
                initialMonth = currentYearMonth,
                events = events,
                holidays = holidays,
            )
        }

    LaunchedEffect(currentYearMonth) {
        dateStateHolder.updateSelectedInViewMonthState(currentYearMonth)
    }

    val listState = rememberLazyListState()

    val isPaginatingBackward = remember { androidx.compose.runtime.mutableStateOf(false) }
    val isPaginatingForward = remember { androidx.compose.runtime.mutableStateOf(false) }

    LaunchedEffect(scheduleStateHolder.initialScrollIndex) {
        if (scheduleStateHolder.initialScrollIndex > 0) {
            listState.scrollToItem(scheduleStateHolder.initialScrollIndex)
        }
    }

    LaunchedEffect(listState) {
        launch {
            snapshotFlow {
                val firstVisible = listState.firstVisibleItemIndex
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size

                (firstVisible until firstVisible + visibleCount)
                    .firstOrNull { idx ->
                        idx < scheduleStateHolder.items.size &&
                                scheduleStateHolder.items[idx] is ScheduleItem.MonthHeader
                    }?.let { idx -> scheduleStateHolder.items[idx] as? ScheduleItem.MonthHeader }
            }.filterNotNull()
                .distinctUntilChanged()
                .collect { header ->
                    dateStateHolder.updateSelectedInViewMonthState(header.yearMonth)
                }
        }

        launch {
            snapshotFlow {
                !isPaginatingBackward.value &&
                        listState.firstVisibleItemIndex < ScheduleStateHolder.THRESHOLD
            }.distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore && !isPaginatingBackward.value) {
                        isPaginatingBackward.value = true

                        val firstVisibleIndex = listState.firstVisibleItemIndex
                        val firstVisibleItemOffset = listState.firstVisibleItemScrollOffset

                        val newItemsCount = scheduleStateHolder.loadMoreBackward()

                        if (newItemsCount > 0) {
                            kotlinx.coroutines.delay(100)

                            try {
                                listState.scrollToItem(
                                    index = firstVisibleIndex + newItemsCount,
                                    scrollOffset = firstVisibleItemOffset,
                                )
                            } catch (_: Exception) {
                            }
                        }

                        kotlinx.coroutines.delay(500)
                        isPaginatingBackward.value = false
                    }
                }
        }

        launch {
            snapshotFlow {
                val visibleInfo = listState.layoutInfo.visibleItemsInfo
                val lastVisibleIndex = visibleInfo.lastOrNull()?.index ?: 0
                val totalItems = scheduleStateHolder.items.size
                !isPaginatingForward.value &&
                        lastVisibleIndex >= totalItems - ScheduleStateHolder.THRESHOLD &&
                        totalItems > 0
            }.distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore && !isPaginatingForward.value) {
                        isPaginatingForward.value = true
                        scheduleStateHolder.loadMoreForward()

                        kotlinx.coroutines.delay(500)
                        isPaginatingForward.value = false
                    }
                }
        }
    }

    if (scheduleStateHolder.items.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            state = listState,
            modifier =
                modifier
                    .fillMaxSize()
                    .background(GCalendarTheme.colorScheme.surfaceContainerLow),
        ) {
            itemsIndexed(
                items = scheduleStateHolder.items,
                key = { _, item -> item.uniqueId },
                contentType = { _, item ->
                    when (item) {
                        is ScheduleItem.MonthHeader -> "month_header"
                        is ScheduleItem.WeekHeader -> "week_header"
                        is ScheduleItem.DayEvents -> "day_events"
                    }
                },
            ) { _, item ->
                when (item) {
                    is ScheduleItem.MonthHeader -> MonthHeader(item.yearMonth)
                    is ScheduleItem.WeekHeader -> WeekHeader(item.startDate, item.endDate)
                    is ScheduleItem.DayEvents ->
                        DayWithEvents(
                            date = item.date,
                            events = item.events,
                            holidays = item.holidays,
                            onEventClick = onEventClick,
                        )
                }
            }
        }
    }
}