package ru.melnikov.gcalendar.data.local

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import ru.melnikov.gcalendar.data.model.CalendarEntity
import ru.melnikov.gcalendar.data.model.EventEntity
import ru.melnikov.gcalendar.data.model.EventReminderEntity
import ru.melnikov.gcalendar.data.model.HolidayEntity
import ru.melnikov.gcalendar.data.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CalendarEntity::class,
        EventEntity::class,
        EventReminderEntity::class,
        HolidayEntity::class],
    version = 1
)
@ConstructedBy(LocalDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserEntityDao(): UserDao
    abstract fun getCalendarEntityDao(): CalendarDao
    abstract fun getEventEntityDao(): EventDao
    abstract fun getHolidayEntityDao(): HolidayDao

}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object LocalDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}