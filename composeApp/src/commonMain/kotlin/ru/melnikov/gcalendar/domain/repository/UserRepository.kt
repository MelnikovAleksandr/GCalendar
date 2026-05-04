package ru.melnikov.gcalendar.domain.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single
import ru.melnikov.gcalendar.common.model.asUser
import ru.melnikov.gcalendar.common.model.asUserEntity
import ru.melnikov.gcalendar.data.local.UserDao
import ru.melnikov.gcalendar.domain.model.User

@Single(binds = [IUserRepository::class])
class UserRepository(
    private val userDao: UserDao,
) : BaseRepository(), IUserRepository {

    override suspend fun getUserFromApi() = safeCallOrThrow("getUserFromApi") {
        val dummyUser = User(
            id = "user_id",
            name = "Demo User",
            email = "user@example.com",
            photoUrl = "https://t4.ftcdn.net/jpg/00/04/09/63/360_F_4096398_nMeewldssGd7guDmvmEDXqPJUmkDWyqA.jpg",
        )
        addUser(dummyUser)
    }

    override fun getAllUsers(): Flow<List<User>> =
        safeFlow(
            flowName = "getAllUsers",
            defaultValue = emptyList(),
            flow = userDao.getAllUsers().map { entities -> entities.map { it.asUser() } }
        )

    override suspend fun addUser(user: User) = safeCallOrThrow("addUser(${user.id})") {
        userDao.insertUser(user.asUserEntity())
    }

    override suspend fun deleteUser(user: User) = safeCallOrThrow("deleteUser(${user.id})") {
        userDao.deleteUser(user.asUserEntity())
    }
}