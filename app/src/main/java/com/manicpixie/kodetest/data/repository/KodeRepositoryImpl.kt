package com.manicpixie.kodetest.data.repository

import com.manicpixie.kodetest.data.remote.KodeApi
import com.manicpixie.kodetest.data.remote.dto.UserDto
import com.manicpixie.kodetest.domain.repository.KodeRepository
import javax.inject.Inject

class KodeRepositoryImpl @Inject constructor(
    private val api: KodeApi
): KodeRepository {
    override suspend fun getUsers(): List<UserDto> {
        return api.getCoins()
    }
}