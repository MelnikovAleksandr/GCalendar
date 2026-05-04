package ru.melnikov.gcalendar.common.model

import ru.melnikov.gcalendar.common.parseDateTime
import ru.melnikov.gcalendar.data.local.model.HolidayEntity
import ru.melnikov.gcalendar.data.remote.model.HolidayItem
import ru.melnikov.gcalendar.domain.model.Holiday

fun HolidayItem.asHoliday(): Holiday =
    Holiday(
        id = urlId,
        name = name,
        date = parseDateTime(date.iso),
        countryCode = country.id
    )

fun HolidayItem.asHolidayEntity(): HolidayEntity =
    HolidayEntity(
        id = urlId,
        name = name,
        date = parseDateTime(date.iso),
        countryCode = country.id
    )

fun HolidayEntity.asHoliday(): Holiday =
    Holiday(id, name, date, countryCode)

fun Holiday.asHolidayEntity(): HolidayEntity =
    HolidayEntity(id, name, date, countryCode)