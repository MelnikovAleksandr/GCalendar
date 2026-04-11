package ru.melnikov.gcalendar.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import ru.melnikov.gcalendar.data.model.CalendarEntity

@Dao
interface CalendarDao {

    @Query("SELECT * FROM calendars WHERE userId = :userId")
    fun getCalendarsByUserId(userId: String): Flow<List<CalendarEntity>>

    @Upsert
    suspend fun upsertCalendar(calendar: CalendarEntity)

    @Delete
    suspend fun deleteCalendar(calendar: CalendarEntity)
}