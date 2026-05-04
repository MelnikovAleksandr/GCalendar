package ru.melnikov.gcalendar.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_failures")
data class SyncFailureEntity(
    @PrimaryKey
    val key: String,
    val keyType: String,
    val timestamp: Long,
    val failureCount: Int = 1,
    val lastErrorMessage: String? = null
)