package ru.melnikov.gcalendar.domain.usecase.holiday

import org.koin.core.annotation.Factory
import ru.melnikov.gcalendar.domain.repository.IHolidayRepository

@Factory
class RefreshHolidaysUseCase(
    private val holidayRepository: IHolidayRepository
) {
    suspend operator fun invoke(countryCode: String, year: Int) {
        holidayRepository.updateHolidays(countryCode, year)
    }
}