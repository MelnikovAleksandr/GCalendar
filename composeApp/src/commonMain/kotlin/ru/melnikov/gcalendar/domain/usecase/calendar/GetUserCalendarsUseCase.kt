package ru.melnikov.gcalendar.domain.usecase.calendar

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import ru.melnikov.gcalendar.domain.model.Calendar
import ru.melnikov.gcalendar.domain.repository.ICalendarRepository

@Factory
class GetUserCalendarsUseCase(
    private val calendarRepository: ICalendarRepository
) {
    operator fun invoke(userId: String): Flow<List<Calendar>> =
        calendarRepository.getCalendarsForUser(userId)
}