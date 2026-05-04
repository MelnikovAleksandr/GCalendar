package ru.melnikov.gcalendar.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.migration.Migration
import ru.melnikov.gcalendar.data.local.model.CalendarEntity
import ru.melnikov.gcalendar.data.local.model.EventEntity
import ru.melnikov.gcalendar.data.local.model.EventReminderEntity
import ru.melnikov.gcalendar.data.local.model.HolidayEntity
import ru.melnikov.gcalendar.data.local.model.SyncFailureEntity
import ru.melnikov.gcalendar.data.local.model.UserEntity

const val DATABASE_VERSION = 2

const val DATABASE_NAME = "calendar.db"

@Database(
    entities = [
        UserEntity::class,
        CalendarEntity::class,
        EventEntity::class,
        EventReminderEntity::class,
        HolidayEntity::class,
        SyncFailureEntity::class,
    ],
    version = DATABASE_VERSION,
    exportSchema = true,
)
@ConstructedBy(LocalDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserEntityDao(): UserDao

    abstract fun getCalendarEntityDao(): CalendarDao

    abstract fun getEventEntityDao(): EventDao

    abstract fun getHolidayEntityDao(): HolidayDao

    abstract fun getSyncFailureDao(): SyncFailureDao

    companion object {
        val MIGRATIONS: Array<Migration> = arrayOf()
    }
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LocalDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}