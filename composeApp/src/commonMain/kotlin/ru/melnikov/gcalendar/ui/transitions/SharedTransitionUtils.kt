package ru.melnikov.gcalendar.ui.transitions

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.datetime.LocalDate
import ru.melnikov.gcalendar.ui.theme.LocalSharedTransitionScope

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedDateElement(
    date: LocalDate,
    type: SharedElementType,
    isVisible: Boolean,
): Modifier = composed {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val key = remember(date, type) { DateSharedElementKey(date, type) }

    with(sharedTransitionScope) {
        this@composed.sharedElementWithCallerManagedVisibility(
            sharedContentState = rememberSharedContentState(key = key),
            visible = isVisible,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedEventElement(
    eventId: String,
    type: SharedElementType,
    isVisible: Boolean,
): Modifier = composed {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val key = remember(eventId, type) { EventSharedElementKey(eventId, type) }

    with(sharedTransitionScope) {
        this@composed.sharedElementWithCallerManagedVisibility(
            sharedContentState = rememberSharedContentState(key = key),
            visible = isVisible,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedTimeColumn(isVisible: Boolean): Modifier = composed {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val key = remember { TimeColumnSharedElementKey() }

    with(sharedTransitionScope) {
        this@composed.sharedElementWithCallerManagedVisibility(
            sharedContentState = rememberSharedContentState(key = key),
            visible = isVisible,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
fun Modifier.sharedDayColumn(
    date: LocalDate,
    isVisible: Boolean,
): Modifier = composed {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    val key = remember(date) { DateSharedElementKey(date, SharedElementType.DayColumn) }

    with(sharedTransitionScope) {
        this@composed.sharedElementWithCallerManagedVisibility(
            sharedContentState = rememberSharedContentState(key = key),
            visible = isVisible,
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun WithSharedTransitionScope(content: @Composable SharedTransitionScope.() -> Unit) {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    content(sharedTransitionScope)
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun isSharedTransitionActive(): Boolean {
    val sharedTransitionScope = LocalSharedTransitionScope.current
    return sharedTransitionScope.isTransitionActive
}