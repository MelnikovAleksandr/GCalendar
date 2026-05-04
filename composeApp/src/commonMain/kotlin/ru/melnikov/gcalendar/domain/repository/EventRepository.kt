@file:OptIn(ExperimentalStoreApi::class)

package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import org.mobilenativefoundation.store.core5.ExperimentalStoreApi
import org.mobilenativefoundation.store.store5.MutableStore
import org.mobilenativefoundation.store.store5.StoreReadRequest
import org.mobilenativefoundation.store.store5.StoreReadResponse
import org.mobilenativefoundation.store.store5.StoreWriteRequest
import ru.melnikov.gcalendar.common.model.asEntity
import ru.melnikov.gcalendar.data.local.EventDao
import ru.melnikov.gcalendar.data.store.EventKey
import ru.melnikov.gcalendar.data.store.SingleEventKey
import ru.melnikov.gcalendar.domain.model.Event

@Single(binds = [IEventRepository::class])
class EventRepository(
    @Named("eventStore") private val eventStore: MutableStore<EventKey, List<Event>>,
    @Named("singleEventStore") private val singleEventStore: MutableStore<SingleEventKey, Event>,
    private val eventDao: EventDao,
) : BaseRepository(),
    IEventRepository {

    override suspend fun syncEventsForCalendar(
        calendarIds: List<String>,
        startTime: Long,
        endTime: Long,
    ): Unit =
        safeCallOrThrow("syncEventsForCalendar(range=$startTime-$endTime)") {
            val key = EventKey(userId = "", startTime = startTime, endTime = endTime)
            eventStore
                .stream<Unit>(StoreReadRequest.fresh(key))
                .filterIsInstance<StoreReadResponse.Data<List<Event>>>()
                .first()
        }

    override fun getEventsForCalendarsInRange(
        userId: String,
        start: Long,
        end: Long,
    ): Flow<List<Event>> {
        val key = EventKey(userId = userId, startTime = start, endTime = end)

        return safeFlow(
            flowName = "getEventsForCalendarsInRange",
            defaultValue = emptyList(),
            flow =
                eventStore
                    .stream<Unit>(StoreReadRequest.cached(key, refresh = false))
                    .filterIsInstance<StoreReadResponse.Data<List<Event>>>()
                    .map { it.value },
        )
    }

    override suspend fun addEvent(event: Event): Unit =
        safeCallOrThrow("addEvent(${event.id})") {
            val key = SingleEventKey(event.id)
            singleEventStore.write(StoreWriteRequest.of(key, event))
        }

    override suspend fun updateEvent(event: Event): Unit =
        safeCallOrThrow("updateEvent(${event.id})") {
            val key = SingleEventKey(event.id)
            singleEventStore.write(StoreWriteRequest.of(key, event))
        }


    override suspend fun deleteEvent(event: Event): Unit =
        safeCallOrThrow(
            "deleteEvent(${event.id})",
        ) {
            eventDao.deleteEventReminders(event.id)

            eventDao.deleteEvent(event.asEntity())

            val key = SingleEventKey(event.id)
            singleEventStore.clear(key)
        }
}