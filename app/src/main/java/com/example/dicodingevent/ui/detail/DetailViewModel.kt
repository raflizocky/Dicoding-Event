package com.example.dicodingevent.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingevent.data.database.FavoriteEvent
import com.example.dicodingevent.data.retrofit.ApiConfig
import com.example.dicodingevent.data.response.DetailEventResponse
import com.example.dicodingevent.data.response.Event
import com.example.dicodingevent.repository.FavoriteEventRepository
import kotlinx.coroutines.launch
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
        _isLoading.value = true
        ApiConfig.getApiService().getDetailEvent(eventId).enqueue(object :
            Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _event.value = response.body()?.event
                    checkFavoriteStatus(eventId)
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "Network error: ${t.message}"
            }
        })
    }

    private fun checkFavoriteStatus(eventId: String) {
        viewModelScope.launch {
            val favoriteEvent = repository.getFavoriteEventById(eventId).value
            _isFavorite.value = favoriteEvent != null
        }
    }

    fun toggleFavorite() {
        val currentEvent = _event.value ?: return
        val isFavorite = _isFavorite.value ?: false

        viewModelScope.launch {
            if (isFavorite) {
                currentEvent.name?.let {
                    FavoriteEvent(currentEvent.id.toString(),
                        it, currentEvent.mediaCover)
                }?.let { repository.delete(it) }
            } else {
                currentEvent.name?.let {
                    FavoriteEvent(currentEvent.id.toString(),
                        it, currentEvent.mediaCover)
                }?.let { repository.insert(it) }
            }
            _isFavorite.value = !isFavorite
        }
    }
}