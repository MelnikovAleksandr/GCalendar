package ru.melnikov.gcalendar.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Timezone(
    @SerialName("offset")
    val offset: String,
    @SerialName("zoneabb")
    val zoneAbb: String,
    @SerialName("zonedst")
    val zonedst: Int,
    @SerialName("zoneoffset")
    val zoneOffset: Int,
    @SerialName("zonetotaloffset")
    val zoneTotalOffset: Int
)