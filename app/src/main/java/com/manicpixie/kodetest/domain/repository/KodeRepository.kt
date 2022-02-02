package com.manicpixie.kodetest.domain.repository

import com.manicpixie.kodetest.data.remote.dto.UserDto

interface KodeRepository {
    suspend fun getUsers() : List<UserDto>
}