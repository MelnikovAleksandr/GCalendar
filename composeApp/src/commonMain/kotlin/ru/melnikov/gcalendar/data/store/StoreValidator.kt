package ru.melnikov.gcalendar.data.store

import org.mobilenativefoundation.store.store5.Validator
import ru.melnikov.gcalendar.common.AppLogger
import ru.melnikov.gcalendar.domain.model.Event
import ru.melnikov.gcalendar.domain.model.Holiday

object CacheDuration {
    const val HOLIDAYS_CACHE_HOURS = 24L
    const val EVENTS_CACHE_HOURS = 1L
    fun hoursToMillis(hours: Long): Long = hours * 60 * 60 * 1000
}

object CacheTimestampTracker {
    private val timestamps = mutableMapOf<String, Long>()

    @OptIn(kotlin.time.ExperimentalTime::class)
    private fun currentTimeMillis(): Long =
        kotlin.time.Clock.System.now().toEpochMilliseconds()

    fun recordFetch(key: String) {
        timestamps[key] = currentTimeMillis()
    }

    fun isExpired(key: String, maxAgeMillis: Long): Boolean {
        val lastFetch = timestamps[key] ?: return true
        return (currentTimeMillis() - lastFetch) > maxAgeMillis
    }

    fun clear(key: String) {
        timestamps.remove(key)
    }

    fun clearAll() {
        timestamps.clear()
    }
}

object HolidayValidator {

    private val maxAgeMillis = CacheDuration.hoursToMillis(CacheDuration.HOLIDAYS_CACHE_HOURS)

    fun create(): Validator<List<Holiday>> = Validator.by { holidays ->
        if (holidays.isEmpty()) {
            AppLogger.d { "Holiday cache is empty, needs refresh" }
            false
        } else {
            AppLogger.d { "Holiday cache has ${holidays.size} items, considered valid" }
            true
        }
    }

    fun isStale(key: HolidayKey): Boolean {
        val cacheKey = "holiday:${key.countryCode}:${key.year}"
        return CacheTimestampTracker.isExpired(cacheKey, maxAgeMillis)
    }

    fun recordFetch(key: HolidayKey) {
        val cacheKey = "holiday:${key.countryCode}:${key.year}"
        CacheTimestampTracker.recordFetch(cacheKey)
    }
}


object EventValidator {

    private val maxAgeMillis = CacheDuration.hoursToMillis(CacheDuration.EVENTS_CACHE_HOURS)

    fun create(): Validator<List<Event>> = Validator.by { events ->
        AppLogger.d { "Event cache has ${events.size} items" }
        true
    }

    fun isStale(key: EventKey): Boolean {
        val cacheKey = "event:${key.userId}:${key.startTime}:${key.endTime}"
        return CacheTimestampTracker.isExpired(cacheKey, maxAgeMillis)
    }

    fun recordFetch(key: EventKey) {
        val cacheKey = "event:${key.userId}:${key.startTime}:${key.endTime}"
        CacheTimestampTracker.recordFetch(cacheKey)
    }

    fun invalidate(key: EventKey) {
        val cacheKey = "event:${key.userId}:${key.startTime}:${key.endTime}"
        CacheTimestampTracker.clear(cacheKey)
    }
}