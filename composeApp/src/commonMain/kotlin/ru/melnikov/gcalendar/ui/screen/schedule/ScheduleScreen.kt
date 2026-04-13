package ru.melnikov.gcalendar.ui.screen.schedule

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateStateHolder
import ru.melnikov.gcalendar.ui.YearMonth
import ru.melnikov.gcalendar.ui.screen.schedule.components.DayWithEvents

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

    val scheduleState = remember(currentYearMonth, events, holidays) {
        ScheduleState(
            initialMonth = currentYearMonth,
            events = events,
            holidays = holidays
        )
    }

    LaunchedEffect(currentYearMonth) {
        dateStateHolder.updateSelectedInViewMonthState(currentYearMonth)
    }

    val listState = rememberLazyListState()

    LaunchedEffect(scheduleState.initialScrollIndex) {
        if (scheduleState.initialScrollIndex > 0) {
            listState.scrollToItem(scheduleState.initialScrollIndex)
        }
    }

    LaunchedEffect(listState) {
        launch {
            snapshotFlow {
                val firstVisible = listState.firstVisibleItemIndex
                val visibleCount = listState.layoutInfo.visibleItemsInfo.size

                (firstVisible until firstVisible + visibleCount)
                    .firstOrNull { idx ->
                        idx < scheduleState.items.size &&
                                scheduleState.items[idx] is ScheduleItem.MonthHeader
                    }
                    ?.let { scheduleState.items[it] as? ScheduleItem.MonthHeader }
            }
                .filterNotNull()
                .distinctUntilChanged()
                .collect { header ->
                    dateStateHolder.updateSelectedInViewMonthState(header.yearMonth)
                }
        }

        launch {
            snapshotFlow { listState.firstVisibleItemIndex < ScheduleState.THRESHOLD }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        val firstVisibleIndex = listState.firstVisibleItemIndex
                        val newItemsCount = scheduleState.loadMoreBackward()

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
                lastVisibleIndex >= scheduleState.items.size - ScheduleState.THRESHOLD
            }
                .distinctUntilChanged()
                .collect { needsMore ->
                    if (needsMore) {
                        scheduleState.loadMoreForward()
                    }
                }
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize()
    ) {
        itemsIndexed(
            items = scheduleState.items,
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

class ScheduleState(
    initialMonth: YearMonth,
    val events: List<Event>,
    val holidays: List<Holiday>
) {
    private val _items = mutableStateListOf<ScheduleItem>()
    val items: List<ScheduleItem> = _items

    private val monthRange = MonthRange(initialMonth)
    val initialScrollIndex: Int

    init {
        val initialItems = createScheduleItemsForMonthRange(
            monthRange.getMonths(),
            events,
            holidays
        )
        _items.addAll(initialItems)

        initialScrollIndex = _items.indexOfFirst {
            it is ScheduleItem.MonthHeader &&
                    it.yearMonth.year == initialMonth.year &&
                    it.yearMonth.month == initialMonth.month
        }.coerceAtLeast(0)
    }

    fun loadMoreBackward(): Int {
        monthRange.expandBackward()
        val newMonths = monthRange.getLastAddedMonthsBackward()
        val newItems = createScheduleItemsForMonthRange(newMonths, events, holidays)

        if (newItems.isNotEmpty()) {
            _items.addAll(0, newItems)
            return newItems.size
        }
        return 0
    }

    fun loadMoreForward(): Int {
        monthRange.expandForward()
        val newMonths = monthRange.getLastAddedMonthsForward()
        val newItems = createScheduleItemsForMonthRange(newMonths, events, holidays)

        if (newItems.isNotEmpty()) {
            _items.addAll(newItems)
            return newItems.size
        }
        return 0
    }

    companion object {
        const val THRESHOLD = 10
    }
}