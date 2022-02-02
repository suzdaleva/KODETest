package com.manicpixie.kodetest.presentation.main

import com.manicpixie.kodetest.domain.model.User
import com.manicpixie.kodetest.domain.util.UserOrder

data class UserListState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String = "",
    val userOrder: UserOrder = UserOrder.Alphabetically
)
