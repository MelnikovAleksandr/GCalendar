package ru.melnikov.gcalendar.data.store

import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreBuilder
import ru.melnikov.gcalendar.common.AppLogger
import ru.melnikov.gcalendar.common.DateRangeHelper
import ru.melnikov.gcalendar.common.asHoliday
import ru.melnikov.gcalendar.common.asHolidayEntity
import ru.melnikov.gcalendar.data.local.HolidayDao
import ru.melnikov.gcalendar.data.remote.HolidayApiService
import ru.melnikov.gcalendar.data.remote.Result
import ru.melnikov.gcalendar.domain.model.Holiday

object HolidayStoreFactory {

    fun create(
        holidayApiService: HolidayApiService,
        holidayDao: HolidayDao
    ): Store<HolidayKey, List<Holiday>> {
        return StoreBuilder.from(
            fetcher = createFetcher(holidayApiService),
            sourceOfTruth = createSourceOfTruth(holidayDao)
        )
            .validator(HolidayValidator.create())
            .build()
    }

    private fun createFetcher(
        holidayApiService: HolidayApiService
    ): Fetcher<HolidayKey, List<Holiday>> = Fetcher.of { key ->
        AppLogger.d { "Fetching holidays for ${key.countryCode}, year ${key.year}" }
        when (val response = holidayApiService.getHolidays(key.countryCode, key.year)) {
            is Result.Error -> {
                AppLogger.e { "Failed to fetch holidays: ${response.error}" }
                throw StoreException("Failed to fetch holidays: ${response.error}")
            }
            is Result.Success -> {
                AppLogger.d { "Fetched ${response.data.response.holidays.size} holidays" }
                HolidayValidator.recordFetch(key)
                response.data.response.holidays.map { it.asHoliday() }
            }
        }
    }

    private fun createSourceOfTruth(
        holidayDao: HolidayDao
    ): SourceOfTruth<HolidayKey, List<Holiday>, List<Holiday>> = SourceOfTruth.of(
        reader = { key ->
            val (startDate, endDate) = getDateRangeForYear(key.year)
            holidayDao.getHolidaysInRange(startDate, endDate).map { entities ->
                entities
                    .filter { it.countryCode == key.countryCode.lowercase() }
                    .map { it.asHoliday() }
            }
        },
        writer = { _, holidays ->
            holidayDao.insertHolidays(holidays.map { it.asHolidayEntity() })
        },
        delete = { _ -> },
        deleteAll = {}
    )

    private fun getDateRangeForYear(year: Int): Pair<Long, Long> =
        DateRangeHelper.getYearRange(year)
}

class StoreException(message: String, cause: Throwable? = null) : Exception(message, cause)