package ru.melnikov.gcalendar.domain.usecase.holiday

import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Factory
import ru.melnikov.gcalendar.domain.model.Holiday
import ru.melnikov.gcalendar.domain.repository.IHolidayRepository

@Factory
class GetHolidaysForYearUseCase(
    private val holidayRepository: IHolidayRepository
) {
    operator fun invoke(countryCode: String, year: Int): Flow<List<Holiday>> =
        holidayRepository.getHolidaysForYear(countryCode, year)
}