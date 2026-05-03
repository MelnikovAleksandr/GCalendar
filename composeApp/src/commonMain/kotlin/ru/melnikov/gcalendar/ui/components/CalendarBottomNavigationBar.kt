package ru.melnikov.gcalendar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import compose.icons.FontAwesomeIcons
import compose.icons.fontawesomeicons.Regular
import compose.icons.fontawesomeicons.Solid
import compose.icons.fontawesomeicons.regular.ListAlt
import compose.icons.fontawesomeicons.solid.CalendarAlt
import compose.icons.fontawesomeicons.solid.CalendarDay
import compose.icons.fontawesomeicons.solid.CalendarWeek
import compose.icons.fontawesomeicons.solid.Plus
import kotlinx.coroutines.launch
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.ui.navigation.NavigableScreen
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun CalendarBottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedView: NavigableScreen,
    onViewSelect: (NavigableScreen) -> Unit,
    onAddClick: () -> Unit,
) {
    // Define navigation items
    val navItems =
        remember {
            listOf(
                NavItem(NavigableScreen.Schedule, FontAwesomeIcons.Regular.ListAlt, "Schedule"),
                NavItem(NavigableScreen.Day, FontAwesomeIcons.Solid.CalendarDay, "Day"),
                NavItem(NavigableScreen.ThreeDay, FontAwesomeIcons.Solid.CalendarAlt, "3 Day"),
                NavItem(NavigableScreen.Week, FontAwesomeIcons.Solid.CalendarWeek, "Week"),
                NavItem(NavigableScreen.Month, FontAwesomeIcons.Solid.CalendarAlt, "Month"),
            )
        }

    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val itemMetrics = remember { mutableStateMapOf<Int, ItemMetrics>() }
    val indicatorOffset = remember { Animatable(0f) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartIndex by remember { mutableStateOf(-1) }
    var indicatorWidthPx by remember { mutableFloatStateOf(0f) }
    var indicatorInitialized by remember { mutableStateOf(false) }

    val selectedIndex =
        navItems.indexOfFirst {
            it.screen == selectedView
        }

    val currentOnViewSelect by rememberUpdatedState(onViewSelect)

    val dragBounds by remember {
        derivedStateOf {
            if (itemMetrics.size == navItems.size && navItems.isNotEmpty()) {
                val firstLeft = itemMetrics[0]?.left ?: 0f
                val lastLeft = itemMetrics[navItems.size - 1]?.left ?: 0f
                firstLeft to lastLeft
            } else {
                0f to 0f
            }
        }
    }

    val itemCenters by remember {
        derivedStateOf {
            itemMetrics.mapValues { (_, metrics) ->
                metrics.left + (metrics.width / 2f)
            }
        }
    }

    LaunchedEffect(selectedIndex, itemMetrics.size, indicatorWidthPx, isDragging) {
        if (
            !isDragging &&
            selectedIndex >= 0 &&
            itemMetrics.containsKey(selectedIndex)
        ) {
            indicatorWidthPx = itemMetrics[selectedIndex]?.width ?: indicatorWidthPx
            val targetPos = itemMetrics[selectedIndex]?.left ?: return@LaunchedEffect
            if (indicatorInitialized && abs(indicatorOffset.value - targetPos) >= 0.5f) {
                indicatorOffset.animateTo(
                    targetValue = targetPos,
                    animationSpec =
                        spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessHigh,
                        ),
                )
            } else {
                indicatorOffset.snapTo(targetPos)
                indicatorInitialized = true
            }
        }
    }

    LaunchedEffect(itemMetrics.size) {
        if (itemMetrics.size != navItems.size) {
            indicatorInitialized = false
            return@LaunchedEffect
        }
        if (selectedIndex < 0) return@LaunchedEffect
        if (!indicatorInitialized) {
            val initialPos = itemMetrics[selectedIndex]?.left ?: 0f
            indicatorOffset.snapTo(initialPos)
            indicatorInitialized = true
        }
    }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Surface(
            modifier = Modifier.height(56.dp).weight(1f),
            shape = RoundedCornerShape(30.dp),
            color = GCalendarTheme.colorScheme.surfaceContainer,
        ) {
            Box(
                modifier =
                    Modifier
                        .padding(3.dp)
                        .pointerInput(selectedIndex) {
                            detectHorizontalDragGestures(
                                onDragStart = {
                                    isDragging = true
                                    dragStartIndex = selectedIndex
                                    dragOffset = indicatorOffset.value
                                },
                                onHorizontalDrag = { _, dragAmount ->
                                    val (minBound, maxBound) = dragBounds
                                    dragOffset = (dragOffset + dragAmount).coerceIn(minBound, maxBound)
                                },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        val indicatorCenter = dragOffset + (indicatorWidthPx / 2f)

                                        val nearestIndex =
                                            itemCenters
                                                .minByOrNull { (_, center) ->
                                                    abs(center - indicatorCenter)
                                                }?.key ?: dragStartIndex

                                        val targetPos = itemMetrics[nearestIndex]?.left ?: dragOffset

                                        indicatorOffset.animateTo(
                                            targetValue = targetPos,
                                            animationSpec =
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessHigh,
                                                ),
                                        )

                                        isDragging = false

                                        if (nearestIndex != dragStartIndex) {
                                            currentOnViewSelect(navItems[nearestIndex].screen)
                                        }
                                    }
                                },
                            )
                        },
            ) {
                if (itemMetrics.size == navItems.size && indicatorWidthPx > 0f) {
                    val currentOffset = if (isDragging) dragOffset else indicatorOffset.value
                    Box(
                        modifier =
                            Modifier
                                .offset { IntOffset(currentOffset.roundToInt(), 0) }
                                .width(with(density) { indicatorWidthPx.toDp() })
                                .fillMaxHeight()
                                .background(
                                    color = GCalendarTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(30.dp),
                                ),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    navItems.forEachIndexed { index, navItem ->
                        BottomNavItem(
                            modifier =
                                Modifier.onGloballyPositioned { coordinates ->
                                    itemMetrics[index] =
                                        ItemMetrics(
                                            left = coordinates.positionInParent().x,
                                            width = coordinates.size.width.toFloat(),
                                        )
                                    if (indicatorWidthPx == 0f || index == selectedIndex) {
                                        indicatorWidthPx = coordinates.size.width.toFloat()
                                    }
                                },
                            selected = selectedIndex == index,
                            onClick = {
                                if (index != selectedIndex && !isDragging) {
                                    coroutineScope.launch {
                                        val targetPos =
                                            itemMetrics[index]?.left ?: indicatorOffset.value
                                        indicatorOffset.animateTo(
                                            targetValue = targetPos,
                                            animationSpec =
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessHigh,
                                                ),
                                        )
                                        currentOnViewSelect(navItem.screen)
                                    }
                                }
                            },
                            icon = navItem.icon,
                            label = navItem.label,
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = GCalendarTheme.colorScheme.primary,
            contentColor = GCalendarTheme.colorScheme.onPrimary,
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = FontAwesomeIcons.Solid.Plus,
                contentDescription = "Add Event",
            )
        }
    }
}

private data class NavItem(
    val screen: NavigableScreen,
    val icon: ImageVector,
    val label: String,
)

private data class ItemMetrics(
    val left: Float,
    val width: Float,
)

@Composable
private fun RowScope.BottomNavItem(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    label: String,
) {
    Column(
        modifier =
            modifier
                .noRippleClickable(onClick = onClick)
                .weight(1f)
                .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint =
                if (selected) {
                    GCalendarTheme.colorScheme.primary
                } else {
                    GCalendarTheme.colorScheme.onSurfaceVariant
                },
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = GCalendarTheme.typography.labelSmall.copy(fontSize = 9.sp),
            color =
                if (selected) {
                    GCalendarTheme.colorScheme.primary
                } else {
                    GCalendarTheme.colorScheme.onSurfaceVariant
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}