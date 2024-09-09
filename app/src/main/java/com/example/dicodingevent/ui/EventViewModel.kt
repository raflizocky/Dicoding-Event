package com.example.dicodingevent.ui

import androidx.lifecycle.*
import com.example.dicodingevent.data.UiState
import com.example.dicodingevent.data.response.ListEventsItem
import com.example.dicodingevent.data.retrofit.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class EventViewModel(private val apiService: ApiService) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<List<ListEventsItem>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ListEventsItem>>> = _uiState.asStateFlow()

    private val _networkState = MutableStateFlow(true)
    val networkState: StateFlow<Boolean> = _networkState.asStateFlow()

    fun setNetworkState(isConnected: Boolean) {
        _networkState.value = isConnected
    }

    fun fetchEvents(active: Int) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val response = apiService.getEvents(active)
                if (response.listEvents.isEmpty()) {
                    _uiState.value = UiState.Error("No events found")
                } else {
                    _uiState.value = UiState.Success(response.listEvents)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    when (e) {
                        is HttpException -> "Error: ${e.code()}"
                        else -> "Network error: ${e.message}"
                    }
                )
            }
        }
    }
}
