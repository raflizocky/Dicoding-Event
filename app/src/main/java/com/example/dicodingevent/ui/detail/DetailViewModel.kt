package com.example.dicodingevent.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.UiState
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.repository.FavoriteEventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel(private val repository: FavoriteEventRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Event?>>(UiState.Loading)
    val uiState: StateFlow<UiState<Event?>> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun fetchEventDetails(eventId: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val result = ApiConfig.apiService.getDetailEvent(eventId)
                if (result.event != null) {
                    _uiState.value = UiState.Success(result.event)
                    checkFavoriteStatus(eventId)
                } else {
                    _uiState.value = UiState.Error("Event details not found")
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(
                    when (e) {
                        is HttpException -> "Error: ${e.code()}"
                        else -> "No internet connection"
                    }
                )
            }
        }
    }

    private fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            _isFavorite.value = repository.getFavoriteEventById(eventId).firstOrNull() != null
        }
    }

    fun toggleFavorite() {
        val currentState = _uiState.value
        if (currentState !is UiState.Success || currentState.data == null) return
        val currentEvent = currentState.data
        val currentFavoriteStatus = _isFavorite.value

        viewModelScope.launch {
            if (currentFavoriteStatus) {
                repository.delete(FavoriteEvent(
                    id = currentEvent.id.toString(),
                    name = currentEvent.name ?: "",
                    mediaCover = currentEvent.mediaCover
                ))
            } else {
                repository.insert(FavoriteEvent(
                    id = currentEvent.id.toString(),
                    name = currentEvent.name ?: "",
                    mediaCover = currentEvent.mediaCover
                ))
            }
            _isFavorite.value = !currentFavoriteStatus
        }
    }
}