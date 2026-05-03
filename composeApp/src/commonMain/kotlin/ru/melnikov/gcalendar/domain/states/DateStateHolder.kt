@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.domain.states

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    val date = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val _currentDateState = MutableStateFlow(
        DateState(
            date,
            date,
            YearMonth(date.year, date.month.number),
        )
    )
    val currentDateState: StateFlow<DateState> = _currentDateState
    fun updateSelectedInViewMonthState(selectedInViewMonth: YearMonth) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                selectedInViewMonth = selectedInViewMonth
            )
        )
    }

    fun updateSelectedDateState(selectedDate: LocalDate) {
        _currentDateState.tryEmit(
            _currentDateState.value.copy(
                selectedDate = selectedDate,
                selectedInViewMonth = YearMonth(selectedDate.year, selectedDate.month),
            )
        )
    }
}