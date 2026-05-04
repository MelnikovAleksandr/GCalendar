package ru.melnikov.gcalendar.domain.usecase.event

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.repository.IEventRepository

@Factory
class GetEventsForDateRangeUseCase(
    private val eventRepository: IEventRepository
) {
    operator fun invoke(userId: String, startTime: Long, endTime: Long): Flow<List<Event>> =
        eventRepository.getEventsForCalendarsInRange(userId, startTime, endTime)
}