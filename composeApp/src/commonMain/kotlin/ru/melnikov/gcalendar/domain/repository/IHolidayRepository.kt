package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.melnikov.gcalendar.domain.model.Holiday

interface IHolidayRepository {
    suspend fun updateHolidays(
        countryCode: String,
        year: Int,
    )

    fun getHolidaysForYear(
        countryCode: String,
        year: Int,
    ): Flow<List<Holiday>>
}