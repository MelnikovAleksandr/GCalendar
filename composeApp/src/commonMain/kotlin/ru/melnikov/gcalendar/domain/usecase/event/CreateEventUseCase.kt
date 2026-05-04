package ru.melnikov.gcalendar.domain.usecase.event

import org.koin.core.annotation.Factory
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.repository.IEventRepository
import ru.melnikov.gcalendar.domain.utils.DomainError
import ru.melnikov.gcalendar.domain.utils.DomainResult
import ru.melnikov.gcalendar.domain.utils.EventValidationException
import ru.melnikov.gcalendar.domain.utils.EventValidator

@Factory
class CreateEventUseCase(
    private val eventRepository: IEventRepository
) {

    suspend operator fun invoke(event: Event): DomainResult<Unit> {
        return try {
            EventValidator.validate(
                title = event.title,
                startTime = event.startTime,
                endTime = event.endTime,
                calendarId = event.calendarId,
                isAllDay = event.isAllDay
            )

            eventRepository.addEvent(event)
            DomainResult.Success(Unit)
        } catch (e: EventValidationException) {
            DomainResult.Error(DomainError.ValidationError(e.message ?: "Validation failed"))
        } catch (e: Exception) {
            DomainResult.Error(DomainError.Unknown(e.message ?: "Failed to create event"))
        }
    }
}