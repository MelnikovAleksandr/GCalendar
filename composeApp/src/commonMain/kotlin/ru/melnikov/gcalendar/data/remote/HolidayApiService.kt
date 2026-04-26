package ru.melnikov.gcalendar.data.remote

import io.ktor.client.HttpClient
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.BuildKonfig
import ru.melnikov.gcalendar.data.remote.error.DataError
import ru.melnikov.gcalendar.data.remote.model.HolidayResponse

@Single
class HolidayApiService(client: HttpClient) {
    private val clientWrapper = ClientWrapper(client)
    private val baseUrl = "https://calendarific.com/api/v2/holidays"
    suspend fun getHolidays(countryCode: String, year: Int): Result<HolidayResponse, DataError> {
        return clientWrapper.networkGetUseCase<HolidayResponse>(
            baseUrl,
            mapOf(
                "api_key" to BuildKonfig.API_KEY,
                "country" to countryCode,
                "year" to year.toString()
            )
        )
    }
}