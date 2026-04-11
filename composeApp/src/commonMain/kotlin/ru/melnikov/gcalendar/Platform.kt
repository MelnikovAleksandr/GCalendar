package ru.melnikov.gcalendar

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform