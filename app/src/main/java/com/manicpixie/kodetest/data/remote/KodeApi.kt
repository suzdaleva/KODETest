package com.manicpixie.kodetest.data.remote

import com.manicpixie.kodetest.data.remote.dto.UserDto
import retrofit2.http.GET

interface KodeApi {
    @GET("S0HL")
    suspend fun getCoins() : List<UserDto>
}