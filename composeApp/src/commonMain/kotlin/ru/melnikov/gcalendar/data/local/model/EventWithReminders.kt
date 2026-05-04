package ru.melnikov.gcalendar.data.local.model

import androidx.room.Embedded
import androidx.room.Relation

data class EventWithReminders(
    @Embedded val event: EventEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "eventId"
    )
    val reminders: List<EventReminderEntity>
)