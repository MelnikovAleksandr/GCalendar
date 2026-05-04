@file:OptIn(ExperimentalStoreApi::class)

package ru.melnikov.gcalendar.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.ksp.generated.module
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Bookkeeper
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.Store
import ru.melnikov.gcalendar.data.local.AppDatabase
import ru.melnikov.gcalendar.data.local.CalendarDao
import ru.melnikov.gcalendar.data.local.EventDao
import ru.melnikov.gcalendar.data.local.HolidayDao
import ru.melnikov.gcalendar.data.local.SyncFailureDao
import ru.melnikov.gcalendar.data.local.UserDao
import ru.melnikov.gcalendar.data.remote.HolidayApiService
import ru.melnikov.gcalendar.data.remote.RemoteCalendarApiService
import ru.melnikov.gcalendar.data.store.EventBookkeeperFactory
import ru.melnikov.gcalendar.data.store.EventKey
import ru.melnikov.gcalendar.data.store.EventStoreFactory
import ru.melnikov.gcalendar.data.store.HolidayKey
import ru.melnikov.gcalendar.data.store.HolidayStoreFactory
import ru.melnikov.gcalendar.data.store.SingleEventBookkeeperFactory
import ru.melnikov.gcalendar.data.store.SingleEventKey
import ru.melnikov.gcalendar.data.store.SingleEventStoreFactory
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday

@Module
class PlatformModule {
    @Single
    fun getLocalDatabase() = getDatabase()
}

expect fun getDatabase(): AppDatabase

@Module
@ComponentScan("ru.melnikov.gcalendar.data")
class DataModule {

    @Single
    fun json() = Json {
        prettyPrint = true
        isLenient = true
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Single
    fun httpClient(json: Json) = HttpClient {
        install(ContentNegotiation) {
            json(json, contentType = ContentType.Application.Json)
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30_000L
            connectTimeoutMillis = 15_000L
            socketTimeoutMillis = 30_000L
        }

        install(HttpRequestRetry) {
            maxRetries = 3
            retryIf { _, response ->
                !response.status.isSuccess() && response.status.value in 500..599
            }
            retryOnServerErrors(maxRetries = 3)
            exponentialDelay()
        }
    }

    @Single
    fun getUserEntityDao(appDatabase: AppDatabase): UserDao = appDatabase.getUserEntityDao()

    @Single
    fun getCalendarEntityDao(appDatabase: AppDatabase): CalendarDao =
        appDatabase.getCalendarEntityDao()

    @Single
    fun getEventEntityDao(appDatabase: AppDatabase): EventDao = appDatabase.getEventEntityDao()

    @Single
    fun getHolidayEntityDao(appDatabase: AppDatabase): HolidayDao =
        appDatabase.getHolidayEntityDao()

    @Single
    fun getSyncFailureDao(appDatabase: AppDatabase): SyncFailureDao =
        appDatabase.getSyncFailureDao()

    @Single
    fun provideHolidayStore(
        holidayApiService: HolidayApiService,
        holidayDao: HolidayDao
    ): Store<HolidayKey, List<Holiday>> =
        HolidayStoreFactory.create(holidayApiService, holidayDao)

    @Single
    @Named("eventBookkeeper")
    fun provideEventBookkeeper(
        syncFailureDao: SyncFailureDao
    ): Bookkeeper<EventKey> =
        EventBookkeeperFactory.create(syncFailureDao)

    @Single
    @Named("singleEventBookkeeper")
    fun provideSingleEventBookkeeper(
        syncFailureDao: SyncFailureDao
    ): Bookkeeper<SingleEventKey> =
        SingleEventBookkeeperFactory.create(syncFailureDao)

    @Single
    @Named("eventStore")
    fun provideEventStore(
        apiService: RemoteCalendarApiService,
        eventDao: EventDao,
        @Named("eventBookkeeper") bookkeeper: Bookkeeper<EventKey>
    ): MutableStore<EventKey, List<Event>> =
        EventStoreFactory.create(apiService, eventDao, bookkeeper)

    @Single
    @Named("singleEventStore")
    fun provideSingleEventStore(
        eventDao: EventDao,
        @Named("singleEventBookkeeper") bookkeeper: Bookkeeper<SingleEventKey>
    ): MutableStore<SingleEventKey, Event> =
        SingleEventStoreFactory.create(eventDao, bookkeeper)
}

@Module
@ComponentScan("ru.melnikov.gcalendar.ui")
class ViewModelModule

@Module
@ComponentScan("ru.melnikov.gcalendar.domain.repository")
class DomainModule

@Module
@ComponentScan("ru.melnikov.gcalendar.domain.usecase")
class UseCaseModule

@Module
@ComponentScan("ru.melnikov.gcalendar.domain.states")
class StateModule

@Module(includes = [PlatformModule::class, DataModule::class, ViewModelModule::class, DomainModule::class, UseCaseModule::class, StateModule::class])
class AppModule

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        modules(
            AppModule().module
        )
        config?.invoke(this)
    }
}