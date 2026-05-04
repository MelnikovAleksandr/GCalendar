package ru.melnikov.gcalendar.data.store

import org.mobilenativefoundation.store.store5.Bookkeeper
import ru.melnikov.gcalendar.common.AppLogger
import ru.melnikov.gcalendar.data.local.SyncFailureDao
import ru.melnikov.gcalendar.data.local.model.SyncFailureEntity

object KeyTypes {
    const val EVENT_KEY = "EVENT_KEY"
    const val SINGLE_EVENT_KEY = "SINGLE_EVENT_KEY"
}

object EventBookkeeperFactory {
    fun create(syncFailureDao: SyncFailureDao): Bookkeeper<EventKey> =
        Bookkeeper.by(
            getLastFailedSync = { key ->
                val keyString = serializeEventKey(key)
                val failure = syncFailureDao.getFailure(keyString)
                failure?.timestamp
            },
            setLastFailedSync = { key, timestamp ->
                val keyString = serializeEventKey(key)
                try {
                    val existing = syncFailureDao.getFailure(keyString)
                    if (existing != null) {
                        syncFailureDao.incrementFailureCount(keyString, timestamp, null)
                    } else {
                        syncFailureDao.insertFailure(
                            SyncFailureEntity(
                                key = keyString,
                                keyType = KeyTypes.EVENT_KEY,
                                timestamp = timestamp,
                                failureCount = 1,
                                lastErrorMessage = null,
                            ),
                        )
                    }
                    AppLogger.d { "Recorded sync failure for EventKey: $key" }
                    true
                } catch (e: Exception) {
                    AppLogger.e { "Failed to record sync failure: ${e.message}" }
                    false
                }
            },
            clear = { key ->
                val keyString = serializeEventKey(key)
                try {
                    syncFailureDao.deleteFailure(keyString)
                    AppLogger.d { "Cleared sync failure for EventKey: $key" }
                    true
                } catch (e: Exception) {
                    AppLogger.e { "Failed to clear sync failure: ${e.message}" }
                    false
                }
            },
            clearAll = {
                try {
                    syncFailureDao.deleteFailuresByType(KeyTypes.EVENT_KEY)
                    AppLogger.d { "Cleared all EventKey sync failures" }
                    true
                } catch (e: Exception) {
                    AppLogger.e { "Failed to clear all sync failures: ${e.message}" }
                    false
                }
            },
        )

    private fun serializeEventKey(key: EventKey): String =
        "${key.userId}:${key.startTime}:${key.endTime}"
}

object SingleEventBookkeeperFactory {
    fun create(syncFailureDao: SyncFailureDao): Bookkeeper<SingleEventKey> =
        Bookkeeper.by(
            getLastFailedSync = { key ->
                val failure = syncFailureDao.getFailure(key.eventId)
                failure?.timestamp
            },
            setLastFailedSync = { key, timestamp ->
                try {
                    val existing = syncFailureDao.getFailure(key.eventId)
                    if (existing != null) {
                        syncFailureDao.incrementFailureCount(key.eventId, timestamp, null)
                    } else {
                        syncFailureDao.insertFailure(
                            SyncFailureEntity(
                                key = key.eventId,
                                keyType = KeyTypes.SINGLE_EVENT_KEY,
                                timestamp = timestamp,
                                failureCount = 1,
                                lastErrorMessage = null,
                            ),
                        )
                    }
                    AppLogger.d { "Recorded sync failure for SingleEventKey: ${key.eventId}" }
                    true
                } catch (e: Exception) {
                    AppLogger.e { "Failed to record sync failure: ${e.message}" }
                    false
                }
            },
            clear = { key ->
                try {
                    syncFailureDao.deleteFailure(key.eventId)
                    AppLogger.d { "Cleared sync failure for SingleEventKey: ${key.eventId}" }
                    true
                } catch (e: Exception) {
                    AppLogger.e { "Failed to clear sync failure: ${e.message}" }
                    false
                }
            },
            clearAll = {
                try {
                    syncFailureDao.deleteFailuresByType(KeyTypes.SINGLE_EVENT_KEY)
                    AppLogger.d { "Cleared all SingleEventKey sync failures" }
                    true
                } catch (e: Exception) {
                    AppLogger.e { "Failed to clear all sync failures: ${e.message}" }
                    false
                }
            }
        )
}