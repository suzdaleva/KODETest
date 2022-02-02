package com.manicpixie.kodetest.presentation.main.components

import androidx.compose.ui.focus.FocusState
import com.manicpixie.kodetest.domain.util.UserOrder

sealed class MainScreenEvent{
    data class EnteredQuery(val value: String, val userOrder: UserOrder) : MainScreenEvent()
    data class Order(val userOrder: UserOrder): MainScreenEvent()
    data class ChangeQueryFocus(val focusState: FocusState) : MainScreenEvent()
    data class CancelSearch(val userOrder: UserOrder): MainScreenEvent()
}
