package ru.melnikov.gcalendar.domain.model

import androidx.compose.runtime.Stable

@Stable
data class Holiday(
    val id: String,
    val name: String,
    val date: Long,
    val countryCode: String
)