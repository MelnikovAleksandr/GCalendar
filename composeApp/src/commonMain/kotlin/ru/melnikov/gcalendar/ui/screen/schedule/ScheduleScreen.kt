package ru.melnikov.gcalendar.ui.screen.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
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
import ru.melnikov.gcalendar.ui.screen.schedule.components.ScheduleItem
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme

@Composable
fun ScheduleScreen(
    modifier: Modifier = Modifier,
    dateStateHolder: DateStateHolder,
    events: List<Event>,
    holidays: List<Holiday>,
    onEventClick: (Event) -> Unit
) {
    val dateState by dateStateHolder.currentDateState.collectAsState()
    val currentDate = dateState.currentDate
    val currentYearMonth = YearMonth.from(currentDate)

    val scheduleStateHolder = remember(currentYearMonth, events, holidays) {
        ScheduleStateHolder(
            initialMonth = currentYearMonth,
            events = events,
            holidays = holidays
        )
    }

    LaunchedEffect(currentYearMonth) {
        dateStateHolder.updateSelectedInViewMonthState(currentYearMonth)
    }

    val listState = rememberLazyListState()

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
                    }
                    ?.let { scheduleStateHolder.items[it] as? ScheduleItem.MonthHeader }
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { header ->
                    dateStateHolder.updateSelectedInViewMonthState(header.yearMonth)
                }
        }

        launch {
            snapshotFlow { listState.firstVisibleItemIndex < ScheduleStateHolder.THRESHOLD }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        val firstVisibleIndex = listState.firstVisibleItemIndex
                        val newItemsCount = scheduleStateHolder.loadMoreBackward()

                        if (newItemsCount > 0) {
                            listState.scrollToItem(firstVisibleIndex + newItemsCount)
                        }
                    }
                }
        }

        launch {
            snapshotFlow {
                val visibleInfo = listState.layoutInfo.visibleItemsInfo
                val lastVisibleIndex = visibleInfo.lastOrNull()?.index ?: 0
                lastVisibleIndex >= scheduleStateHolder.items.size - ScheduleStateHolder.THRESHOLD
            }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        scheduleStateHolder.loadMoreForward()
                    }
                }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .background(GCalendarTheme.colorScheme.surfaceContainerLow)
    ) {
        itemsIndexed(
            items = scheduleStateHolder.items,
            key = { _, item -> item.uniqueId }
        ) { _, item ->
            when (item) {
                is ScheduleItem.MonthHeader -> ScheduleItem.MonthHeader(item.yearMonth)
                is ScheduleItem.WeekHeader -> ScheduleItem.WeekHeader(item.startDate, item.endDate)
                is ScheduleItem.DayEvents -> DayWithEvents(
                    date = item.date,
                    events = item.events,
                    holidays = item.holidays,
                    onEventClick = onEventClick
                )
            }
        }
    }
}