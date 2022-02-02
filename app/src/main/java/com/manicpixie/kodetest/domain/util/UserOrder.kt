package com.manicpixie.kodetest.domain.util

sealed class UserOrder {
    object Alphabetically : UserOrder()
    object Birthday : UserOrder()

}