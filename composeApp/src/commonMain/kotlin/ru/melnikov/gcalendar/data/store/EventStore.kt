@file:OptIn(ExperimentalStoreApi::class)

package ru.melnikov.gcalendar.data.store

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.Bookkeeper
import org.mobilenativefoundation.store.store5.Converter
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.MutableStoreBuilder
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.Updater
import org.mobilenativefoundation.store.store5.UpdaterResult
import ru.melnikov.gcalendar.common.AppLogger
import ru.melnikov.gcalendar.common.model.asEntity
import ru.melnikov.gcalendar.common.model.asEvent
import ru.melnikov.gcalendar.data.local.EventDao
import ru.melnikov.gcalendar.data.local.model.EventReminderEntity
import ru.melnikov.gcalendar.data.remote.RemoteCalendarApiService
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.data.remote.Result

object EventStoreFactory {

    fun create(
        apiService: RemoteCalendarApiService,
        eventDao: EventDao,
        bookkeeper: Bookkeeper<EventKey>
    ): MutableStore<EventKey, List<Event>> {
        return MutableStoreBuilder.from(
            fetcher = createFetcher(apiService),
            sourceOfTruth = createSourceOfTruth(eventDao),
            converter = createEventListConverter()
        )
            .validator(EventValidator.create())
            .build(
                updater = createUpdater(apiService),
                bookkeeper = bookkeeper
            )
    }

    private fun createEventListConverter(): Converter<List<Event>, List<Event>, List<Event>> =
        Converter.Builder<List<Event>, List<Event>, List<Event>>()
            .fromNetworkToLocal { it }
            .fromOutputToLocal { it }
            .build()

    private fun createFetcher(
        apiService: RemoteCalendarApiService
    ): Fetcher<EventKey, List<Event>> = Fetcher.of { key ->
        AppLogger.d { "Fetching events for user ${key.userId}, range ${key.startTime}-${key.endTime}" }
        when (val response = apiService.fetchEventsForCalendar(
            calendarIds = emptyList(),
            startTime = key.startTime,
            endTime = key.endTime
        )) {
            is Result.Error -> {
                AppLogger.e { "Failed to fetch events: ${response.error}" }
                throw StoreException("Failed to fetch events: ${response.error}")
            }
            is Result.Success -> {
                AppLogger.d { "Fetched ${response.data.size} events" }
                EventValidator.recordFetch(key)
                response.data.map { it.asEvent() }
            }
        }
    }

    private fun createSourceOfTruth(
        eventDao: EventDao
    ): SourceOfTruth<EventKey, List<Event>, List<Event>> = SourceOfTruth.of(
        reader = { key ->
            eventDao.getEventsWithRemindersBetweenDates(key.userId, key.startTime, key.endTime)
                .map { eventsWithReminders ->
                    eventsWithReminders.map { it.asEvent() }
                }
        },
        writer = { _, events ->
            events.forEach { event ->
                val eventEntity = event.asEntity()
                val reminderEntities = event.reminderMinutes.map { minutes ->
                    EventReminderEntity(event.id, minutes)
                }
                eventDao.insertEventWithReminders(eventEntity, reminderEntities)
            }
        },
        delete = { key ->
            AppLogger.d { "Delete called for key: $key (no-op for range queries)" }
        },
        deleteAll = {
            AppLogger.d { "DeleteAll called (no-op - preserving local data)" }
        }
    )

    @Suppress("UNUSED_PARAMETER")
    private fun createUpdater(
        apiService: RemoteCalendarApiService
    ): Updater<EventKey, List<Event>, Unit> = Updater.by(
        post = { key, events ->
            AppLogger.d { "Updater: ${events.size} events saved locally (offline-first mode)" }
            UpdaterResult.Success.Typed(Unit)
        }
    )
}


object SingleEventStoreFactory {

    fun create(
        eventDao: EventDao,
        bookkeeper: Bookkeeper<SingleEventKey>
    ): MutableStore<SingleEventKey, Event> {
        return MutableStoreBuilder.from(
            fetcher = createFetcher(eventDao),
            sourceOfTruth = createSourceOfTruth(eventDao),
            converter = createSingleEventConverter()
        )
            .build(
                updater = createUpdater(),
                bookkeeper = bookkeeper
            )
    }

    private fun createSingleEventConverter(): Converter<Event, Event, Event> =
        Converter.Builder<Event, Event, Event>()
            .fromNetworkToLocal { it }
            .fromOutputToLocal { it }
            .build()

    private fun createFetcher(
        eventDao: EventDao
    ): Fetcher<SingleEventKey, Event> = Fetcher.of { key ->
        AppLogger.d { "Fetching single event: ${key.eventId}" }
        val entity = eventDao.getEventById(key.eventId)
            ?: throw StoreException("Event not found: ${key.eventId}")
        entity.asEvent()
    }

    private fun createSourceOfTruth(
        eventDao: EventDao
    ): SourceOfTruth<SingleEventKey, Event, Event> = SourceOfTruth.of(
        reader = { key ->
            flow {
                val entity = eventDao.getEventById(key.eventId)
                if (entity != null) {
                    emit(entity.asEvent())
                }
            }
        },
        writer = { _, event ->
            val eventEntity = event.asEntity()
            val reminderEntities = event.reminderMinutes.map { minutes ->
                EventReminderEntity(event.id, minutes)
            }
            eventDao.insertEventWithReminders(eventEntity, reminderEntities)
        },
        delete = { key ->
            eventDao.getEventById(key.eventId)?.let { entity ->
                eventDao.deleteEvent(entity)
            }
        },
        deleteAll = {
            AppLogger.d { "DeleteAll called on SingleEventStore (no-op)" }
        }
    )

    private fun createUpdater(): Updater<SingleEventKey, Event, Unit> = Updater.by(
        post = { key, _ ->
            AppLogger.d { "Updater: Event ${key.eventId} saved locally (offline-first mode)" }
            UpdaterResult.Success.Typed(Unit)
        }
    )
}