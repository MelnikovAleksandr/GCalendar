package ru.melnikov.gcalendar.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import gcalendar.composeapp.generated.resources.Res
import gcalendar.composeapp.generated.resources.ic_add
import gcalendar.composeapp.generated.resources.ic_calendar_view_day
import gcalendar.composeapp.generated.resources.ic_calendar_view_month
import gcalendar.composeapp.generated.resources.ic_calendar_view_schedule
import gcalendar.composeapp.generated.resources.ic_calendar_view_three_day
import gcalendar.composeapp.generated.resources.ic_calendar_view_week
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import ru.melnikov.gcalendar.common.GlassShaderParams
import ru.melnikov.gcalendar.common.applyIf
import ru.melnikov.gcalendar.common.createGlassRenderEffect
import ru.melnikov.gcalendar.common.noRippleClickable
import ru.melnikov.gcalendar.ui.navigation.NavigableScreen
import ru.melnikov.gcalendar.ui.theme.GCalendarTheme
import kotlin.math.abs

@Composable
internal fun CalendarBottomNavigationBar(
    modifier: Modifier = Modifier,
    selectedView: NavigableScreen,
    onViewSelect: (NavigableScreen) -> Unit,
    onAddClick: () -> Unit,
) {
    val navItems =
        remember {
            listOf(
                NavItem(
                    NavigableScreen.Schedule,
                    Res.drawable.ic_calendar_view_schedule,
                    "Schedule",
                ),
                NavItem(NavigableScreen.Day, Res.drawable.ic_calendar_view_day, "Day"),
                NavItem(NavigableScreen.ThreeDay, Res.drawable.ic_calendar_view_three_day, "3 Day"),
                NavItem(NavigableScreen.Week, Res.drawable.ic_calendar_view_week, "Week"),
                NavItem(NavigableScreen.Month, Res.drawable.ic_calendar_view_month, "Month"),
            )
        }

    val coroutineScope = rememberCoroutineScope()
    val itemMetrics = remember { mutableStateMapOf<Int, ItemMetrics>() }
    val indicatorOffset = remember { Animatable(0f) }
    val indicatorScale = remember { Animatable(1f) }
    val boxScale = remember { Animatable(1f) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var dragStartIndex by remember { mutableStateOf(-1) }
    var indicatorWidthPx by remember { mutableFloatStateOf(0f) }
    var indicatorInitialized by remember { mutableStateOf(false) }
    var isClickAnimating by remember { mutableStateOf(false) }
    var isMorphingOut by remember { mutableStateOf(false) }

    val dragScaleFactor = 1.25f

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

    LaunchedEffect(isDragging, isClickAnimating) {
        if (isDragging || isClickAnimating) {
            indicatorScale.animateTo(
                targetValue = dragScaleFactor,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh,
                    ),
            )
        }
    }

    LaunchedEffect(isMorphingOut) {
        if (isMorphingOut) {
            indicatorScale.animateTo(
                targetValue = 1f,
                animationSpec =
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessHigh,
                    ),
            )
            isMorphingOut = false
        }
    }

    LaunchedEffect(isDragging, isClickAnimating, isMorphingOut) {
        boxScale.animateTo(
            targetValue = if (isDragging || isClickAnimating || isMorphingOut) 1.05f else 1f,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessHigh,
                ),
        )
    }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        var navBarSize by remember { mutableStateOf(IntSize.Zero) }

        val glassEffect =
            remember(
                navBarSize,
                isDragging,
                isClickAnimating,
                isMorphingOut,
                dragOffset,
                indicatorWidthPx,
                indicatorOffset.value,
                indicatorScale.value,
            ) {
                if ((isDragging || isClickAnimating || isMorphingOut) && navBarSize.width > 0 && navBarSize.height > 0 && indicatorWidthPx > 0f) {
                    val effectOffset = if (isDragging) dragOffset else indicatorOffset.value
                    val centerX = (effectOffset + indicatorWidthPx / 2f) / navBarSize.width
                    val centerY = 0.5f

                    val glassWidth =
                        (indicatorWidthPx / navBarSize.width) * 1.3f *
                                indicatorScale
                                    .value
                    val glassHeight = indicatorScale.value

                    val cornerRadius = glassHeight * 0.5f

                    createGlassRenderEffect(
                        width = navBarSize.width.toFloat(),
                        height = navBarSize.height.toFloat(),
                        params =
                            GlassShaderParams.waterDroplet().copy(
                                width = glassWidth,
                                height = glassHeight,
                                centerX = centerX,
                                centerY = centerY,
                                cornerRadius = cornerRadius,
                                thickness = 0.1f,
                                bevelWidth = 0.25f,
                            ),
                    )
                } else {
                    null
                }
            }

        Box(
            modifier =
                Modifier
                    .height(56.dp)
                    .weight(1f)
                    .onSizeChanged { navBarSize = it }
                    .graphicsLayer {
                        renderEffect = glassEffect
                        scaleX = boxScale.value
                        scaleY = boxScale.value
                    },
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                        .background(GCalendarTheme.colorScheme.surfaceContainer)
                        .padding(3.dp)
                        .pointerInput(selectedIndex) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    isDragging = true
                                    dragStartIndex = selectedIndex
                                    dragOffset = indicatorOffset.value
                                },
                                onDrag = { _, dragAmount ->
                                    val (minBound, maxBound) = dragBounds
                                    dragOffset =
                                        (dragOffset + dragAmount.x).coerceIn(minBound, maxBound)
                                },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        val indicatorCenter = dragOffset + (indicatorWidthPx / 2f)

                                        val nearestIndex =
                                            itemCenters
                                                .minByOrNull { (_, center) ->
                                                    abs(center - indicatorCenter)
                                                }?.key ?: dragStartIndex

                                        val targetPos =
                                            itemMetrics[nearestIndex]?.left ?: dragOffset

                                        indicatorOffset.animateTo(
                                            targetValue = targetPos,
                                            animationSpec =
                                                spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessHigh,
                                                ),
                                        )

                                        isDragging = false
                                        isMorphingOut = true

                                        if (nearestIndex != dragStartIndex) {
                                            currentOnViewSelect(navItems[nearestIndex].screen)
                                        }
                                    }
                                },
                            )
                        },
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
                        isDragging = isDragging,
                        isClickAnimating = isClickAnimating,
                        isMorphingOut = isMorphingOut,
                        onClick = {
                            if (index != selectedIndex && !isDragging && !isClickAnimating && !isMorphingOut) {
                                coroutineScope.launch {
                                    isClickAnimating = true
                                    val targetPos =
                                        itemMetrics[index]?.left ?: indicatorOffset.value
                                    indicatorOffset.animateTo(
                                        targetValue = targetPos,
                                        animationSpec =
                                            tween(
                                                durationMillis = 400,
                                                easing = EaseInCubic,
                                            ),
                                    )
                                    isClickAnimating = false
                                    isMorphingOut = true
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

        Spacer(modifier = Modifier.width(12.dp))

        FloatingActionButton(
            onClick = onAddClick,
            shape = CircleShape,
            containerColor = GCalendarTheme.colorScheme.primary,
            contentColor = GCalendarTheme.colorScheme.onPrimary,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = "Add Event",
            )
        }
    }
}

private data class NavItem(
    val screen: NavigableScreen,
    val icon: DrawableResource,
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
    isDragging: Boolean,
    isClickAnimating: Boolean,
    isMorphingOut: Boolean,
    onClick: () -> Unit,
    icon: DrawableResource,
    label: String,
) {
    val showBackground = selected && !isDragging && !isClickAnimating && !isMorphingOut

    Column(
        modifier =
            modifier
                .noRippleClickable(onClick = onClick)
                .weight(1f)
                .fillMaxHeight()
                .applyIf(showBackground) {
                    background(
                        color = GCalendarTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(30.dp),
                    ).padding(horizontal = 3.dp)
                },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            tint =
                if (showBackground) {
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
                if (showBackground) {
                    GCalendarTheme.colorScheme.primary
                } else {
                    GCalendarTheme.colorScheme.onSurfaceVariant
                },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}