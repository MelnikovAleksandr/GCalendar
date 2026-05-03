package ru.melnikov.gcalendar.data.remote.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.melnikov.gcalendar.domain.model.Calendar

@Serializable
data class CalendarResponseItem(
    @SerialName("color")
    val color: Int,
    @SerialName("id")
    val id: String,
    @SerialName("isPrimary")
    val isPrimary: Boolean,
    @SerialName("isVisible")
    val isVisible: Boolean,
    @SerialName("name")
    val name: String,
    @SerialName("userId")
    val userId: String
)

fun CalendarResponseItem.asCalendar(): Calendar {
    return Calendar(
        id = id,
        name = name,
        color = color,
        isVisible = isVisible,
        isPrimary = isPrimary,
        userId = userId
    )
}