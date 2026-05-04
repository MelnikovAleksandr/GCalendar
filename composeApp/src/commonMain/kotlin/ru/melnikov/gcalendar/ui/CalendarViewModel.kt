@file:OptIn(ExperimentalTime::class, ExperimentalAtomicApi::class)

package ru.melnikov.gcalendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import ru.melnikov.gcalendar.common.AppLogger
import ru.melnikov.gcalendar.common.DateUtils
import ru.melnikov.gcalendar.domain.repository.ICalendarRepository
import ru.melnikov.gcalendar.domain.repository.IEventRepository
import ru.melnikov.gcalendar.domain.repository.IUserRepository
import ru.melnikov.gcalendar.domain.states.CalendarUiState
import ru.melnikov.gcalendar.domain.usecase.calendar.GetUserCalendarsUseCase
import ru.melnikov.gcalendar.domain.usecase.event.GetEventsForDateRangeUseCase
import ru.melnikov.gcalendar.domain.usecase.holiday.GetHolidaysForYearUseCase
import ru.melnikov.gcalendar.domain.usecase.holiday.RefreshHolidaysUseCase
import ru.melnikov.gcalendar.domain.usecase.user.GetCurrentUserUseCase
import ru.melnikov.gcalendar.domain.utils.DomainError
import ru.melnikov.gcalendar.ui.state.DateStateHolder
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.ExperimentalTime

@KoinViewModel
class CalendarViewModel(
    private val userRepository: IUserRepository,
    private val calendarRepository: ICalendarRepository,
    private val eventRepository: IEventRepository,
    private val dateStateHolder: DateStateHolder,
    getUserCalendarsUseCase: GetUserCalendarsUseCase,
    getEventsForDateRangeUseCase: GetEventsForDateRangeUseCase,
    private val getHolidaysForYearUseCase: GetHolidaysForYearUseCase,
    private val refreshHolidaysUseCase: RefreshHolidaysUseCase,
    getCurrentUserUseCase: GetCurrentUserUseCase,
) : ViewModel() {
    private val userId = getCurrentUserUseCase()
    private val dateRange = DateUtils.getDateRange()
    private val currentDate = dateRange.currentDate
    private val startTime = dateRange.startTime
    private val endTime = dateRange.endTime
    private val _uiState = MutableStateFlow(CalendarUiState(isLoading = true))

    @OptIn(ExperimentalAtomicApi::class)
    private val isInitialized = AtomicBoolean(false)
    private val users =
        userRepository
            .getAllUsers()
            .catch { exception ->
                handleError("Failed to load users", exception)
                emit(emptyList())
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val holidays =
        dateStateHolder.currentDateState
            .map { it.selectedInViewMonth.year }
            .distinctUntilChanged()
            .flatMapLatest { year ->
                combine(
                    getHolidaysForYearUseCase("RU", year - 1),
                    getHolidaysForYearUseCase("RU", year),
                    getHolidaysForYearUseCase("RU", year + 1),
                ) { prevYear, currentYear, nextYear ->
                    (prevYear + currentYear + nextYear).distinctBy { it.date }
                }
            }
            .catch { exception ->
                handleError("Failed to load holidays", exception)
                emit(emptyList())
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )

    private val calendars =
        getUserCalendarsUseCase(userId)
            .catch { exception ->
                handleError("Failed to load calendars", exception)
                emit(emptyList())
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )

    private val events =
        getEventsForDateRangeUseCase(userId, startTime, endTime)
            .catch { exception ->
                handleError("Failed to load events", exception)
                emit(emptyList())
            }.shareIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                replay = 1,
            )

    @OptIn(FlowPreview::class)
    val uiState =
        combine(
            _uiState,
            users.distinctUntilChanged(),
            holidays.distinctUntilChanged(),
            calendars.distinctUntilChanged(),
            events.distinctUntilChanged(),
        ) { currentState, usersList, holidaysList, calendarsList, eventsList ->
            currentState.copy(
                accounts = usersList.toImmutableList(),
                holidays = holidaysList.toImmutableList(),
                calendars = calendarsList.toImmutableList(),
                events = eventsList.toImmutableList(),
                isLoading = false,
            )
        }.distinctUntilChanged()
            .debounce(30)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = CalendarUiState(isLoading = true),
            )

    init {
        initializeData()
    }

    @OptIn(ExperimentalAtomicApi::class)
    private fun initializeData() {
        if (isInitialized.compareAndSet(expectedValue = false, newValue = true)) {
            viewModelScope.launch {
                try {
                    val initJobs =
                        listOf(
                            async {
                                initializeUsers()
                                initializeCalendars()
                                initializeEvents()
                            },
                            async { initializeHolidays() },
                        )
                    initJobs.awaitAll()
                } catch (exception: Exception) {
                    handleError("Initialization failed", exception)
                } finally {
                    updateLoadingState(false)
                }
            }
        }
    }

    private suspend fun initializeUsers() {
        runCatching {
            userRepository.getUserFromApi()
        }.onFailure { exception ->
            handleError("Failed to initialize users", exception)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun initializeHolidays() {
        runCatching {
            dateStateHolder.currentDateState
                .map { it.selectedInViewMonth.year }
                .distinctUntilChanged()
                .flatMapLatest { year ->
                    combine(
                        getHolidaysForYearUseCase("RU", year - 1).map { (year - 1) to it },
                        getHolidaysForYearUseCase("RU", year).map { year to it },
                        getHolidaysForYearUseCase("RU", year + 1).map { (year + 1) to it },
                    ) { prev, current, next -> listOf(prev, current, next) }
                }
                .collect { yearHolidayPairs ->
                    yearHolidayPairs.forEach { (yr, holidays) ->
                        if (holidays.isEmpty()) {
                            viewModelScope.launch {
                                runCatching {
                                    refreshHolidaysUseCase("RU", yr)
                                }.onFailure { exception ->
                                    AppLogger.w(exception) { "Failed to refresh holidays for year $yr" }
                                }
                            }
                        }
                    }
                }
        }.onFailure { exception ->
            handleError("Failed to initialize holidays", exception)
        }
    }

    private suspend fun initializeCalendars() {
        runCatching {
            calendarRepository.refreshCalendarsForUser(userId)
        }.onFailure { exception ->
            handleError("Failed to initialize calendars", exception)
        }
    }

    private suspend fun initializeEvents() {
        runCatching {
            eventRepository.syncEventsForCalendar(emptyList(), startTime, endTime)
        }.onFailure { exception ->
            handleError("Failed to initialize events", exception)
        }
    }

    private fun updateState(update: (CalendarUiState) -> CalendarUiState) {
        _uiState.update(update)
    }

    private fun updateLoadingState(isLoading: Boolean) {
        updateState { it.copy(isLoading = isLoading) }
    }

    private fun handleError(
        message: String,
        exception: Throwable,
    ) {
        AppLogger.e(exception) { "CalendarViewModel: $message" }
        val errorMessage = "$message: ${exception.message ?: "Unknown error"}"
        updateState { currentState ->
            currentState.copy(
                isLoading = false,
                error = DomainError.Unknown(errorMessage),
            )
        }
    }

    fun clearError() {
        updateState { it.copy(error = null) }
    }

    @OptIn(ExperimentalAtomicApi::class)
    override fun onCleared() {
        super.onCleared()
        isInitialized.store(false)
    }
}