package ru.melnikov.gcalendar.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holidays")
data class HolidayEntity(
    @PrimaryKey val id: String,
    val name: String,
    val date: Long,
    val countryCode: String
)