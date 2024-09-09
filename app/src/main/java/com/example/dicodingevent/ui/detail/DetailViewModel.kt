package com.example.dicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.repository.FavoriteEventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.*

class DetailViewModel(private val repository: FavoriteEventRepository) : ViewModel() {
    private val _event = MutableLiveData<Event?>()
    val event: LiveData<Event?> = _event

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun fetchEventDetails(eventId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = withContext(Dispatchers.IO) {
                    ApiConfig.getApiService().getDetailEvent(eventId).await()
                }
                _isLoading.postValue(false)
                _event.postValue(result.event)
                checkFavoriteStatus(eventId)
            } catch (e: Exception) {
                _isLoading.postValue(false)
                when (e) {
                    is HttpException -> {
                        _errorMessage.postValue("Error: ${e.code()}")
                    }
                    else -> {
                        _errorMessage.postValue("Network error: ${e.message}")
                    }
                }
            }
        }
    }

    private fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            repository.getFavoriteEventById(eventId).collect { favoriteEvent ->
                _isFavorite.postValue(favoriteEvent != null)
            }
        }
    }

    fun toggleFavorite() {
        val currentEvent = _event.value ?: return
        val currentFavoriteStatus = _isFavorite.value ?: false

        viewModelScope.launch {
            if (currentFavoriteStatus) {
                repository.getFavoriteEventById(currentEvent.id.toString()).collect { favoriteEvent ->
                    favoriteEvent?.let {
                        repository.delete(it)
                    }
                }
            } else {
                val favoriteEvent = FavoriteEvent(
                    id = currentEvent.id.toString(),
                    name = currentEvent.name ?: "",
                    mediaCover = currentEvent.mediaCover
                )
                repository.insert(favoriteEvent)
            }
            // The favorite status will be updated automatically via the Flow in checkFavoriteStatus
        }
    }
}