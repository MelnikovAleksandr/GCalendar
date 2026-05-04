@file:OptIn(ExperimentalTime::class)

package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.store5.Store
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import ru.melnikov.gcalendar.data.store.HolidayKey
import ru.melnikov.gcalendar.domain.model.Holiday
import kotlin.time.ExperimentalTime

@Single(binds = [IHolidayRepository::class])
class HolidayRepository(
    private val holidayStore: Store<HolidayKey, List<Holiday>>,
) : BaseRepository(), IHolidayRepository {

    override suspend fun updateHolidays(
        countryCode: String,
        year: Int,
    ) = safeCallOrThrow("updateHolidays($countryCode, $year)") {
        val key = HolidayKey(countryCode, year)
        val request = StoreReadRequest.fresh(key)
        holidayStore.stream(request).collect { response ->
            when (response) {
                is StoreReadResponse.Data -> {
                    logDebug { "Successfully refreshed holidays for $countryCode, $year: ${response.value.size} holidays" }
                    return@collect
                }
                is StoreReadResponse.Error.Exception -> {
                    throw response.error
                }
                is StoreReadResponse.Error.Message -> {
                    throw RepositoryException(response.message)
                }
                else -> {}
            }
        }
    }

    override fun getHolidaysForYear(
        countryCode: String,
        year: Int,
    ): Flow<List<Holiday>> {
        val key = HolidayKey(countryCode, year)

        return safeFlow(
            flowName = "getHolidaysForYear($countryCode, $year)",
            defaultValue = emptyList(),
            flow = holidayStore.stream(StoreReadRequest.cached(key, refresh = true))
                .filterIsInstance<StoreReadResponse.Data<List<Holiday>>>()
                .map { it.value }
        )
    }
}