package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.melnikov.gcalendar.domain.model.User

interface IUserRepository {
    suspend fun getUserFromApi()

    fun getAllUsers(): Flow<List<User>>

    suspend fun addUser(user: User)

    suspend fun deleteUser(user: User)
}