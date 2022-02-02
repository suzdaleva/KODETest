package com.manicpixie.kodetest.presentation.main

import androidx.compose.ui.graphics.Color

data class SearchFieldState(
    val query : String = "",
    val hint: String = "",
    val isHintVisible : Boolean = true,
    val tint: Color = Color(0xFFC3C3C6)
)
