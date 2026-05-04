package ru.melnikov.gcalendar.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.usecase.event.CreateEventUseCase
import ru.melnikov.gcalendar.domain.usecase.event.DeleteEventUseCase
import ru.melnikov.gcalendar.domain.usecase.event.UpdateEventUseCase
import ru.melnikov.gcalendar.domain.utils.onError
import ru.melnikov.gcalendar.domain.utils.onSuccess

data class EventUiState(
    val selectedEvent: Event? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@KoinViewModel
class EventViewModel(
    private val createEventUseCase: CreateEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    fun selectEvent(event: Event) {
        _uiState.update { it.copy(selectedEvent = event) }
    }

    fun clearSelectedEvent() {
        _uiState.update { it.copy(selectedEvent = null) }
    }

    fun addEvent(event: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            createEventUseCase(event)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                }.onError { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun editEvent(event: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            updateEventUseCase(event)
                .onSuccess {
                    _uiState.update {
                        it.copy(selectedEvent = null, isLoading = false, errorMessage = null)
                    }
                }.onError { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            deleteEventUseCase(event)
                .onSuccess {
                    _uiState.update {
                        it.copy(selectedEvent = null, isLoading = false, errorMessage = null)
                    }
                }.onError { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}