package ru.melnikov.gcalendar.domain.model

data class Holiday(
    val id: String,
    val name: String,
    val date: Long,
    val countryCode: String
)