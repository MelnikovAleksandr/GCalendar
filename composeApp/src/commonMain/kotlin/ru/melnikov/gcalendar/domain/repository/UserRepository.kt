package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.common.asUser
import ru.melnikov.gcalendar.common.asUserEntity
import ru.melnikov.gcalendar.data.local.UserDao
import ru.melnikov.gcalendar.domain.model.User

@Single
class UserRepository(private val userDao: UserDao) {

    suspend fun getUserFromApi() {
        val dummyUser = User(
            id = "user_id",
            name = "Demo User",
            email = "user@example.com",
            photoUrl = "https://t4.ftcdn.net/jpg/00/04/09/63/360_F_4096398_nMeewldssGd7guDmvmEDXqPJUmkDWyqA.jpg"
        )
        addUser(dummyUser)
    }
    fun getAllUsers(): Flow<List<User>> =
        userDao.getAllUsers().map { entities -> entities.map { it.asUser() } }

    suspend fun addUser(user: User) {
        userDao.insertUser(user.asUserEntity())
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user.asUserEntity())
    }
}