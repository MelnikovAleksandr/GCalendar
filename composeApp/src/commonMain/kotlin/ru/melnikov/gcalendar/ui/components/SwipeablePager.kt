package ru.melnikov.gcalendar.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun <T> SwipeablePager(
    modifier: Modifier = Modifier,
    currentReference: T,
    calculateOffset: (current: T, base: T) -> Int,
    pageToReference: (baseReference: T, initialPage: Int, page: Int) -> T,
    onReferenceChange: (T) -> Unit,
    content: @Composable (reference: T) -> Unit,
) {
    val totalPages = 10000
    val initialPage = totalPages / 2
    val baseReference = remember { currentReference }

    val referenceOffset =
        remember(currentReference, baseReference) {
            calculateOffset(currentReference, baseReference)
        }

    val pagerState =
        rememberPagerState(
            initialPage = initialPage + referenceOffset,
            pageCount = { totalPages },
        )

    val pageConverter: (Int) -> T =
        remember(baseReference, initialPage) {
            { page ->
                pageToReference(baseReference, initialPage, page)
            }
        }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .collect { page ->
                val newReference = pageConverter(page)
                if (newReference != currentReference) {
                    onReferenceChange(newReference)
                }
            }
    }

    LaunchedEffect(currentReference) {
        val targetOffset = calculateOffset(currentReference, baseReference)
        val targetPage = initialPage + targetOffset
        if (pagerState.settledPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    HorizontalPager(
        state = pagerState,
    ) { page ->
        val reference = pageConverter(page)
        content(reference)
    }
}
