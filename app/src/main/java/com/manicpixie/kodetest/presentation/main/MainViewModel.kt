package com.manicpixie.kodetest.presentation.main

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manicpixie.kodetest.common.Resource
import com.manicpixie.kodetest.domain.use_case.get_users.GetUsers
import com.manicpixie.kodetest.domain.util.UserOrder
import com.manicpixie.kodetest.presentation.main.components.MainScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    val getUsersUseCase: GetUsers,
    val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _searchQuery = mutableStateOf(
        SearchFieldState(
            hint = "Введите имя, тег, почту..."
        )
    )
    val searchQuery: State<SearchFieldState> = _searchQuery
    private val _contentState = mutableStateOf(UserListState())
    val contentState : State<UserListState> = _contentState

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val _isRefreshing = MutableStateFlow(false)

    val isRefreshing: StateFlow<Boolean>
        get() = _isRefreshing.asStateFlow()

     private fun getUsers(userOrder: UserOrder) {
        getUsersUseCase(userOrder).onEach {result ->
            when(result) {
                is Resource.Success -> {
                    _contentState.value = UserListState(
                        users = result.data ?: emptyList(),
                        userOrder = userOrder)
                }
                is Resource.Error -> {
                    viewModelScope.launch {
                        _contentState.value =
                            UserListState(error = result.message ?: "Unknown error")
                    }
                }
                is Resource.Loading -> {
                    _contentState.value = UserListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }

    init {
        getUsers(UserOrder.Alphabetically)
    }

    sealed class UiEvent {
        data class ShowErrorSnackBar(val message: String) : UiEvent()
        data class ShowLoadingSnackBar(val message: String) : UiEvent()
        object CancelSnackBar: UiEvent()
    }



    fun refresh(userOrder: UserOrder) {

        getUsersUseCase(userOrder).onEach { result ->
            _isRefreshing.emit(true)
            when(result) {
                is Resource.Success -> {
                    searchUsers(searchQuery.value.query, userOrder)
                    //_eventFlow.emit(UiEvent.ShowLoadingSnackBar("Секундочку, гружусь..."))
                    _isRefreshing.emit(false)
                    _eventFlow.emit(UiEvent.CancelSnackBar)
                }
                is Resource.Error -> {
                    _eventFlow.emit(UiEvent.ShowErrorSnackBar("Hе могу обновить данные.\n" +
                            "Проверь соединение с интернетом"))
                    delay(2000)
                    _isRefreshing.emit(false)
                }
                is Resource.Loading -> {
                    _eventFlow.emit(UiEvent.ShowLoadingSnackBar("..."))
                }
            }

        }.launchIn(viewModelScope)
    }


    private fun searchUsers(query: String, userOrder: UserOrder) {
        getUsersUseCase(userOrder).onEach { result ->
            when(result) {
                is Resource.Success -> {
                    _contentState.value = UserListState(
                        users = result.data!!.filter { it.lastName.lowercase().startsWith(query) || it.firstName.lowercase().startsWith(query) || it.userTag.lowercase().startsWith(query)} ?: emptyList(),
                        userOrder = userOrder)
                }
                is Resource.Error -> {
                    _contentState.value = UserListState(error = result.message ?: "Unknown error")
                }
                is Resource.Loading -> {
                    _contentState.value = UserListState(isLoading = true)
                }
            }

        }.launchIn(viewModelScope)
    }


    fun onEvent(event: MainScreenEvent) {
        when(event) {
            is MainScreenEvent.EnteredQuery -> {
                _searchQuery.value = _searchQuery.value.copy(
                   query = event.value
                )
                searchUsers(searchQuery.value.query, event.userOrder)
            }
            is MainScreenEvent.ChangeQueryFocus -> {
                _searchQuery.value = _searchQuery.value.copy(
                    isHintVisible = !event.focusState.isFocused && _searchQuery.value.query.isBlank(),
                    tint = if(!event.focusState.isFocused) Color(0xFFC3C3C6) else Color(0xFF050510)
                )
            }
            is MainScreenEvent.Order -> {
                if(contentState.value.userOrder::class == event.userOrder::class) return
                getUsers(event.userOrder)

            }
            is MainScreenEvent.CancelSearch -> {
                _searchQuery.value = _searchQuery.value.copy(
                    isHintVisible = true,
                    query = "",
                    tint = Color(0xFFC3C3C6)
                )
                getUsers(event.userOrder)
            }
        }
    }
}