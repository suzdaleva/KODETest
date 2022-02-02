package com.manicpixie.kodetest.data.remote.dto

import com.manicpixie.kodetest.domain.model.User



data class UserDto(
    val avatarUrl: String,
    val birthday: String,
    val department: String,
    val firstName: String,
    val id: String,
    val lastName: String,
    val phone: String,
    val position: String,
    val userTag: String
)
    fun UserDto.toUser(): User {
        return User(
            avatarUrl = avatarUrl,
            birthday = birthday,
            department = department,
            firstName = firstName,
            id = id,
            lastName = lastName,
            phone = phone,
            position = position,
            userTag = userTag
        )
    }
