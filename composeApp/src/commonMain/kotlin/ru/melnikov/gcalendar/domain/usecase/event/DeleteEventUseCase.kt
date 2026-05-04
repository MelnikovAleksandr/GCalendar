package ru.melnikov.gcalendar.domain.usecase.event

import org.koin.core.annotation.Factory
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.repository.IEventRepository
import ru.melnikov.gcalendar.domain.utils.DomainError
import ru.melnikov.gcalendar.domain.utils.DomainResult

@Factory
class DeleteEventUseCase(
    private val eventRepository: IEventRepository
) {

    suspend operator fun invoke(event: Event): DomainResult<Unit> {
        return try {
            eventRepository.deleteEvent(event)
            DomainResult.Success(Unit)
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e.message ?: "Failed to delete event"))
        }
    }
}