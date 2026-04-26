package ru.melnikov.gcalendar.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("holidays")
    val holidays: List<Holiday>
)