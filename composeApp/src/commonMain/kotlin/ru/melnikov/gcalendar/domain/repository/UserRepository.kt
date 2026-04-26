package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.data.local.UserDao
import ru.melnikov.gcalendar.data.local.model.UserEntity
import ru.melnikov.gcalendar.domain.model.User

@Single
class UserRepository(private val userDao: UserDao) {
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities -> entities.map { it.toUser() } }

    suspend fun addUser(user: User) {
        userDao.insertUser(user.toEntity())
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.toEntity())
    }

    private fun UserEntity.toUser(): User =
        User(id, name, email, photoUrl)

    private fun User.toEntity(): UserEntity =
        UserEntity(id, name, email, photoUrl)
}