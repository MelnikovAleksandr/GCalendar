@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.ui.state

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.common.YearMonth
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Single
class DateStateHolder {
    @OptIn(ExperimentalTime::class)
    val date: LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private val _currentDateState = MutableStateFlow(
        DateState(
            currentDate = date,
            selectedDate = date,
            selectedInViewMonth = YearMonth(date.year, date.month.number),
        )
    )

    val currentDateState: StateFlow<DateState> = _currentDateState

    fun updateSelectedInViewMonthState(selectedInViewMonth: YearMonth) {
        _currentDateState.update { current ->
            current.copy(selectedInViewMonth = selectedInViewMonth)
        }
    }

    fun updateSelectedDateState(selectedDate: LocalDate) {
        _currentDateState.update { current ->
            current.copy(
                selectedDate = selectedDate,
                selectedInViewMonth = YearMonth(selectedDate.year, selectedDate.month),
            )
        }
    }

    @OptIn(ExperimentalTime::class)
    fun resetToToday() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        _currentDateState.update { current ->
            current.copy(
                selectedDate = today,
                selectedInViewMonth = YearMonth(today.year, today.month.number),
            )
        }
    }
}