package ru.melnikov.gcalendar.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventResponseItem(
    @SerialName("calendarId")
    val calendarId: String,
    @SerialName("calendarName")
    val calendarName: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("endTime")
    val endTime: Long,
    @SerialName("id")
    val id: String,
    @SerialName("isAllDay")
    val isAllDay: Boolean,
    @SerialName("isRecurring")
    val isRecurring: Boolean,
    @SerialName("location")
    val location: String? = null,
    @SerialName("recurringRule")
    val recurringRule: String? = null,
    @SerialName("reminderMinutes")
    val reminderMinutes: List<Int>,
    @SerialName("startTime")
    val startTime: Long,
    @SerialName("title")
    val title: String
)