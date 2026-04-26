package ru.melnikov.gcalendar.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.melnikov.gcalendar.data.local.model.HolidayEntity

@Dao
interface HolidayDao {

    @Query("SELECT * FROM holidays WHERE date >= :startDate AND date <= :endDate")
    fun getHolidaysInRange(startDate: Long, endDate: Long): Flow<List<HolidayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolidays(holidays: List<HolidayEntity>)
}