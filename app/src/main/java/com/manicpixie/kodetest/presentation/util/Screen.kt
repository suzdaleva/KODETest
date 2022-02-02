package com.manicpixie.kodetest.presentation.util

sealed class Screen(val route: String) {
    object MainScreen: Screen("main_screen")
    object ProfileScreen: Screen("profile_screen/{user}")
}
