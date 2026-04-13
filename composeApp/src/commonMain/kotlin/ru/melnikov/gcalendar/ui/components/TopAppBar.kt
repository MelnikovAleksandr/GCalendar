package ru.melnikov.gcalendar.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBar as MaterialTopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skydoves.landscapist.coil3.CoilImage
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ru.melnikov.gcalendar.common.lengthOfMonth
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.common.toLocalDateTime
import ru.melnikov.gcalendar.common.toSentenceCase
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.states.DateState
import ru.melnikov.gcalendar.ui.TopBarCalendarView
import ru.melnikov.gcalendar.ui.YearMonth
import ru.melnikov.gcalendar.ui.isLeap
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    dateState: DateState,
    monthDropdownState: TopBarCalendarView,
    onMenuClick: () -> Unit,
    onSelectToday: () -> Unit,
    onToggleMonthDropdown: (TopBarCalendarView) -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    Column(
        modifier = Modifier.background(
            color = GCalendarTheme.colorScheme.primaryContainer
        ).animateContentSize()
    ) {
        val rotationDegree by animateFloatAsState(
            targetValue = if (monthDropdownState != TopBarCalendarView.NoView)
                180f else 0f,
            animationSpec = tween(
                durationMillis = 300,
                easing = EaseInCubic
            ),
            label = "rotation"
        )

        val currentYear = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).year

        val showYear = dateState.selectedInViewMonth.year != currentYear

        val monthTitle = if (showYear) {
            "${dateState.selectedInViewMonth.month.name.toSentenceCase()} ${dateState.selectedInViewMonth.year}"
        } else {
            dateState.selectedInViewMonth.month.name.toSentenceCase()
        }

        MaterialTopAppBar(
            colors = TopAppBarColors(
                containerColor = GCalendarTheme.colorScheme.onSecondaryContainer,
                scrolledContainerColor = GCalendarTheme.colorScheme.onPrimary,
                navigationIconContentColor = GCalendarTheme.colorScheme.onPrimary,
                titleContentColor = GCalendarTheme.colorScheme.onPrimary,
                actionIconContentColor = GCalendarTheme.colorScheme.onPrimary,
                subtitleContentColor = GCalendarTheme.colorScheme.onPrimary
            ),
            navigationIcon = {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu"
                    )
                }
            },
            title = {
                Row(
                    modifier = Modifier.noRippleClickable {
                        val toggleView =
                            if (monthDropdownState != TopBarCalendarView.NoView)
                                TopBarCalendarView.NoView
                            else
                                TopBarCalendarView.Month
                        onToggleMonthDropdown(toggleView)
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthTitle,
                        style = GCalendarTheme.typography.labelSmall
                    )
                    Icon(
                        modifier = Modifier.rotate(rotationDegree),
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Toggle Month Dropdown"
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    // TODO
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                }
                IconButton(onClick = { onSelectToday() }) {
                    Text(
                        text = dateState.currentDate.day.toString(),
                        style = GCalendarTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                CoilImage(
                    imageModel = {
                        "https://t4.ftcdn" +
                                ".net/jpg/00/04/09/63/360_F_4096398_nMeewldssGd7guDmvmEDXqPJUmkDWyqA" +
                                ".jpg"
                    },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                )
            },
        )

        when (monthDropdownState) {
            TopBarCalendarView.Month -> {
                TopBarMonthView(
                    month = YearMonth(
                        dateState.selectedInViewMonth.year, dateState
                            .selectedInViewMonth.month
                    ),
                    events = emptyList(),
                    holidays = emptyList(),
                    onDayClick = onDayClick,
                    selectedDay = dateState.selectedDate
                )
            }

            else -> {}
        }
    }
}

@Composable
private fun TopBarMonthView(
    month: YearMonth,
    events: List<Event>,
    holidays: List<Holiday>,
    onDayClick: (LocalDate) -> Unit,
    selectedDay: LocalDate
) {
    val firstDayOfMonth = LocalDate(month.year, month.month, 1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.ordinal + 1
    val daysInMonth = month.month.lengthOfMonth(month.year.isLeap())
    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
    ) {

        item(span = { GridItemSpan(7) }) {
            TopAppBarWeekdayHeader()
        }

        items(firstDayOfWeek) {
            TopAppBarEmptyPagingDayCell()
        }

        items(daysInMonth) { day ->
            val date = LocalDate(month.year, month.month, day + 1)
            TopAppBarDayCell(
                date = date,
                events = events.filter { event ->
                    event.startTime.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                holidays = holidays.filter { holiday ->
                    holiday.date.toLocalDateTime(TimeZone.currentSystemDefault()).date == date
                },
                isSelected = date == selectedDay,
                onDayClick = onDayClick
            )
        }
    }
}

@Composable
private fun TopAppBarWeekdayHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")

        daysOfWeek.forEach { day ->
            Text(
                text = day,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = GCalendarTheme.colorScheme.onPrimary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TopAppBarDayCell(
    date: LocalDate,
    events: List<Event>,
    holidays: List<Holiday>,
    isSelected: Boolean,
    onDayClick: (LocalDate) -> Unit
) {
    val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    val isToday = date == today

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                when {
                    isSelected -> GCalendarTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                    isToday -> GCalendarTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
                    else -> Color.Transparent
                }
            )
            .clickable { onDayClick(date) }
            .padding(4.dp)
    ) {
        Column {
            Text(
                text = date.day.toString(),
                fontSize = 12.sp,
                style = GCalendarTheme.typography.bodyMedium,
                color = when {
                    isToday -> GCalendarTheme.colorScheme.onPrimary
                    else -> GCalendarTheme.colorScheme.onPrimary
                },
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            holidays.firstOrNull()?.let { holiday ->
                Text(
                    text = holiday.name,
                    style = GCalendarTheme.typography.bodyMedium,
                    color = Color(0xFF2196F3),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(top = 2.dp)
                        .fillMaxWidth()
                        .background(Color(0xFF2196F3).copy(alpha = 0.1f), RoundedCornerShape(2.dp))
                        .padding(2.dp)
                )
            }

            val maxEventsToDisplay = 3
            val displayedEvents = events.take(maxEventsToDisplay)

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp)
            ) {
                displayedEvents.forEach { event ->
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(
                                Color((event.color ?: 0xFFE91E63) as Int),
                                CircleShape
                            )
                            .padding(1.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                }

                if (events.size > maxEventsToDisplay) {
                    Text(
                        text = "+${events.size - maxEventsToDisplay}",
                        fontSize = 10.sp,
                        color = GCalendarTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun TopAppBarEmptyPagingDayCell() {
    Box(
        modifier = Modifier.padding(4.dp)
    )
}

@Preview
@Composable
fun TopAppBarPreview() {
    GCalendarTheme {
        TopAppBar(
            dateState = DateState(
                currentDate = LocalDate(2025, 12, 12),
                selectedDate = LocalDate(2025, 12, 12),
                selectedInViewMonth = YearMonth(2025, 12),
                viewStartDate = LocalDate(2025, 12, 12)
            ),
            monthDropdownState = TopBarCalendarView.Week,
            onMenuClick = {},
            onSelectToday = {},
            onToggleMonthDropdown = {},
            onDayClick = {}
        )
    }
}