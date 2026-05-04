package ru.melnikov.gcalendar.common.model

import ru.melnikov.gcalendar.data.local.model.UserEntity
import ru.melnikov.gcalendar.domain.model.User

fun UserEntity.asUser(): User =
    User(id, name, email, photoUrl)

fun User.asUserEntity(): UserEntity =
    UserEntity(id, name, email, photoUrl)